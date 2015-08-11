package ALBasicServer.ALServerSynTask;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicCommon.ALBasicCommonFun;
import ALBasicServer.ALBasicServerConf;
import ALBasicServer.ALTask._IALSynTask;

/**********************
 * 架构中的同步任务管理对象，对象中根据不同的任务安排方式进行处理<br>
 * 
 * @author alzq
 *
 */
public class ALSynTaskManager
{
    private static ALSynTaskManager g_instance = new ALSynTaskManager();
    
    public static ALSynTaskManager getInstance()
    {
        if(null == g_instance)
            g_instance = new ALSynTaskManager();
        
        return g_instance;
    }
    
    /** 当前任务队列由于此队列只从队列头抽取对象，并且仅在队列尾插入对象 */
    private LinkedList<_IALSynTask> _m_lCurrentTaskList;
    /** 当前任务队列操作锁 */
    private ReentrantLock _m_lCurrentTaskMutex;

    /** 定时任务的时间精度 */
    private int _m_iTimingTaskCheckTime;
    /** 定时任务的大区间长度，根据精度和长度可以决定一个区间的时间跨度 */
    private int _m_iTimingTaskCheckAreaSize;
    /** 定时任务开启处理的时间标记 */
    private long _m_lTimingTaskMgrStartTime;
    
    /** 最后一次检测的回合数和对应下标 */
    private int _m_iLastCheckRound;
    private int _m_iLastCheckTick;

    /** 记录固定队列长度的定时任务存储队列，用数组便于根据下标查询 */
    private ALSynTimingTaskNode[] _m_arrTimingTaskNodeList;
    
    /** 定时任务队列锁 */
    private ReentrantLock _m_lTimingTaskMutex;
    
    /** 任务对应的同步信号量 */
    private Semaphore _m_sTaskEvent;
    
    protected ALSynTaskManager()
    {
        _m_lCurrentTaskList = new LinkedList<_IALSynTask>();
        _m_lCurrentTaskMutex = new ReentrantLock();
        
        //读取配置
        _m_iTimingTaskCheckTime = ALBasicServerConf.getInstance().getTimerCheckTime();
        if(_m_iTimingTaskCheckTime < 10)
            _m_iTimingTaskCheckTime = 10;
        if(_m_iTimingTaskCheckTime > 1000)
            _m_iTimingTaskCheckTime = 1000;
        
        _m_iTimingTaskCheckAreaSize = ALBasicServerConf.getInstance().getTimerCheckAreaSize();
        if(_m_iTimingTaskCheckAreaSize < 1000)
            _m_iTimingTaskCheckAreaSize = 1000;
        if(_m_iTimingTaskCheckAreaSize > 100000)
            _m_iTimingTaskCheckAreaSize = 100000;
        
        //获取开启管理对象的时间
        _m_lTimingTaskMgrStartTime = ALBasicCommonFun.getNowTimeMS();
        //初始化最后一次检测的位置信息
        _m_iLastCheckRound = 0;
        _m_iLastCheckTick = 0;
        
        //创建固定区间长度的精度队列
        _m_arrTimingTaskNodeList = new ALSynTimingTaskNode[_m_iTimingTaskCheckAreaSize];
        for(int i = 0; i < _m_iTimingTaskCheckAreaSize; i++)
        {
            _m_arrTimingTaskNodeList[i] = new ALSynTimingTaskNode(0);
        }

        //创建锁对象
        _m_lTimingTaskMutex = new ReentrantLock();

        _m_sTaskEvent = new Semaphore(0);
    }
    
    public int getTaskCheckTime() {return _m_iTimingTaskCheckTime;}

    /*****************
     * 注册一个直接执行的任务
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:01:18 PM
     */
    public void regTask(_IALSynTask _task)
    {
        _lockCurrentTaskList();
        
        //向链表中添加执行任务
        _m_lCurrentTaskList.add(_task);

        //释放任务数量信号量，在实际线程中将可获取任务进行处理
        _releaseTaskEvent();
        
        _unlockCurrentTaskList();
    }
    
    /*********************
     * 注册定时执行的任务
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:04:48 PM
     */
    public void regTask(_IALSynTask _task, int _time)
    {
        _lockTimingTaskList();
        
        //判断定时时间，当非法，则直接当作当前执行任务插入
        if(_time <= 0)
        {
            regTask(_task);
        }
        else
        {
            //获取当前时间
            long nowTime = ALBasicCommonFun.getNowTimeMS();
            //注册定时任务，将任务添加到表中等待插入到执行队列
            if(!_regTimingTask(nowTime + _time - _m_lTimingTaskMgrStartTime, _task))
                regTask(_task);
        }
        
        _unlockTimingTaskList();
    }
    public void regTask(_IALSynTask _task, long _time)
    {
        _lockTimingTaskList();
        
        //判断定时时间，当非法，则直接当作当前执行任务插入
        if(_time <= 0)
        {
            regTask(_task);
        }
        else
        {
            //获取当前时间
            long nowTime = ALBasicCommonFun.getNowTimeMS();
            //注册定时任务，将任务添加到表中等待插入到执行队列
            if(!_regTimingTask(nowTime + _time - _m_lTimingTaskMgrStartTime, _task))
                regTask(_task);
        }
        
        _unlockTimingTaskList();
    }
    
    /*********************
     * 提取出第一个需要执行的任务
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:17:58 PM
     */
    public _IALSynTask popCurrentTask()
    {
        _acquireTaskEvent();
        _lockCurrentTaskList();
        
        //判断任务队列是否为空
        if(_m_lCurrentTaskList.isEmpty())
        {
            _unlockCurrentTaskList();
            return null;
        }
        
        //取出并移除任务队列第一个任务
        _IALSynTask task = _m_lCurrentTaskList.removeFirst();
        
        _unlockCurrentTaskList();
        return task;
    }
    
    /****************
     * 注册整个队列的定时任务
     * 系统内接口不对外开放
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:32:18 PM
     */
    protected void _registerTaskList(LinkedList<_IALSynTask> _taskList)
    {
        //在循环内加锁有利于大量任务插入时长时间阻塞处理线程的情况
        while(!_taskList.isEmpty())
        {
            _lockCurrentTaskList();
            
            _m_lCurrentTaskList.add(_taskList.removeFirst());

            _releaseTaskEvent();
            
            _unlockCurrentTaskList();
        }
    }
    
    /***************
     * 当前任务队列操作锁定操作
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:34:52 PM
     */
    protected void _lockCurrentTaskList()
    {
        _m_lCurrentTaskMutex.lock();
    }
    protected void _unlockCurrentTaskList()
    {
        _m_lCurrentTaskMutex.unlock();
    }

    /*********************
     * 定时任务队列操作锁定操作
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:35:02 PM
     */
    protected void _lockTimingTaskList()
    {
        _m_lTimingTaskMutex.lock();
    }
    protected void _unlockTimingTaskList()
    {
        _m_lTimingTaskMutex.unlock();
    }

    /*********************
     * 释放一个任务信号量，以使得等待执行任务的线程通过信号量获得通知
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:35:08 PM
     */
    protected void _releaseTaskEvent()
    {
        _m_sTaskEvent.release();
    }
    
    /*********************
     * 获取一个任务信号量，表示开始处理一个任务
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:35:37 PM
     */
    protected void _acquireTaskEvent()
    {
        _m_sTaskEvent.acquireUninterruptibly();
    }
    
    /****************
     * 在定时任务处理表中添加定时执行的任务，带入的_dealTime是距离本任务管理对象开启的时间间隔
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:06:12 PM
     */
    protected boolean _regTimingTask(long _dealTime, _IALSynTask _task)
    {
        _lockTimingTaskList();
        
        try
        {
            //根据时间精度以及回合总时间区域计算对应的回合数以及时间节点
            int tick = (int)((_dealTime + _m_iTimingTaskCheckTime - 1) / _m_iTimingTaskCheckTime);
            int round = tick / _m_iTimingTaskCheckAreaSize;
            //计算实际的下标数
            tick = tick - (round * _m_iTimingTaskCheckAreaSize);
            
            //当下标和回合等于最后一次检测的数据时将任务移到下一个下标中进行处理
            if(tick <= _m_iLastCheckTick && round <= _m_iLastCheckRound)
            {
                tick++;
                if(tick >= _m_iTimingTaskCheckAreaSize)
                {
                    tick -= _m_iTimingTaskCheckAreaSize;
                    round++;
                }
            }
            
            //将任务添加到对应下标
            return _m_arrTimingTaskNodeList[tick].addTimingTask(round, _task);
        }
        finally
        {
            _unlockTimingTaskList();
        }
    }

    /********************
     * 将到目前为止的所有定时任务取出
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:23:11 PM
     */
    protected void _popTimerTask(LinkedList<_IALSynTask> _recList)
    {
        if(null == _recList)
            return ;

        _lockTimingTaskList();

        try
        {
            //获取运行的时间
            long dealTime = ALBasicCommonFun.getNowTimeMS() - _m_lTimingTaskMgrStartTime;
            //根据运行时间计算出当前时间对应的下标
            int tick = (int)(dealTime / _m_iTimingTaskCheckTime);
            int round = tick / _m_iTimingTaskCheckAreaSize;
            //计算实际的下标数
            tick = tick - (round * _m_iTimingTaskCheckAreaSize);
            
            //将下标和round不断累加获取需要执行的任务
            while(_m_iLastCheckTick < tick || _m_iLastCheckRound < round)
            {
                //累加下标
                _m_iLastCheckTick++;
                //判断下标是否越界
                if(_m_iLastCheckTick >= _m_iTimingTaskCheckAreaSize)
                {
                    _m_iLastCheckTick -= _m_iTimingTaskCheckAreaSize;
                    _m_iLastCheckRound++;
                }
                
                //获取对应数据
                _m_arrTimingTaskNodeList[_m_iLastCheckTick].popAllRoundTaskAndMoveNextRound(_m_iLastCheckRound, _recList);
            }
        }
        finally
        {
            _unlockTimingTaskList();
        }
    }
}

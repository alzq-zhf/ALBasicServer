package ALBasicServer.ALServerSynTask;

import java.util.Date;
import java.util.HashMap;
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
    
    /** 定时任务队列，索引为定时的时间值（毫秒） */
    private HashMap<Long, LinkedList<_IALSynTask>> _m_htTimingTaskTable;
    /** 定时任务队列锁 */
    private ReentrantLock _m_lTimingTaskMutex;
    
    /** 定时任务的时间精度 */
    private int _m_iTimingTaskCheckTime;
    /** 定时任务最后一次处理的时间 */
    private long _m_lTimingTaskLastCheckTime;
    
    /** 任务对应的同步信号量 */
    private Semaphore _m_sTaskEvent;
    
    protected ALSynTaskManager()
    {
        _m_lCurrentTaskList = new LinkedList<_IALSynTask>();
        _m_lCurrentTaskMutex = new ReentrantLock();
        
        _m_htTimingTaskTable = new HashMap<Long, LinkedList<_IALSynTask>>();
        _m_lTimingTaskMutex = new ReentrantLock();
        _m_iTimingTaskCheckTime = ALBasicServerConf.getInstance().getTimerCheckTime();
        
        _m_lTimingTaskLastCheckTime = ALBasicCommonFun.getNowTimeMS();

        _m_sTaskEvent = new Semaphore(0);
    }
    
    public long getTimingTaskLastCheckTime() {return _m_lTimingTaskLastCheckTime;}

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
            _regTimingTask(nowTime + _time, _task);
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
            _regTimingTask(nowTime + _time, _task);
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
    
    /***********************
     * 将指定范围时间的需要执行的定时任务取出
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:22:52 PM
     */
    public LinkedList<LinkedList<_IALSynTask>> popNeedDealTimingTask(long _startTime)
    {
        return _popTimerTask(_startTime);
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
    
    /*****************
     * 刷新当前时间对应精度舍入后的最后一个时间点
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:13:53 PM
     */
    protected long _refreshTimingTaskLastCheckTime()
    {
        long nowTime = new Date().getTime();
        
        int deltaTime = (int)(nowTime % _m_iTimingTaskCheckTime);
        _m_lTimingTaskLastCheckTime = nowTime - deltaTime;
        
        return _m_lTimingTaskLastCheckTime;
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
     * 在定时任务处理表中添加定时执行的任务
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:06:12 PM
     */
    protected void _regTimingTask(long _dealTime, _IALSynTask _task)
    {
        //根据时间精度将时间更改为最近的执行时间
        int deltaTime = (int)(_dealTime % _m_iTimingTaskCheckTime);
        
        //计算实际执行的时间，当时间不为处理精度的整数时，往后推到精度的整数时进行处理
        long realDealTime = _dealTime;
        if(0 != deltaTime)
            realDealTime = _dealTime - deltaTime + _m_iTimingTaskCheckTime;
        
        _lockTimingTaskList();
        
        //获取对应处理时间的处理任务队列
        LinkedList<_IALSynTask> taskList = _m_htTimingTaskTable.get(realDealTime);
        if(null == taskList)
        {
            taskList = new LinkedList<_IALSynTask>();
            _m_htTimingTaskTable.put(realDealTime, taskList);
        }
        
        taskList.add(_task);
        
        _unlockTimingTaskList();
    }

    /********************
     * 将指定开始时间线到当前最后精度处理时间线之间的所有任务取出并放入处理队列
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:23:11 PM
     */
    protected LinkedList<LinkedList<_IALSynTask>> _popTimerTask(long _startTime)
    {
        LinkedList<LinkedList<_IALSynTask>> list = new LinkedList<LinkedList<_IALSynTask>>();
        
        _lockTimingTaskList();

        long endTime = _m_lTimingTaskLastCheckTime;
        
        //当时间在时间区间内时，取出对应的任务
        long time = _startTime;
        while (time <= endTime)
        {
            LinkedList<_IALSynTask> tmpList = _m_htTimingTaskTable.remove(time);
            
            if(null != tmpList)
                list.add(tmpList);
            
            time += _m_iTimingTaskCheckTime;
        }
        
        _unlockTimingTaskList();
        
        return list;
    }
}

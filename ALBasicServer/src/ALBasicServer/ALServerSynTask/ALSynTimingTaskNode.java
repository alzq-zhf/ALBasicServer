package ALBasicServer.ALServerSynTask;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALTask._IALSynTask;
import ALServerLog.ALServerLog;

/*******************
 * 定时任务的划片集合节点信息
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jul 16, 2015 10:57:02 PM
 */
public class ALSynTimingTaskNode
{
    /** 当前回合标记 */
    private int _m_iCurRound;
    /** 当前回合的本节点定时任务队列 */
    private LinkedList<_IALSynTask> _m_lCurRoundTimingTaskList;

    /** 下一回合标记 */
    private int _m_iNextRound;
    /** 固定记录下一回合的延迟任务队列集合 */
    private LinkedList<_IALSynTask> _m_lNextRoundTimingTaskList;
    
    /** 非本回合与下一回合的较长时间延迟任务信息集合队列 */
    private LinkedList<ALSynTimingTaskNodeFarDelayTaskInfo> _m_lFarDelayTaskList;
    
    /** 本节点下的数据锁 */
    private ReentrantLock _m_mutex;
    
    public ALSynTimingTaskNode(int _round)
    {
        _m_iCurRound = _round;
        _m_lCurRoundTimingTaskList = new LinkedList<_IALSynTask>();
        
        _m_iNextRound = _round + 1;
        _m_lNextRoundTimingTaskList = new LinkedList<_IALSynTask>();
        
        _m_lFarDelayTaskList = new LinkedList<ALSynTimingTaskNodeFarDelayTaskInfo>();
        
        _m_mutex = new ReentrantLock();
    }
    
    /***************
     * 添加指定回合的本时间节点的任务，返回是否添加成功，不成功则表示需要马上执行
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:26:06 PM
     */
    public boolean addTimingTask(int _round, _IALSynTask _task)
    {
        _lock();
        
        try
        {
            if(null == _task)
                return false;
            
            //当本回合的时候，直接进行添加
            if(_round == _m_iCurRound)
            {
                _m_lCurRoundTimingTaskList.add(_task);
                return true;
            }
            else if(_round == _m_iNextRound)
            {
                _m_lNextRoundTimingTaskList.add(_task);
                return true;
            }
            else if(_round > _m_iNextRound)
            {
                //长时间延迟的任务，按照回合顺序放入对应队列
                ListIterator<ALSynTimingTaskNodeFarDelayTaskInfo> iterator = _m_lFarDelayTaskList.listIterator();
                while(iterator.hasNext())
                {
                    ALSynTimingTaskNodeFarDelayTaskInfo taskInfo = iterator.next();
                    if(taskInfo.getRound() == _round)
                    {
                        //匹配回合则加入本节点
                        taskInfo.addSynTask(_task);
                        break;
                    }
                    else if(taskInfo.getRound() < _round)
                    {
                        //当插入的回合比对应回合早，则需要在对应回合之前插入数据
                        //这里采用的做法是将本节点数据重复插入到下一个节点，之后将本节点设置为新数据
                        iterator.add(taskInfo);

                        //创建新节点
                        ALSynTimingTaskNodeFarDelayTaskInfo newInfo = new ALSynTimingTaskNodeFarDelayTaskInfo(_round);
                        iterator.set(newInfo);
                        //插入任务
                        newInfo.addSynTask(_task);
                        break;
                    }
                }
                
                //判断是否已经到了最后节点
                if(!iterator.hasNext())
                {
                    //在最后节点则往最后追加数据
                    //创建新节点
                    ALSynTimingTaskNodeFarDelayTaskInfo newInfo = new ALSynTimingTaskNodeFarDelayTaskInfo(_round);
                    iterator.add(newInfo);
                    //插入任务
                    newInfo.addSynTask(_task);
                }
                
                return true;
            }
            else
            {
                //当回合小于当前回合则表示失败，外部需要直接处理
                return false;
            }
        }
        finally
        {
            _unlock();
        }
    }
    
    /***************
     * 取出所有对应回合的本节点任务
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:49:51 PM
     */
    public void popAllRoundTaskAndMoveNextRound(int _round, LinkedList<_IALSynTask> _recList)
    {
        if(null == _recList)
            return ;
        
        _lock();
        
        try
        {
            //判断回合是否在本回合之前，则不做处理
            if(_round < _m_iCurRound)
                return ;
            
            //将所有当前回合任务放入队列
            while(!_m_lCurRoundTimingTaskList.isEmpty())
            {
                _recList.addLast(_m_lCurRoundTimingTaskList.pop());
            }
            
            //当回合在本回合之后则弹出错误警告，并将所有对应回合之后的任务放入队列
            if(_round > _m_iCurRound)
            {
                //可能跳过某个回合，因此输出错误
                ALServerLog.Fatal("Timing Syn Task Round Error! pop round: " + _round + " - current round: " + _m_iCurRound);
                
                //移动到对应的回合
                _moveToRound(_round);
                //将所有当前回合任务放入队列
                while(!_m_lCurRoundTimingTaskList.isEmpty())
                {
                    _recList.addLast(_m_lCurRoundTimingTaskList.pop());
                }
            }
            
            //移动到下一回合
            _moveNextRound();
        }
        finally
        {
            _unlock();
        }
    }
    
    /****************
     * 移动当前回合至指定回合
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:57:27 PM
     */
    protected void _moveToRound(int _targetRound)
    {
        //在不匹配对应回合的时候不断往后推移
        while(_m_iCurRound < _targetRound)
        {
            _moveNextRound();
        }
    }
    
    /***************
     * 向下移动一个回合
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:58:23 PM
     */
    protected void _moveNextRound()
    {
        //累加一个回合
        _m_iCurRound++;
        _m_iNextRound++;
        
        //将下一回合任务累加到当前队列
        while(!_m_lNextRoundTimingTaskList.isEmpty())
        {
            _m_lCurRoundTimingTaskList.addLast(_m_lNextRoundTimingTaskList.pop());
        }
        
        //获取第一个长时间间隔任务队列节点，是否匹配下一回合，是则将任务放入
        if(!_m_lFarDelayTaskList.isEmpty())
        {
            ALSynTimingTaskNodeFarDelayTaskInfo farDelayInfo = _m_lFarDelayTaskList.getFirst();
            if(farDelayInfo.getRound() == _m_iNextRound)
            {
                //从长时间间隔队列删除
                _m_lFarDelayTaskList.pop();
                //将任务放入
                farDelayInfo.popAllSynTask(_m_lNextRoundTimingTaskList);
            }
        }
    }
    
    protected void _lock(){_m_mutex.lock();}
    protected void _unlock() {_m_mutex.unlock();}
}

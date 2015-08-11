package ALBasicServer.ALServerSynTask;

import java.util.LinkedList;

import ALBasicServer.ALTask._IALSynTask;

/*******************
 * 定时任务的划片集合节点中，不再本回合区域内的任务节点集合信息
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jul 16, 2015 10:57:02 PM
 */
public class ALSynTimingTaskNodeFarDelayTaskInfo
{
    /** 对应回合标记 */
    private int _m_iRound;
    /** 任务对象 */
    private LinkedList<_IALSynTask> _m_stSynTaskList;
    
    public ALSynTimingTaskNodeFarDelayTaskInfo(int _round)
    {
        _m_iRound = _round;
        _m_stSynTaskList = new LinkedList<_IALSynTask>();
    }
    
    public int getRound() {return _m_iRound;}
    
    /***************
     * 向本节点中插入任务
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:41:12 PM
     */
    public void addSynTask(_IALSynTask _task)
    {
        if(null == _task)
            return ;
        
        _m_stSynTaskList.addLast(_task);
    }
    
    /***************
     * 将所有延迟任务放入到接收队列中
     * 
     * @author alzq.z
     * @time   Jul 16, 2015 11:39:28 PM
     */
    public void popAllSynTask(LinkedList<_IALSynTask> _recList) 
    {
        if(null == _recList)
            return ;

        while(!_m_stSynTaskList.isEmpty())
        {
            _recList.addLast(_m_stSynTaskList.pop());
        }
    }
}

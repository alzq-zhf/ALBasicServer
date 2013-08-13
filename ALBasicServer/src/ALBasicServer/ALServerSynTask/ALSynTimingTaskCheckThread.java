package ALBasicServer.ALServerSynTask;

import java.util.LinkedList;

import ALBasicCommon._AALBasicThread;
import ALBasicServer.ALBasicServerConf;
import ALBasicServer.ALTask._IALSynTask;

public class ALSynTimingTaskCheckThread extends _AALBasicThread
{
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    private int _m_iCheckTime;
    
    public ALSynTimingTaskCheckThread()
    {
        _m_bThreadExit = false;
        _m_iCheckTime = ALBasicServerConf.getInstance().getTimerCheckTime();
    }
    
    public void exitThread()
    {
        _m_bThreadExit = true;
    }
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 11:05:35 PM
     */
    @Override
    protected void _run()
    {
        //获取开启时间
        ALSynTaskManager.getInstance()._refreshTimingTaskLastCheckTime();
        long startTime = ALSynTaskManager.getInstance().getTimingTaskLastCheckTime();
        
        while(!_m_bThreadExit)
        {
            //刷新检测时间
            long nowTime = ALSynTaskManager.getInstance()._refreshTimingTaskLastCheckTime();

            //获取需要处理的所有任务队列的队列
            LinkedList<LinkedList<_IALSynTask>> needAddTaskList = 
                    ALSynTaskManager.getInstance().popNeedDealTimingTask(startTime);
            
            if(null != needAddTaskList)
            {
                while(!needAddTaskList.isEmpty())
                {
                    //逐个任务的插入到当前任务队列中
                    ALSynTaskManager.getInstance()._registerTaskList(needAddTaskList.removeFirst());
                }
            }
            
            //重新设置开始获取处理任务的时间
            startTime = nowTime;
            
            //休眠指定精度时间
            try {
                sleep(_m_iCheckTime);
            } catch (InterruptedException e) {}
        }
    }
}

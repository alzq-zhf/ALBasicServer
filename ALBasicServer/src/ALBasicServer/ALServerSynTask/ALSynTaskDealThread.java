package ALBasicServer.ALServerSynTask;

import ALBasicCommon._AALBasicThread;
import ALBasicServer.ALBasicServerConf;
import ALBasicServer.ALTask._IALSynTask;
import ALBasicServer.ALThread.ALThreadManager;
import ALBasicServer.ALThread.ALThreadMutexMgr;
import ALServerLog.ALServerLog;



public class ALSynTaskDealThread extends _AALBasicThread
{
    /** 本线程对应锁信息的存储结构体 */
    private ALThreadMutexMgr _m_tmrThreadMutexMgr;
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    
    public ALSynTaskDealThread()
    {
        _m_tmrThreadMutexMgr  = null;
        _m_bThreadExit = false;
    }
    
    public void exitThread()
    {
        _m_bThreadExit = true;
    }
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 11:05:54 PM
     */
    @Override
    protected void _run()
    {
        if(ALBasicServerConf.getInstance().getCheckMutex())
        {
            //获得当前线程ID
            long threadID = Thread.currentThread().getId();
            _m_tmrThreadMutexMgr = ALThreadManager.getInstance().regThread(threadID);
            
            //注册失败直接返回，不进行线程体操作
            if(null == _m_tmrThreadMutexMgr)
                return ;
        }
        
        while(!_m_bThreadExit)
        {
            //执行任务循环
            //获取当前需要执行的任务，无任务时将等待信号量
            _IALSynTask curTask = ALSynTaskManager.getInstance().popCurrentTask();
            
            if(null != curTask)
            {
                try
                {
                    //执行对应任务
                    curTask.run();
                }
                catch (Exception e)
                {
                    ALServerLog.Error(curTask.getClass().getName() + " Error!!");
                    e.printStackTrace();
                    
                    //当有进行锁检测时需要尝试释放所有注册锁，避免异常的操作导致锁未释放
                    if(ALBasicServerConf.getInstance().getCheckMutex())
                        _m_tmrThreadMutexMgr.releaseAllMutex();
                }

                //在任务正常或异常执行完后都需要对锁的释放情况进行判断
                if(ALBasicServerConf.getInstance().getCheckMutex())
                {
                    if(!_m_tmrThreadMutexMgr.judgeAllMutexRelease())
                    {
                        //有部分锁未释放
                        ALServerLog.Error(curTask.getClass().getName() + " have some mutexs are not released !");
    
                        _m_tmrThreadMutexMgr.releaseAllMutex();
                    }
                }
            }
        }
    }
}

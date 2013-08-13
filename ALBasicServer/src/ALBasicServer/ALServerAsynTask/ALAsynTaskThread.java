package ALBasicServer.ALServerAsynTask;

import ALBasicCommon._AALBasicThread;


/************************
 * 异步任务处理线程函数体
 * @author alzq
 *
 */
public class ALAsynTaskThread extends _AALBasicThread
{
    /** 线程是否退出 */
    private boolean _m_bThreadExit;
    /** 本线程内任务处理对象 */
    private ALAsynThreadTaskManager _m_tmTaskManager;
    
    public ALAsynTaskThread()
    {
        _m_bThreadExit = false;
        _m_tmTaskManager = new ALAsynThreadTaskManager();
    }

    public void exitThread()
    {
        _m_bThreadExit = true;
    }
    
    /*************
     * 获取任务管理对象
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:43:20 PM
     */
    protected ALAsynThreadTaskManager _getTaskManager()
    {
        return _m_tmTaskManager;
    }
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:48:09 PM
     */
    @Override
    protected void _run()
    {
        while(!_m_bThreadExit)
        {
            //每次获取第一个可执行任务进行处理
            @SuppressWarnings("rawtypes")
            ALAsynTaskInfo info = _m_tmTaskManager.popFirstAsynTask();
            
            if(null != info)
            {
                info.run();
            }
        }
    }
}

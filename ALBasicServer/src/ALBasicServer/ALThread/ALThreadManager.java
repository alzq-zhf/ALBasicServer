package ALBasicServer.ALThread;

import java.util.Hashtable;

import ALBasicServer.ALServerAsynTask.ALAsynTaskThread;
import ALBasicServer.ALServerSynTask.ALSynTaskDealThread;
import ALBasicServer.ALServerSynTask.ALSynTimingTaskCheckThread;
import ALBasicServer.ALSocket.ALServerSocketSendThread;
import ALServerLog.ALServerLog;

/***********************
 * 线程注册控制对象，每个由系统开启的线程将在此对象中注册<br>
 * 并由注册的队列进行锁获取的相关检查和控制操作<br>
 * 
 * @author alzq
 *
 */
public class ALThreadManager
{
    private static ALThreadManager g_instance = new ALThreadManager();
    
    public static ALThreadManager getInstance()
    {
        if(null == g_instance)
            g_instance = new ALThreadManager();
        
        return g_instance;
    }
    
    /** 存储所有线程ID对应线程中锁信息的表 */
    private Hashtable<Long, ALThreadMutexMgr> _m_htThreadMutexInfoTable;
    
    protected ALThreadManager()
    {
        _m_htThreadMutexInfoTable = new Hashtable<Long, ALThreadMutexMgr>();
    }

    /*********************
     * 开启任务执行线程，并返回该线程的控制对象
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:36:17 PM
     */
    public ALSynTaskDealThread createTaskDealThread()
    {
        ALSynTaskDealThread normalTaskDealThread = new ALSynTaskDealThread();
        //开启线程
        normalTaskDealThread.start();
        
        return normalTaskDealThread;
    }
    
    /***************
     * 开启定时任务监控线程，此线程全局只开一个，并返回该线程的控制对象
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:43:24 PM
     */
    public ALSynTimingTaskCheckThread createTimingTaskCheckThread()
    {
        ALSynTimingTaskCheckThread timingTaskDealThread = new ALSynTimingTaskCheckThread();
        //开启线程
        timingTaskDealThread.start();
        
        return timingTaskDealThread;
    }
    
    /***************
     * 开启Socket发送线程，并返回该线程的控制对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:33:19 PM
     */
    public ALServerSocketSendThread createSocketSendThread()
    {
        ALServerSocketSendThread socketSendThread = new ALServerSocketSendThread();
        //开启线程
        socketSendThread.start();
        
        return socketSendThread;
    }
    
    /***************
     * 开启异步处理线程，并返回该线程的控制对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:29:31 PM
     */
    public ALAsynTaskThread createAsynTaskThread()
    {
        ALAsynTaskThread asynTaskThread = new ALAsynTaskThread();
        //开启线程
        asynTaskThread.start();
        
        return asynTaskThread;
    }
    
    /***************
     * 在工程中注册一个线程对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:29:23 PM
     */
    public ALThreadMutexMgr regThread(long _threadID)
    {
        if(_m_htThreadMutexInfoTable.containsKey(_threadID))
            return null;
        
        ALServerLog.Info("Reg thread: " + _threadID);
        
        ALThreadMutexMgr threadMutexMgr = new ALThreadMutexMgr(_threadID);
        _m_htThreadMutexInfoTable.put(_threadID, threadMutexMgr);
        
        return threadMutexMgr;
    }
    
    /****************
     * 获取对应线程的锁信息控制对象
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:47:30 PM
     */
    public ALThreadMutexMgr getThreadMutexRegister(long _threadID)
    {
        return _m_htThreadMutexInfoTable.get(_threadID);
    }
}

package ALBasicServer.ALServerAsynTask;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALTask._IALAsynCallBackTask;
import ALBasicServer.ALTask._IALAsynCallTask;
import ALBasicServer.ALTask._IALAsynRunnableTask;

/**********************
 * 异步任务处理线程中存放本线程需要处理的任务队列的对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 20, 2013 10:37:01 PM
 */
@SuppressWarnings("rawtypes")
public class ALAsynThreadTaskManager
{
    private Semaphore _m_sSemaphore;
    private ReentrantLock _m_rMutex;
    /** 已经排入计划的异步任务处理对象 */
    private LinkedList<ALAsynTaskInfo> _m_lAsynTaskList;
    
    public ALAsynThreadTaskManager()
    {
        _m_sSemaphore = new Semaphore(0);
        _m_rMutex = new ReentrantLock();
        _m_lAsynTaskList = new LinkedList<ALAsynTaskInfo>();
    }
    
    /***************
     * 加入回调类型异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:01 PM
     */
    public <T> void regTask(_IALAsynCallTask<T> _callObj, _IALAsynCallBackTask<T> _callBackObj)
    {
        ALAsynTaskInfo<T> info = new ALAsynTaskInfo<T>(_callObj, _callBackObj);
        
        _lock();
        
        _m_lAsynTaskList.add(info);
        _m_sSemaphore.release();
        
        _unlock();
    }
    
    /*****************
     * 加入执行类异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:29 PM
     */
    public <T> void regTask(_IALAsynRunnableTask _runTask)
    {
        ALAsynTaskInfo<T> info = new ALAsynTaskInfo<T>(_runTask);
        
        _lock();
        
        _m_lAsynTaskList.add(info);
        _m_sSemaphore.release();
        
        _unlock();
    }
    
    /*********************
     * 取出第一个需要处理的异步任务对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:42 PM
     */
    public ALAsynTaskInfo popFirstAsynTask()
    {
        _m_sSemaphore.acquireUninterruptibly();
        ALAsynTaskInfo info = null;
        
        _lock();
        
        info = _m_lAsynTaskList.remove(0);
        
        _unlock();
        
        return info;
    }
    
    protected void _lock()
    {
        _m_rMutex.lock();
    }
    protected void _unlock()
    {
        _m_rMutex.unlock();
    }
}

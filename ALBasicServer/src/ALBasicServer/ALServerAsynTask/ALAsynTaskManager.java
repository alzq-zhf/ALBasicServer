package ALBasicServer.ALServerAsynTask;

import java.util.ArrayList;

import ALBasicServer.ALTask._AALAsynCallAndBackTask;
import ALBasicServer.ALTask._IALAsynCallBackTask;
import ALBasicServer.ALTask._IALAsynCallTask;
import ALBasicServer.ALTask._IALAsynRunnableTask;
import ALBasicServer.ALThread.ALThreadManager;

/***********************
 * 异步任务处理的管理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 4:09:59 PM
 */
public class ALAsynTaskManager
{
    private static ALAsynTaskManager g_instance = new ALAsynTaskManager();
    
    public static ALAsynTaskManager getInstance()
    {
        return g_instance;
    }
    
    /** 已经排入计划的异步任务处理对象 */
    private ArrayList<ALAsynTaskThread> _m_lAsynTaskThreadList;
    private int maxIdx;
    
    /** 系统异步任务处理线程对象 */
    private ALAsynTaskThread _m_tSystemAsynTaskThread;
    
    public ALAsynTaskManager()
    {
        _m_lAsynTaskThreadList = null;
        maxIdx = -1;
        
        _m_tSystemAsynTaskThread = null;
    }
    
    /*****************
     * 初始化所有的异步任务处理线程，带入处理线程数量
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:51:29 PM
     */
    public void init(int _asynTaskThreadCount)
    {
        //初始化系统异步处理线程
        _m_tSystemAsynTaskThread = ALThreadManager.getInstance().createAsynTaskThread();
        
        //最少一个异步线程
        if(_asynTaskThreadCount <= 0)
            _asynTaskThreadCount = 1;
        
        _m_lAsynTaskThreadList = new ArrayList<ALAsynTaskThread>(_asynTaskThreadCount);
        
        for(int i = 0; i < _asynTaskThreadCount; i++)
        {
            //逐个创建线程
            _m_lAsynTaskThreadList.add(ALThreadManager.getInstance().createAsynTaskThread());
        }
        
        maxIdx = _asynTaskThreadCount - 1;
    }
    
    /***************
     * 为指定序号的异步处理线程加入回调类型异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:01 PM
     */
    public <T> void regTask(int _threadIdx, _IALAsynCallTask<T> _callObj, _IALAsynCallBackTask<T> _callBackObj)
    {
        if(_threadIdx > maxIdx || _threadIdx < 0)
            _threadIdx = 0;
        
        //注册任务
        _m_lAsynTaskThreadList.get(_threadIdx)._getTaskManager().regTask(_callObj, _callBackObj);
    }
    
    /***************
     * 为指定序号的异步处理线程加入回调类型异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:01 PM
     */
    public <T> void regTask(int _threadIdx, _AALAsynCallAndBackTask<T> _task)
    {
        if(_threadIdx > maxIdx || _threadIdx < 0)
            _threadIdx = 0;
        
        //注册任务
        _m_lAsynTaskThreadList.get(_threadIdx)._getTaskManager().regTask(_task, _task);
    }
    
    /*****************
     * 为指定序号的异步处理线程加入执行类异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:29 PM
     */
    public void regTask(int _threadIdx, _IALAsynRunnableTask _runTask)
    {
        if(_threadIdx > maxIdx || _threadIdx < 0)
            _threadIdx = 0;
        
        //注册任务
        _m_lAsynTaskThreadList.get(_threadIdx)._getTaskManager().regTask(_runTask);
    }
    
    /***************
     * 为系统异步处理线程加入回调类型异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:01 PM
     */
    public <T> void regSysTask(_IALAsynCallTask<T> _callObj, _IALAsynCallBackTask<T> _callBackObj)
    {
        //注册任务
        _m_tSystemAsynTaskThread._getTaskManager().regTask(_callObj, _callBackObj);
    }
    
    /*****************
     * 为系统异步处理线程加入执行类异步任务处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:09:29 PM
     */
    public void regSysTask(_IALAsynRunnableTask _runTask)
    {
        //注册任务
        _m_tSystemAsynTaskThread._getTaskManager().regTask(_runTask);
    }
}

package ALBasicServer.ALServerAsynTask;

import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALTask._IALAsynCallBackTask;
import ALBasicServer.ALTask._IALAsynCallTask;
import ALBasicServer.ALTask._IALAsynRunnableTask;

/*********************
 * 异步任务处理信息对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 4:01:56 PM
 */
public class ALAsynTaskInfo<T>
{
    private _IALAsynCallTask<T> _m_tCallObj;
    private _IALAsynCallBackTask<T> _m_tCallBackObj;
    private _IALAsynRunnableTask _m_tRunObj;
    
    public ALAsynTaskInfo(_IALAsynCallTask<T> _callObj, _IALAsynCallBackTask<T> _callBackObj)
    {
        _m_tCallObj = _callObj;
        _m_tCallBackObj = _callBackObj;
        _m_tRunObj = null;
    }
    public ALAsynTaskInfo(_IALAsynRunnableTask _runObj)
    {
        _m_tCallObj = null;
        _m_tCallBackObj = null;
        _m_tRunObj = _runObj;
    }
    
    public void run()
    {
        if(null == _m_tRunObj)
        {
            //处理异步
            T object = null;
            try
            {
                if(null != _m_tCallObj)
                {
                    object = _m_tCallObj.dealAsynTask();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            //添加任务进行回调的处理
            if(null != _m_tCallBackObj)
            {
                ALSynTaskManager.getInstance().regTask(new SynAsynCallBackTask<T>(object, _m_tCallBackObj));
            }
        }
        else
        {
            try
            {
                _m_tRunObj.run();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}

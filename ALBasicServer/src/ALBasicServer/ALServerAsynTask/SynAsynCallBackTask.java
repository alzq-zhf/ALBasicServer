package ALBasicServer.ALServerAsynTask;

import ALBasicServer.ALTask._IALAsynCallBackTask;
import ALBasicServer.ALTask._IALSynTask;

public class SynAsynCallBackTask<T> implements _IALSynTask
{
    private T _m_OBJ;
    private _IALAsynCallBackTask<T> _m_CallBackTask;
    
    public SynAsynCallBackTask(T _obj, _IALAsynCallBackTask<T> _callBackTask)
    {
        _m_OBJ = _obj;
        _m_CallBackTask = _callBackTask;
    }
    
    @Override
    public void run()
    {
        if(null == _m_OBJ)
            _m_CallBackTask.dealFail();
        else
        {
            try
            {
                _m_CallBackTask.dealSuc(_m_OBJ);
            }
            catch (Exception e)
            {
                //回调过程中错误则当作失败处理
                e.printStackTrace();
                _m_CallBackTask.dealFail();
            }
        }
    }
}

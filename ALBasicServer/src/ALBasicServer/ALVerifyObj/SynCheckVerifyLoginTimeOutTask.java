package ALBasicServer.ALVerifyObj;

import ALBasicServer.ALTask._IALSynTask;

/*******************
 * 验证指定验证序列号的验证操作是否完成，未完成则直接按照失败处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 2:41:06 PM
 */
public class SynCheckVerifyLoginTimeOutTask implements _IALSynTask
{
    private int serialize;
    
    public SynCheckVerifyLoginTimeOutTask(int _serialize)
    {
        serialize = _serialize;
    }

    @Override
    public void run()
    {
        ALVerifyObjMgr.getInstance()._comfirmVerifyResult(serialize, null);
    }

}

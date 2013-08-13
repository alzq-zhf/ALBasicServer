package ALBasicServer.ALVerifyObj;

import ALBasicServer.ALBasicServer;
import ALBasicServer.ALTask._IALAsynRunnableTask;


/****************
 * 用户登录的异步处理函数
 * @author alzq
 *
 */
public class AsynRun_UserLoginTask implements _IALAsynRunnableTask
{
    private ALVerifyDealerObj _m_vdVerifyDealer;
    private String _m_sUserName;
    private String _m_sUserPassword;
    
    public AsynRun_UserLoginTask(int _serialize, String _name, String _password)
    {
        _m_vdVerifyDealer = new ALVerifyDealerObj(_serialize);
        _m_sUserName = _name;
        _m_sUserPassword = _password;
    }

    @Override
    public void run()
    {
        ALBasicServer.getVerifyObj().verifyIdentity(_m_vdVerifyDealer, _m_sUserName, _m_sUserPassword);
    }

}

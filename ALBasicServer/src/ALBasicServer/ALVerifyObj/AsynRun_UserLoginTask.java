package ALBasicServer.ALVerifyObj;

import ALBasicServer.ALSocket.ALBasicServerSocket;
import ALBasicServer.ALTask._IALAsynRunnableTask;


/****************
 * 用户登录的异步处理函数
 * @author alzq
 *
 */
public class AsynRun_UserLoginTask implements _IALAsynRunnableTask
{
    private ALVerifyDealerObj _m_vdVerifyDealer;
    private ALBasicServerSocket _m_ssSocketObj;
    
    public AsynRun_UserLoginTask(int _serialize, ALBasicServerSocket _socketObj)
    {
        _m_vdVerifyDealer = new ALVerifyDealerObj(_serialize);
        _m_ssSocketObj = _socketObj;
    }

    @Override
    public void run()
    {
        //获取验证对象
        _IALVerifyFun verifyFun = ALVerifyObjMgr.getInstance().getVerifyObj(_m_ssSocketObj.getVerifyObjIdx());
        if(null == verifyFun)
            return ;
        
        verifyFun.verifyIdentity(_m_vdVerifyDealer, _m_ssSocketObj.getLoginClientType(), _m_ssSocketObj.getUserName()
                , _m_ssSocketObj.getUserPassword(), _m_ssSocketObj.getLoginCustomMsg());
    }

}

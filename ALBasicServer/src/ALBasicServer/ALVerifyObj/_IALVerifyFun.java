package ALBasicServer.ALVerifyObj;

public interface _IALVerifyFun
{
    /*******************
     * 验证用户身份并返回身份标识对象,此函数在异步处理中调用
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:18:12 PM
     */
    public abstract void verifyIdentity(ALVerifyDealerObj _verifyDealer, String _userName, String _userPassword);
}

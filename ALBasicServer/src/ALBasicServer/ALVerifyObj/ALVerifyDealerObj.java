package ALBasicServer.ALVerifyObj;

import ALBasicServer.ALSocket._AALBasicServerSocketListener;

/********************
 * 处理结果的对象，用户可以不必了解内部机制的序列号等对象直接操作一个函数确认验证结果
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 4:20:56 PM
 */
public class ALVerifyDealerObj
{
    private int verifySerialize;
    
    public ALVerifyDealerObj(int _serialize)
    {
        verifySerialize = _serialize;
    }
    
    /******************
     * 确认结果
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:20:44 PM
     */
    public void comfirmResult(_AALBasicServerSocketListener _listener)
    {
        ALVerifyObjMgr.getInstance()._comfirmVerifyResult(verifySerialize, _listener);
    }
}

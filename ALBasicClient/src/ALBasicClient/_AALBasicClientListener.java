package ALBasicClient;

import java.nio.ByteBuffer;

import ALBasicProtocolPack._IALProtocolStructure;
import ALBasicProtocolPack.BasicObj._IALProtocolReceiver;

public abstract class _AALBasicClientListener implements _IALProtocolReceiver
{
    /** 连接的端口对象 */
    private ALBasicClientSocket _m_csClientSocket;
    
    public _AALBasicClientListener(String _serverIP, int _serverPort)
    {
        _m_csClientSocket = new ALBasicClientSocket(this, _serverIP, _serverPort);
    }
    
    public ALBasicClientSocket getSocket() {return _m_csClientSocket;}

    /*******************
     * 连接服务器并尝试登录
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:04:33 PM
     */
    public void login(String _userName, String _userPassword)
    {
        _m_csClientSocket.login(_userName, _userPassword);
    }
    
    /********************
     * 发送消息
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:04:41 PM
     */
    public void send(ByteBuffer _buffer)
    {
        if(null == _m_csClientSocket)
            return ;
        
        _m_csClientSocket.send(_buffer);
    }
    public void send(ByteBuffer _tmpHeader, ByteBuffer _buffer)
    {
        if(null == _m_csClientSocket)
            return ;
        
        _m_csClientSocket.send(_tmpHeader, _buffer);
    }
    public void send(_IALProtocolStructure _protocolObj)
    {
        if(null == _m_csClientSocket || null == _protocolObj)
            return ;
        
        _m_csClientSocket.send(_protocolObj.makeFullPackage());
    }

    public String getUserName() {return _m_csClientSocket.getUserName();}

    /****************
     * 接收消息的处理函数，此函数在接收线程中处理，如需要更好的处理方式则需要另开线程进行处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:04:48 PM
     */
    public abstract void receiveMes(ByteBuffer _mes);
    /****************
     * 连接服务器失败
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:04:53 PM
     */
    public abstract void ConnectFail();
    /***************
     * 登录失败
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:05:03 PM
     */
    public abstract void LoginFail();
    /***************
     * 登出操作
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:05:07 PM
     */
    public abstract void Disconnect();
    /***************
     * 登录成功
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:05:11 PM
     */
    public abstract void LoginSuc();
}

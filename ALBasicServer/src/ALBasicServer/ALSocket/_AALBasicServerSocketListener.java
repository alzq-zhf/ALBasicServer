package ALBasicServer.ALSocket;

import java.nio.ByteBuffer;

import ALBasicProtocolPack._IALProtocolStructure;
import ALBasicProtocolPack.BasicObj._IALProtocolReceiver;

/***********************
 * 服务器架构下的消息处理对象，作为带入到消息实际处理函数中的一个参数
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 11:48:14 AM
 */
public abstract class _AALBasicServerSocketListener implements _IALProtocolReceiver
{
    /** Socket对象 */
    private ALBasicServerSocket _m_csSocket;

    /************
     * 获取Socket对象ID
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:49:51 AM
     */
    public long getSocketID()
    {
    	if(null == _m_csSocket)
    		return 0;
    	
    	return _m_csSocket.getSocketID();
    }
    public ALBasicServerSocket getSocket() {return _m_csSocket;}
    public boolean enable() {return (null != _m_csSocket);}
    
    //内部函数，设置Socket对象
    public void setSocket(ALBasicServerSocket _socket) {_m_csSocket = _socket;}
    
    /**************
     * 发送消息到对应的接收端
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:52:17 AM
     */
    public void send(ByteBuffer _buffer)
    {
        if(null == _m_csSocket || null == _buffer)
            return ;
        
        _m_csSocket.send(_buffer);
    }
    public void send(_IALProtocolStructure _protocolObj)
    {
        if(null == _m_csSocket || null == _protocolObj)
            return ;
        
        //协议打包主体
        ByteBuffer msg = _protocolObj.makeFullPackage();
        
        _m_csSocket.send(msg);
    }
    
    /*****************
     * 断开Socket
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:35:39 PM
     */
    public void logout()
    {
        ALServerSocketMgr.getInstance().kickUser(_m_csSocket);
    }
    
    /******************
     * 获取到相关消息并处理的函数
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:36:05 PM
     */
    public abstract void receiveMsg(ByteBuffer _buf);
    
    /*******************
     * 连接并验证成功时调用的函数
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:36:37 PM
     */
    public abstract void login();
    
    /*******************
     * Socket断开连接时调用的函数
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:37:13 PM
     */
    public abstract void disconnect();
}

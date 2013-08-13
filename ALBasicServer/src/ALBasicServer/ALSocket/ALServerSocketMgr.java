package ALBasicServer.ALSocket;

import java.nio.channels.SocketChannel;
import java.util.Hashtable;

import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALServerLog.ALServerLog;

/****************
 * 所有连接到本服务器的Socket的管理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 11:56:18 AM
 */
public class ALServerSocketMgr
{
    private static ALServerSocketMgr g_instance;
    
    public static ALServerSocketMgr getInstance()
    {
        if(null == g_instance)
            g_instance = new ALServerSocketMgr();
        
        return g_instance;
    }
    
    /** 注册的相关Socket对象,通过连接的SocketChannel对象作为索引Key */
    private Hashtable<SocketChannel, ALBasicServerSocket> _m_htSocketRegTable;
    /** 还未注册的socket对象链表 */

    protected ALServerSocketMgr()
    {
        _m_htSocketRegTable = new Hashtable<SocketChannel, ALBasicServerSocket>();
    }
    
    /*******************
     * 注册Socket对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:59:38 AM
     */
    public void regSocket(ALBasicServerSocket _socket)
    {
        _m_htSocketRegTable.put(_socket._getSocketChannel(), _socket);
    }
    
    /********************
     * 获取socket对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:59:42 AM
     */
    public ALBasicServerSocket getSocket(SocketChannel _channel)
    {
        return _m_htSocketRegTable.get(_channel);
    }
    
    /*********************
     * 删除注册的对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:59:48 AM
     */
    public void unregSocket(SocketChannel _channel)
    {
        ALBasicServerSocket socket = _m_htSocketRegTable.remove(_channel);
        
        kickUser(socket);
    }
    
    /*******************
     * 剔除已连接的Socket的操作
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:59:54 AM
     */
    public void kickUser(ALBasicServerSocket _socket)
    {
        if(null == _socket)
            return ;
        
        ALServerLog.Info(_socket.getUserName() + " Disconnected!");
        
        SocketChannel channel = _socket._getSocketChannel();
        
        if(null != channel)
        {
            _m_htSocketRegTable.remove(channel);

            //移除Socket中的发送Channel对象
            _socket._removeChannel();
            try {
                channel.close();
            } catch (Exception e) {}
        }
        
        //插入断开连接的处理任务
        ALSynTaskManager.getInstance().regTask(new SynDisconnectTask(_socket));
    }
}

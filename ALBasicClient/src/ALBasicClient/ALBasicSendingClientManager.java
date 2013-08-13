package ALBasicClient;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/*********************
 * 存储所有发送对象的队列，进行相关顺序的存储以及统一管理
 * @author alzq
 *
 */
public class ALBasicSendingClientManager
{
    private static ALBasicSendingClientManager g_instance = new ALBasicSendingClientManager();
    
    public static ALBasicSendingClientManager getInstance()
    {
        if(null == g_instance)
            g_instance = new ALBasicSendingClientManager();
        
        return g_instance;
    }
    
    /** 存储Socket数量相关的信号量 */
    private Semaphore _m_sSocketEvent;
    /** 发送端口队列锁 */
    private ReentrantLock _m_rSocketLock;
    /** 需要发送的SocketList队列 */
    private LinkedList<ALBasicClientSocket> _m_lSendSocketList;
    
    protected ALBasicSendingClientManager()
    {
        _m_sSocketEvent = new Semaphore(0);
        _m_rSocketLock = new ReentrantLock();
        _m_lSendSocketList = new LinkedList<ALBasicClientSocket>();
    }
    
    /*****************
     * 添加需要发送的Socket
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:58:37 PM
     */
    public void addSendSocket(ALBasicClientSocket _socket)
    {
        _lock();
        
        _m_lSendSocketList.add(_socket);
        _m_sSocketEvent.release();
        
        _unlock();
    }
    
    /*****************
     * 取出一个Socket进行发送
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:58:41 PM
     */
    public ALBasicClientSocket popSendSocket()
    {
        _m_sSocketEvent.acquireUninterruptibly();
        
        ALBasicClientSocket socket = null;
        _lock();
        
        socket = _m_lSendSocketList.pop();
        
        _unlock();
        
        return socket;
    }
    
    protected void _lock()
    {
        _m_rSocketLock.lock();
    }
    protected void _unlock()
    {
        _m_rSocketLock.unlock();
    }
}

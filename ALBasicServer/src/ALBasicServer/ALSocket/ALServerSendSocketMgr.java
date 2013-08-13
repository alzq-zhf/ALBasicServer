package ALBasicServer.ALSocket;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


/*********************
 * 存储所有需要发送消息的Socket，并使用堆栈方式进行管理。<br>
 * 逐个取出并进行发送处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 1:42:56 PM
 */
public class ALServerSendSocketMgr
{
    private static ALServerSendSocketMgr g_instance = new ALServerSendSocketMgr();
    
    public static ALServerSendSocketMgr getInstance()
    {
        if(null == g_instance)
            g_instance = new ALServerSendSocketMgr();
        
        return g_instance;
    }
    
    /** 存储Socket数量相关的信号量 */
    private Semaphore _m_sSocketEvent;
    /** 发送端口队列锁 */
    private ReentrantLock _m_rSocketLock;
    /** 需要发送的SocketList队列 */
    private LinkedList<ALBasicServerSocket> _m_lSendSocketList;
    
    protected ALServerSendSocketMgr()
    {
        _m_sSocketEvent = new Semaphore(0);
        _m_rSocketLock = new ReentrantLock();
        _m_lSendSocketList = new LinkedList<ALBasicServerSocket>();
    }
    
    /*****************
     * 添加需要发送的Socket对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:44:32 PM
     */
    public void addSendSocket(ALBasicServerSocket _socket)
    {
        _lock();
        
        _m_lSendSocketList.add(_socket);
        _m_sSocketEvent.release();
        
        _unlock();
    }
    
    /*****************
     * 取出第一个需要发送数据的Socket
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:44:54 PM
     */
    public ALBasicServerSocket popSendSocket()
    {
        _m_sSocketEvent.acquireUninterruptibly();
        
        ALBasicServerSocket socket = null;
        _lock();
        
        socket = _m_lSendSocketList.pop();
        
        _unlock();
        
        return socket;
    }
    
    //================= protected function
    protected void _lock()
    {
        _m_rSocketLock.lock();
    }
    protected void _unlock()
    {
        _m_rSocketLock.unlock();
    }
}

package ALBasicClient;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALServerSynTask.ALSynTaskManager;

/*********************
 * 在服务器架构内连接服务器的客户端对象，在接受消息后统一使用同步任务进行处理
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   May 28, 2014 11:32:21 PM
 */
public abstract class _AALBasicServerClientListener extends _AALBasicClientListener
{
    /** 接收到的消息管理队列信息 */
    private ReentrantLock _m_msgMutex;
    private LinkedList<ByteBuffer> _m_lMsgList;
    
    public _AALBasicServerClientListener(String _serverIP, int _serverPort)
    {
        super(_serverIP, _serverPort);
        
        _m_msgMutex = new ReentrantLock();
        _m_lMsgList = new LinkedList<ByteBuffer>();
    }
    
    @Override
    public void receiveMes(ByteBuffer _mes)
    {
        addMessage(_mes);
    }
    
    /********************
     * 添加一个需要处理的消息
     * 
     * @author alzq.z
     * @time   Mar 4, 2013 10:19:30 PM
     */
    public void addMessage(ByteBuffer _msg)
    {
        //消息无效或角色无加载完成则直接返回
        if(null == _msg)
            return ;
        
        boolean needStartTask = false;
        
        _lock();
        
        if(_m_lMsgList.isEmpty())
            needStartTask = true;
        
        _m_lMsgList.addLast(_msg);
        
        _unlock();
        
        if(needStartTask)
            ALSynTaskManager.getInstance().regTask(new ALSynServerClientDealMessageTask(this));
    }
    
    /******************
     * 自行从消息队列中取出一个消息进行处理
     * 
     * @author alzq.z
     * @time   Mar 4, 2013 10:24:27 PM
     */
    public void dealMessage()
    {
        ByteBuffer msg = null;
        boolean needContinueTask = false;
        
        //取出第一个消息
        _lock();
        
        if(!_m_lMsgList.isEmpty())
            msg = _m_lMsgList.getFirst();
        
        _unlock();
        
        if(null != msg)
        {
            //处理消息
            try
            {
                this._dealMes(msg);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        
        _lock();
        
        _m_lMsgList.pop();
        
        //判断消息队列是否为空
        if(!_m_lMsgList.isEmpty())
            needContinueTask = true;
        
        _unlock();
        
        if(needContinueTask)
            ALSynTaskManager.getInstance().regTask(new ALSynServerClientDealMessageTask(this));
    }
    
    protected void _lock()
    {
        _m_msgMutex.lock();
    }
    protected void _unlock()
    {
        _m_msgMutex.unlock();
    }
    
    /********************
     * 玩家角色消息数据的处理函数
     *
     * @author alzq.z
     * @time Mar 4, 2013 10:46:06 AM
     */
    protected abstract void _dealMes(ByteBuffer _msg);
}

package ALBasicServer.ALSocket;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALBasicServerConf;
import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALVerifyObj.ALVerifyObjMgr;
import ALServerLog.ALServerLog;
import BasicServer.C2S_BasicClientVerifyInfo;


public class ALBasicServerSocket
{
    /** 对应的连接ID */
    private long _m_ID;
    /** 对应的连接对象 */
    private SocketChannel _m_scSocketChannel;
    /** 对应的处理对象 */
    private _AALBasicServerSocketListener _m_clListener;
    /** 验证时传入的用户名 */
    private String _m_sUserName;
    /** 验证时传入的用户密码 */
    private String _m_sUserPassword;
    /** 是否正在登录 */
    private boolean _m_bLoginIng;
    
    /** 对方连接的信息 */
    private String _m_ConnectIP;
    private int _m_iConnectPort;
    
    /** 接收到的消息相关处理 */
    private ReentrantLock _m_rRecMessageMutex;
    private LinkedList<ByteBuffer> _m_lRecMessageList;
    
    /** 发送队列锁 */
    private ReentrantLock _m_rSendListMutex;
    /** 需要发送的消息队列 */
    private LinkedList<ByteBuffer> _m_lSendBufferList;
    
    /** 缓存读取字节的位置，长度根据配置设置 */
    private int _m_sBufferLen;
    private ByteBuffer _m_bByteBuffer;
    
    public ALBasicServerSocket(long _id, SocketChannel _channel)
    {
        _m_ID = _id;
        _m_scSocketChannel = _channel;
        
        _m_clListener = null;
        _m_sUserName = null;
        _m_sUserPassword = null;
        
        _m_rRecMessageMutex = new ReentrantLock();
        _m_lRecMessageList = new LinkedList<ByteBuffer>();
        
        _m_rSendListMutex = new ReentrantLock();
        _m_lSendBufferList = new LinkedList<ByteBuffer>();
        
        _m_sBufferLen = 0;
        _m_bByteBuffer = ByteBuffer.allocate(ALBasicServerConf.getInstance().getServerRecBufferLen());
        _m_bByteBuffer.clear();
    }
    
    public long getSocketID() {return _m_ID;}
    public String getUserName() {return _m_sUserName;}
    public String getUserPassword() {return _m_sUserPassword;}
    public _AALBasicServerSocketListener getListener() {return _m_clListener;}
    public String getIP() {return _m_ConnectIP;}
    public int getPort() {return _m_iConnectPort;}
    
    /**************
     * 设置处理对象
     * @param _listener
     */
    public void setListener(_AALBasicServerSocketListener _listener)
    {
        _m_clListener = _listener;
    }

    /**************
     * 设置登录过程完成
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:44:56 PM
     */
    public void setLoginEnd()
    {
        _m_bLoginIng = false;
    }
    
    /********************
     * 将消息添加到发送队列，等待发送
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:42:24 PM
     */
    public void send(ByteBuffer _buf)
    {
        if(null == _m_scSocketChannel || null == _buf || _buf.remaining() == 0)
            return ;
        
        boolean needAddToSendList = false;
        _lockBuf();
        
        //判断当前队列是否有剩余协议
        //当当前无发送消息存在于队列中时，需要将socket添加到对应发送队列中
        if(_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        //先插入长度数据，后插入实际数据
        ByteBuffer lenthBuffer = ByteBuffer.allocate(4);
        lenthBuffer.putInt(_buf.remaining());
        lenthBuffer.flip();
        
        _m_lSendBufferList.add(lenthBuffer);
        _m_lSendBufferList.add(_buf);
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALServerSendSocketMgr.getInstance().addSendSocket(this);
    }
    /*****************
     * 增加函数用于将两段数据作为一个数据包发送。<br>
     * 用于一些拼凑数据发送。（如协议对象发送）<br>
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:46:29 PM
     */
    public void send(ByteBuffer _tmpHeader, ByteBuffer _buf)
    {
        if(null == _m_scSocketChannel || null == _buf || _buf.remaining() == 0)
            return ;
        
        boolean needAddToSendList = false;
        _lockBuf();

        //判断当前队列是否有剩余协议
        //当当前无发送消息存在于队列中时，需要将socket添加到对应发送队列中
        if(_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        //先插入长度数据，后插入实际数据
        ByteBuffer lenthBuffer = ByteBuffer.allocate(4);
        lenthBuffer.putInt(_buf.remaining() + _tmpHeader.remaining());
        lenthBuffer.flip();
        
        _m_lSendBufferList.add(lenthBuffer);
        _m_lSendBufferList.add(_tmpHeader);
        _m_lSendBufferList.add(_buf);
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALServerSendSocketMgr.getInstance().addSendSocket(this);
    }
    
    /*****************
     * 直接将字节数据作为一个完整包发送，不增加长度等消息<br>
     * 用于一些可扩展的发送方式，如Group<br>
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 1:48:07 PM
     */
    public void sendRealDataBuffer(ByteBuffer _buf)
    {
        if(null == _m_scSocketChannel || null == _buf || _buf.remaining() == 0)
            return ;
        
        boolean needAddToSendList = false;
        _lockBuf();

        //判断当前队列是否有剩余协议
        //当当前无发送消息存在于队列中时，需要将socket添加到对应发送队列中
        if(_m_lSendBufferList.isEmpty())
            needAddToSendList = true;

        _m_lSendBufferList.add(_buf);
        
        _unlockBuf();
        
        if(needAddToSendList)
            ALServerSendSocketMgr.getInstance().addSendSocket(this);
    }
    
    /**********************
     * 实际的发送函数，尝试发送尽量多的消息，并判断是否有剩余消息需要发送<br>
     * 发送完成后判断是否有消息未发送完毕<br>
     * 如有消息未发送完毕，需要添加下一轮的发送任务<br>
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:00:56 PM
     */
    protected void _realSendMessage()
    {
        if(null == _m_scSocketChannel)
            return ;

        boolean needAddToSendList = false;
        //是否需要延迟发送，当写入操作无法将数据写入时，需要延迟发送
        //避免因为网速问题导致不断循环发送一个Socket而使Socket占用达到100%
        boolean needDelaySend = false;
        //总体写入数据的长度
        int totalWriteBufLen = 0;
        _lockBuf();

        while(!_m_lSendBufferList.isEmpty())
        {
            //Socket 允许写入操作时
            ByteBuffer buf = _m_lSendBufferList.getFirst();
            
            if(buf.remaining() <= 0)
            {
                ALServerLog.Error("try to send a null buffer");
                ALServerLog.Error("Wrong buffer Src Data:");
                for(int i = 0; i < buf.limit(); i++)
                {
                    ALServerLog.Error(buf.get(i) + " ");
                }
            }
                
            try {
                //统计写入数据的长度
                totalWriteBufLen += _m_scSocketChannel.write(buf);
                
                //判断写入后对应数据的读取指针位置
                if(buf.remaining() <= 0)
                    _m_lSendBufferList.pop();
                else
                    break;
            }
            catch (Exception e)
            {
                ALServerLog.Error("Socket send message error! user[" + _m_sUserName + "]");
                e.printStackTrace();
                ALServerSocketMgr.getInstance().kickUser(this);
                _unlockBuf();
                return ;
            }
        }
        
        //判断总体写入数据的长度
        if(totalWriteBufLen <= 0)
            needDelaySend = true;
        
        //当需要发送队列不为空时，继续添加发送节点
        if(!_m_lSendBufferList.isEmpty())
            needAddToSendList = true;
        
        _unlockBuf();
        
        if(needAddToSendList)
        {
            if(needDelaySend)
            {
                //使用定时任务的定时处理方式进行延迟发送
                ALSynTaskManager.getInstance().regTask(new SynDelaySendTask(this), 50);
            }
            else
            {
                ALServerSendSocketMgr.getInstance().addSendSocket(this);
            }
        }
    }

    /********************
     * 将消息添加到待处理消息队列中
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:13:48 PM
     */
    protected void _addRecMessage(ByteBuffer _mes)
    {
        if(null == _m_scSocketChannel)
            return ;
        
        boolean needAddDealTask = false;
        _lockRecMes();
        
        //判断处理消息队列中是否已经有数据
        //如无数据，表明需要将socket添加到对应处理队列中
        if(_m_lRecMessageList.isEmpty())
            needAddDealTask = true;
        
        _m_lRecMessageList.add(_mes);
        
        _unlockRecMes();
        
        if(needAddDealTask)
            ALSynTaskManager.getInstance().regTask(new SynReceiveMessageTask(this));
    }
    
    /**********************
     * 消息处理函数，将队列中第一个消息取出并处理。根据取出时消息队列剩余数量决定是否在任务队列末尾添加对应的处理任务<br>
     * 此函数在同步任务中处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:17:02 PM
     */
    protected void _dealRecMessage()
    {
        if(null == _m_scSocketChannel)
            return ;

        ByteBuffer buf = null;
        boolean needAddDealTask = false;
        
        _lockRecMes();

        if(!_m_lRecMessageList.isEmpty())
        {
            //非空才能取出第一个消息对象
            buf = _m_lRecMessageList.getFirst();
        }
        
        _unlockRecMes();

        if(null != buf)
        {
            //处理消息
            try
            {
                _m_clListener.receiveMsg(buf);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        _lockRecMes();

        //必须在处理完成后才删除BUF，否在在处理过程中可能因为插入接收数据导致同时开启第2个任务进行消息处理
        _m_lRecMessageList.pop();
        
        //当需要发送队列不为空时，继续添加发送节点
        if(!_m_lRecMessageList.isEmpty())
            needAddDealTask = true;
        
        _unlockRecMes();
        
        if(needAddDealTask)
            ALSynTaskManager.getInstance().regTask(new SynReceiveMessageTask(this));
    }
    
    /*********************
     * 接收函数中将接收到的字节放入消息缓冲区中，根据消息组装原理进行消息拆分<br>
     * 并将拆分后的成型消息放入待处理消息队列中
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:23:16 PM
     */
    protected void _socketReceivingMessage(ByteBuffer _buf)
    {
        //将数据放入缓冲中
        try
        {
            _m_bByteBuffer.put(_buf);
        }
        catch (BufferOverflowException e)
        {
            //长度不足，此时提示
            ALServerLog.Error("_socketReceivingMessage length is too long, Socket Buffer need more!");
            _m_bByteBuffer.put(_buf.array(), 0, _m_bByteBuffer.remaining());
            //放置待插入数据中指针位置为缓冲区读取指针，可在数据处理后再将数据放入缓存一次
            _buf.position(_m_bByteBuffer.remaining());
        }
        
        if(0 == _m_sBufferLen)
        {
            //尚未读取长度前
            if(_m_bByteBuffer.position() >= 4)
            {
                //当缓冲中字节大于2时可获取对应的消息长度
                _m_sBufferLen = _m_bByteBuffer.getInt(0);
            }
        }
        
        //当长度有效则判断是否到达消息末尾
        int bufLen = _m_bByteBuffer.position();
        int startPos = 0;
        while(0 != _m_sBufferLen && bufLen >= startPos + _m_sBufferLen + 4)
        {
            //到达消息末尾，将消息取出，并清除缓存消息
            ByteBuffer message = ByteBuffer.allocate(_m_sBufferLen);
            message.put(_m_bByteBuffer.array(), startPos + 4, _m_sBufferLen);
            message.flip();
            
            //设置新的开始位置
            startPos = startPos + _m_sBufferLen + 4;
            
            //添加消息
            if(null != _m_clListener)
            {
                _addRecMessage(message);
            }
            else
            {
                //处理登录操作
                _login(message);
            }
            
            //根据长度设置对应消息长度
            if(bufLen - startPos > 4)
            {
                //当缓冲中字节大于2时可获取对应的消息长度
                _m_sBufferLen = _m_bByteBuffer.getInt(startPos);
            }
            else
            {
                _m_sBufferLen = 0;
                break;
            }
        }
        
        //判断数据经过了操作
        //如数据经过了操作需要将剩余数据重新拷贝放入缓存
        if(startPos != 0)
        {
        	ByteBuffer tmpBuf = ByteBuffer.allocate(bufLen - startPos);
        	tmpBuf.put(_m_bByteBuffer.array(), startPos, bufLen - startPos);
        	tmpBuf.flip();
        	
        	_m_bByteBuffer.clear();
        	_m_bByteBuffer.put(tmpBuf);
        }
        
        //如原先缓存数据未完全放入，此时将剩余数据放入
        if(_buf.remaining() > 0)
        {
            _m_bByteBuffer.put(_buf);
        }
    }
    
    /********************
     * 处理Socket的连接创建操作
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:30:22 PM
     */
    protected void _login(ByteBuffer _loginBuf)
    {
        if(null == _m_scSocketChannel || _m_bLoginIng || null != _m_clListener)
            return ;

        C2S_BasicClientVerifyInfo info = new C2S_BasicClientVerifyInfo();
        try
        {
            info.readPackage(_loginBuf);
        }
        catch (Exception e)
        {
            //读取信息直接异常，则踢出用户
            ALServerSocketMgr.getInstance().kickUser(this);
            return ;
        }
        
        //设置正在登录
        _m_bLoginIng = true;
        
        //还未登录进入登录流程
        String userName = null;
        String userPassword  = null;
        
        userName = info.getUserName();
        userPassword = info.getUserPassword();
        
        _m_sUserName = userName;
        _m_sUserPassword = userPassword;
        
        if(null != _m_scSocketChannel)
        {
            _m_ConnectIP = _m_scSocketChannel.socket().getInetAddress().getHostAddress();
            _m_iConnectPort = _m_scSocketChannel.socket().getPort();
        }

        //开启异步处理登录
        ALVerifyObjMgr.getInstance().addVerifySocket(this);
    }

    protected void _removeChannel()
    {
        _m_scSocketChannel = null;
    }
    
    protected SocketChannel _getSocketChannel()
    {
        return _m_scSocketChannel;
    }
    
    protected void _lockBuf()
    {
        _m_rSendListMutex.lock();
    }
    protected void _unlockBuf()
    {
        _m_rSendListMutex.unlock();
    }
    
    protected void _lockRecMes()
    {
        _m_rRecMessageMutex.lock();
    }
    protected void _unlockRecMes()
    {
        _m_rRecMessageMutex.unlock();
    }
}

package ALBasicServer.ALSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import ALBasicServer.ALVerifyObj.ALVerifyObjMgr;
import ALBasicServer.ALVerifyObj._IALVerifyFun;
import ALServerLog.ALServerLog;

/***********************
 * 服务器监听端口的处理函数类，部分操作由于需要访问内部函数，因此独立
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 4:39:40 PM
 */
public class ALServerSocketListenFunction
{
    /** 分配Socket连接的ID标志 */
    private static long g_socketSerialize = 1;
    
    /**********************
     * 开启服务器监听端口，并对所有返回数据做处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:34:07 PM
     */
    public static void startServer(int _port, int _recBuffLen, _IALVerifyFun _verifyObj)
    {
        InetSocketAddress socketAddress = new InetSocketAddress(_port);
        Selector selector = null;
        
        //注册验证处理对象
        int verifyObjIdx = ALVerifyObjMgr.getInstance().regVerifyObj(_verifyObj);
        
        //创建接收BUF
        ByteBuffer recBuffer = ByteBuffer.allocate(_recBuffLen * 2);
        
        try {
            //创建事件响应对象
            selector = Selector.open();
            //创建服务器channel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            //绑定socket
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(socketAddress);
            
            //注册处理事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e)
        {
            //开启端口失败
            ALServerLog.Fatal("Open server port error!!");
            e.printStackTrace();
            return ;
        }
        
        ALServerLog.Info("Server Start socket...");
        
        //开启循环处理Socket事件
        while(true)
        {
            try
            {
                selector.select();
            } catch (Exception e) {
                ALServerLog.Fatal("Server port select event error!!");
                e.printStackTrace();
            }
            
            //循环获取到的事件
            Set<SelectionKey> readyKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iter = readyKeySet.iterator();
            while(iter.hasNext())
            {
                SelectionKey key = (SelectionKey)iter.next();
                //移除已经取出的事件
                iter.remove();

                try
                {
                    if(key.isAcceptable())
                    {
                        //收到一个接受连接的操作
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                        try
                        {
                            SocketChannel client = serverSocketChannel.accept();
                            ALServerLog.Info("Accept a client connection ");
                            
                            client.configureBlocking(false);
                            
                            ALBasicServerSocket newSocket = new ALBasicServerSocket(_getSocketID(), verifyObjIdx, client, _recBuffLen);
                            //将对象及其ID注册
                            ALServerSocketMgr.getInstance().regSocket(newSocket);
                            
                            //注册读取事件，读取操作由server socket channel处理，写入操作可直接对socket channel进行操作
                            client.register(selector, SelectionKey.OP_READ);
                        }
                        catch (Exception e)
                        {
                            ALServerLog.Fatal("Server port accept error!!");
                            e.printStackTrace();
                        }
                    }
                    else if(key.isReadable())
                    {
                        //收到数据
                        SocketChannel socketChannel = (SocketChannel)key.channel();
    
                        //读取数据
                        try
                        {
                            recBuffer.clear();
                            //此时进行读取，当读取的包大小超过存储区域则可能导致数据读取不全
                            int recLen = socketChannel.read(recBuffer);
                            //设置限制，并将读取位置归0
                            recBuffer.flip();
                            
                            if(recLen <= 0)
                            {
                                if(recLen < 0)
                                {
                                    //尝试在对应Socket存储结构体中删除对应Socket
                                    ALServerSocketMgr.getInstance().unregSocket(socketChannel);
                                }
                                continue;
                            }
    
                            //尝试在已注册对象中查询对应Socket
                            ALBasicServerSocket socket = ALServerSocketMgr.getInstance().getSocket(socketChannel);
                            
                            if(null == socket)
                                continue;
                            
                            //处理消息读取
                            socket._socketReceivingMessage(recBuffer);
                        }
                        catch (IOException e)
                        {
                            //尝试在对应Socket存储结构体中删除对应Socket
                            ALServerSocketMgr.getInstance().unregSocket(socketChannel);
                        }
                    }
                }
                catch (Exception e) {
                    
                }
            }
        }
    }
    
    /**************
     * 获取新的SocketID
     * @return
     */
    protected static synchronized long _getSocketID()
    {
        return g_socketSerialize++;
    }
}

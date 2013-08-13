package ALBasicServer.ALVerifyObj;

import java.util.Hashtable;

import ALBasicServer.ALServerAsynTask.ALAsynTaskManager;
import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALSocket.ALBasicServerSocket;
import ALBasicServer.ALSocket._AALBasicServerSocketListener;
import ALBasicServer.ALSocket.ALServerSocketMgr;
import BasicServer.S2C_BasicClientVerifyResult;

/******************
 * 连接Socket验证的管理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 2:36:33 PM
 */
public class ALVerifyObjMgr
{
    private static int g_verifySerialize = 1;
    private static ALVerifyObjMgr g_instance = new ALVerifyObjMgr();
    
    public static ALVerifyObjMgr getInstance()
    {
        if(null == g_instance)
            g_instance = new ALVerifyObjMgr();
        
        return g_instance;
    }
    
    private Hashtable<Integer, ALBasicServerSocket> m_htVerifySocket;
    
    public ALVerifyObjMgr()
    {
        m_htVerifySocket = new Hashtable<Integer, ALBasicServerSocket>();
    }
    
    /****************
     * 添加一个需要验证的Socket对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:37:43 PM
     */
    public void addVerifySocket(ALBasicServerSocket _socket)
    {
        int serialize = _getSerizlize();
        
        m_htVerifySocket.put(serialize, _socket);
        
        //开启一个异步任务用于验证Socket的合法性
        ALAsynTaskManager.getInstance().regSysTask(new AsynRun_UserLoginTask(serialize, _socket.getUserName(), _socket.getUserPassword()));
        
        //开启定时任务检测是否登录超时，超时则直接按照失败处理
        ALSynTaskManager.getInstance().regTask(new SynCheckVerifyLoginTimeOutTask(serialize), 30000);
    }
    
    /*************
     * 带入处理序列号，设置处理结果。
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 2:41:52 PM
     */
    protected void _comfirmVerifyResult(int _serialize, _AALBasicServerSocketListener _listener)
    {
        ALBasicServerSocket socket = m_htVerifySocket.remove(_serialize);
        
        if(null == socket)
            return ;
        
        if(null == _listener)
        {
            ALServerSocketMgr.getInstance().kickUser(socket);
        }
        else
        {
            //设置Socket和Listener相互的关联
            socket.setListener(_listener);
            _listener.setSocket(socket);
            
            socket.setLoginEnd();

            //创建返回协议
            S2C_BasicClientVerifyResult retMsg = new S2C_BasicClientVerifyResult();
            retMsg.setUserName(socket.getUserName());
            retMsg.setSocketID(socket.getSocketID());

            //发送登录完成的协议返回
            _listener.send(retMsg.makePackage());
            
            _listener.login();
        }
    }
    
    /***************
     * 获取新的验证序列号
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 3:14:37 PM
     */
    private synchronized int _getSerizlize()
    {
        return g_verifySerialize++;
    }
}

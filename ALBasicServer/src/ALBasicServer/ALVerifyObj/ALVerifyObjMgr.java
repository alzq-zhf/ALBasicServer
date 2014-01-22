package ALBasicServer.ALVerifyObj;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

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
    
    private Hashtable<Integer, ALBasicServerSocket> _m_htVerifySocket;
    /** 验证处理对象的队列 */
    private ReentrantLock _m_mutex;
    private ArrayList<_IALVerifyFun> _m_arrVerifyFunList;
    
    public ALVerifyObjMgr()
    {
        _m_htVerifySocket = new Hashtable<Integer, ALBasicServerSocket>();
        
        _m_mutex = new ReentrantLock();
        _m_arrVerifyFunList = new ArrayList<_IALVerifyFun>();
    }
    
    /************
     * 注册一个验证处理对象，并返回验证处理对象的下标
     * 
     * @author alzq.z
     * @time   Jan 22, 2014 11:50:33 PM
     */
    public int regVerifyObj(_IALVerifyFun _verifyFun)
    {
        _m_mutex.lock();
        
        int newIdx = _m_arrVerifyFunList.size();
        _m_arrVerifyFunList.add(_verifyFun);
        
        _m_mutex.unlock();
        
        return newIdx;
    }
    
    /************
     * 获取验证处理的对象
     * 
     * @author alzq.z
     * @time   Jan 22, 2014 11:50:33 PM
     */
    public _IALVerifyFun getVerifyObj(int _verifyFunIdx)
    {
        _m_mutex.lock();
        
        try
        {
            return _m_arrVerifyFunList.get(_verifyFunIdx);
        }
        finally
        {
            _m_mutex.unlock();
        }
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
        
        _m_htVerifySocket.put(serialize, _socket);
        
        //开启一个异步任务用于验证Socket的合法性
        ALAsynTaskManager.getInstance().regSysTask(new AsynRun_UserLoginTask(serialize, _socket));
        
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
        _comfirmVerifyResult(_serialize, _listener, "");
    }
    protected void _comfirmVerifyResult(int _serialize, _AALBasicServerSocketListener _listener, String _customRetMsg)
    {
        ALBasicServerSocket socket = _m_htVerifySocket.remove(_serialize);
        
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
            retMsg.setSocketID(socket.getSocketID());
            retMsg.setCustomRetMsg(_customRetMsg);

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

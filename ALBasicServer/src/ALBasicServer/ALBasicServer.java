package ALBasicServer;

import ALBasicServer.ALServerAsynTask.ALAsynTaskManager;
import ALBasicServer.ALSocket.ALServerSocketListenFunction;
import ALBasicServer.ALThread.ALThreadManager;
import ALBasicServer.ALVerifyObj._IALVerifyFun;
import ALServerLog.ALServerLog;

public class ALBasicServer
{
    /** 是否成功初始化 */
    private static boolean g_inited = false;
    /** 注册的验证对象 */
    private static _IALVerifyFun g_CosVerifyObj = null;
    
    /**
     * 服务器模块启动初始化函数<br>
     * 开启基本的服务器逻辑任务处理线程<br>
     * 异步任务处理线程<br>
     * 以及相关发送消息线程和Socket监听循环<br>
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:33:52 PM
     */
    public static void initBasicServer(int _asynTaskDealThreadCount)
    {
        if(g_inited)
        {
        	ALServerLog.Fatal("Please don't try to repeat init AL Basic Server model!!");
            new Exception().printStackTrace();
            return ;
        }
        
        g_inited = true;

        //读取配置文件并初始化相关环境操作
        if(ALBasicServerConf.getInstance().init())
        {
            //初始化日志配置
            ALServerLog.initALServerLog();
            
            //输出服务器基本日志
            ALServerLog.Fatal(ALBasicServerConf.getInstance().getServerTag() + " start initialize...");
            ALServerLog.Fatal("Server Log Level - " + ALServerLog.getLogLevel());
            
            //开启定时任务的检测线程
            ALThreadManager.getInstance().createTimingTaskCheckThread();
            
            //开启对应的普通任务执行线程
            ALServerLog.Fatal("Server Start Task Thread Count - " + ALBasicServerConf.getInstance().getSynTaskThreadNum());
            for(int i = 0; i < ALBasicServerConf.getInstance().getSynTaskThreadNum(); i++)
            {
                ALThreadManager.getInstance().createTaskDealThread();
            }

            //开启对应的端口发送执行线程
            ALServerLog.Fatal("Server Start Socket Send Thread Count - " + ALBasicServerConf.getInstance().getSendThreadNum());
            for(int i = 0; i < ALBasicServerConf.getInstance().getSendThreadNum(); i++)
            {
                ALThreadManager.getInstance().createSocketSendThread();
            }

            //开启对应的异步任务执行线程
            ALServerLog.Fatal("Server Start AsyncTask Thread Count - " + _asynTaskDealThreadCount);
            ALAsynTaskManager.getInstance().init(_asynTaskDealThreadCount);
        }

        if(ALBasicServerConf.getInstance().getCheckMutex())
        {
            //获得当前线程ID
            long threadID = Thread.currentThread().getId();
            ALThreadManager.getInstance().regThread(threadID);
        }
    }
    
    /*********************
     * 设置验证对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:33:57 PM
     */
    public static void regVerifyObj(_IALVerifyFun _obj)
    {
        g_CosVerifyObj = _obj;
    }
    
    /************
     * 获取验证对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:34:01 PM
     */
    public static _IALVerifyFun getVerifyObj()
    {
        return g_CosVerifyObj;
    }
    
    /**********************
     * 开启服务器监听端口，并对所有返回数据做处理
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 4:34:07 PM
     */
    public static void startServer()
    {
        ALServerSocketListenFunction.startServer();
    }
}

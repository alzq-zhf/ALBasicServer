package ALBasicClient;

import ALServerLog.ALServerLog;

public class ALBasicClient
{
    /** 是否成功初始化 */
    private static boolean g_inited = false;
    
    /************************
     * 初始化相关客户端环境
     */
    public static void init()
    {
        if(g_inited)
        {
            ALServerLog.Fatal("Please don't try to repeat init AL Basic Client model!!");
            new Exception().printStackTrace();
            return ;
        }
        
        g_inited = true;
        
        if(ALBasicClientConf.getInstance().init())
        {
            //初始化日志配置
            ALServerLog.initALServerLog();
            
            //开启对应的端口发送执行线程
            ALServerLog.Fatal("Start Client Socket Send Thread Count - " + ALBasicClientConf.getInstance().getSendThreadNum());
            for(int i = 0; i < ALBasicClientConf.getInstance().getSendThreadNum(); i++)
            {
                ALBasicClientSendThreadMgr.getInstance().createSocketSendThread();
            }
        }
    }
}

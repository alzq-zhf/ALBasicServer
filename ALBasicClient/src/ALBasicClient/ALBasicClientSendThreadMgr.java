package ALBasicClient;


/***********************
 * 客户端对象发送消息的线程控制管理对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 9:53:09 PM
 */
public class ALBasicClientSendThreadMgr
{
    private static ALBasicClientSendThreadMgr g_instance = new ALBasicClientSendThreadMgr();
    
    public static ALBasicClientSendThreadMgr getInstance()
    {
        if(null == g_instance)
            g_instance = new ALBasicClientSendThreadMgr();
        
        return g_instance;
    }
    
    protected ALBasicClientSendThreadMgr()
    {
    }
    
    /***************
     * 开启Socket发送线程
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 9:53:49 PM
     */
    public void createSocketSendThread()
    {
        CosClientSocketSendThread socketSendThread = new CosClientSocketSendThread();
        //开启线程
        socketSendThread.start();
    }
}

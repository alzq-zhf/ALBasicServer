package ALBasicClient;

import ALBasicServer.ALTask._IALSynTask;

/****************
 * 客户端消息接收对象的消息处理任务对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Mar 4, 2013 10:28:39 PM
 */
public class ALSynServerClientDealMessageTask implements _IALSynTask
{
    private _AALBasicServerClientListener clientListener;
    
    public ALSynServerClientDealMessageTask(_AALBasicServerClientListener _clientListener)
    {
        clientListener = _clientListener;
    }
    
    @Override
    public void run()
    {
        if(null == clientListener)
            return ;
        
        clientListener.dealMessage();
    }
}

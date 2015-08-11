package ALBasicServer.ALSocket;

import ALBasicServer.ALTask._IALSynTask;

/******************
 * 当对应Socket无法发送数据时，使用此任务延迟加入发送队列
 * @author alzq
 *
 */
public class SynDelaySendTask implements _IALSynTask
{
    private ALBasicServerSocket socket;
    
    public SynDelaySendTask(ALBasicServerSocket _socket)
    {
        socket = _socket;
    }

    @Override
    public void run()
    {
        ALServerSendSocketMgr.getInstance().addSendSocket(socket);
    }

}

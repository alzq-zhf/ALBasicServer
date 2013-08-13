package ALBasicServer.ALSocket;

import ALBasicServer.ALTask._IALSynTask;


public class SynReceiveMessageTask implements _IALSynTask
{
    private ALBasicServerSocket _m_csSocket;
    
    public SynReceiveMessageTask(ALBasicServerSocket _socket)
    {
        _m_csSocket = _socket;
    }
    
    @Override
    public void run()
    {
        if(null == _m_csSocket)
            return ;
        
        //处理消息队列中第一个消息
        //在任务中处理不占用发送接收线程
        //同时不事先把消息取出可以保证消息处理的有序性
        _m_csSocket._dealRecMessage();
    }

}

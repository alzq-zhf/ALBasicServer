package ALBasicServer.ALSocket;

import ALBasicServer.ALTask._IALSynTask;

public class SynDisconnectTask implements _IALSynTask
{
    private ALBasicServerSocket _m_csSocket;
    
    public SynDisconnectTask(ALBasicServerSocket _socket)
    {
        _m_csSocket = _socket;
    }
    
    @Override
    public void run()
    {
        if(null == _m_csSocket)
            return ;
        
        _AALBasicServerSocketListener listener = _m_csSocket.getListener();
        
        if(null == listener)
            return ;
        
        listener.disconnect();
        
        //设置Socket为NULL
        listener.setSocket(null);
    }

}

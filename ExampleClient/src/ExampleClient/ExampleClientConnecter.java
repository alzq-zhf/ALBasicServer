package ExampleClient;

import java.nio.ByteBuffer;

import ALBasicClient._AALBasicServerClientListener;
import ALBasicCommon.ALBasicCommonFun;
import ALServerLog.ALServerLog;

public class ExampleClientConnecter extends _AALBasicServerClientListener {

	public ExampleClientConnecter(String _serverIP, int _serverPort) {
		super(_serverIP, _serverPort);
	}

	@Override
	public void ConnectFail() {
		ALServerLog.Error("ConnectFail");
	}

	@Override
	public void Disconnect() {
		ALServerLog.Error("Disconnect");
	}

	@Override
	public void LoginFail() {
		ALServerLog.Error("LoginFail");
	}

	@Override
	public void LoginSuc(String _customMsg) {
		ALServerLog.Error("LoginSuc");
		
		//send test msg
		send(ALBasicCommonFun.getStringBuf("client message"));
	}

	@Override
	public void _dealMes(ByteBuffer _msg)
	{
		ALServerLog.Error("receiveMes: " + ALBasicCommonFun.getString(_msg));
	}


}

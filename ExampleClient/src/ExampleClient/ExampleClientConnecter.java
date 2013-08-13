package ExampleClient;

import java.nio.ByteBuffer;

import ALBasicClient._AALBasicClientListener;
import ALBasicCommon.ALBasicCommonFun;
import ALServerLog.ALServerLog;

public class ExampleClientConnecter extends _AALBasicClientListener {

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
	public void LoginSuc() {
		ALServerLog.Error("LoginSuc");
		
		//发送一个字符串消息给服务器
		send(ALBasicCommonFun.getStringBuf("client message"));
	}

	@Override
	public void receiveMes(ByteBuffer arg0) {
		ALServerLog.Error("receiveMes: " + ALBasicCommonFun.getString(arg0));
	}


}

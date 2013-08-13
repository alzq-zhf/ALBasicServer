package ExampleServer;

import java.nio.ByteBuffer;

import ALBasicCommon.ALBasicCommonFun;
import ALBasicServer.ALServerAsynTask.ALAsynTaskManager;
import ALBasicServer.ALServerSynTask.ALSynTaskManager;
import ALBasicServer.ALSocket._AALBasicServerSocketListener;
import ALServerLog.ALServerLog;

public class ExampleServerSocketListener extends _AALBasicServerSocketListener {

	@Override
	public void disconnect() {
		ALServerLog.Error("disconnect");
	}

	@Override
	public void login() {
		ALServerLog.Error("login");
	}

	@Override
	public void receiveMsg(ByteBuffer arg0) {
		ALServerLog.Error("receiveMsg: " + ALBasicCommonFun.getString(arg0));
		
		//�����߼�����
		ALSynTaskManager.getInstance().regTask(new ExampleSynTask());
		
		//�����ӳ��߼�����
		ALSynTaskManager.getInstance().regTask(new ExampleDelaySynTask(), 3000);
		
		//�����첽����2�ַ�ʽ
		ALAsynTaskManager.getInstance().regTask(0, new ExampleAsynRunTask());
		ALAsynTaskManager.getInstance().regTask(0, new ExampleAsynCallTask(), new ExampleAsynCallbackTask());
		
		//������Ϣ
		send(ALBasicCommonFun.getStringBuf("server message"));
	}

}

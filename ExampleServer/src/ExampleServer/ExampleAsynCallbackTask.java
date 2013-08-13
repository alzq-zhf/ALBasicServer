package ExampleServer;

import ALBasicServer.ALTask._IALAsynCallBackTask;
import ALServerLog.ALServerLog;

public class ExampleAsynCallbackTask implements _IALAsynCallBackTask<Integer> {

	@Override
	public void dealFail() {
		ALServerLog.Error("asyn task fail");
	}

	@Override
	public void dealSuc(Integer arg0) {
		ALServerLog.Error("asyn task suc: " + arg0);
	}


}

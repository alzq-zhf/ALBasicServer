package ExampleServer;

import ALBasicServer.ALTask._IALAsynRunnableTask;
import ALServerLog.ALServerLog;

public class ExampleAsynRunTask implements _IALAsynRunnableTask {

	@Override
	public void run() {
		ALServerLog.Error("asyn run task");
	}


}

package ExampleServer;

import ALBasicServer.ALTask._IALSynTask;
import ALServerLog.ALServerLog;

public class ExampleDelaySynTask implements _IALSynTask {

	@Override
	public void run() {
		ALServerLog.Error("syn delay task");
	}


}

package ExampleServer;

import ALBasicServer.ALTask._IALAsynCallTask;
import ALServerLog.ALServerLog;

public class ExampleAsynCallTask implements _IALAsynCallTask<Integer> {

	@Override
	public Integer dealAsynTask() {
		ALServerLog.Error("asyn task");
		
		return 3;
	}


}

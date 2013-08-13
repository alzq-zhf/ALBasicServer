package ExampleServer;

import ALBasicServer.ALVerifyObj.ALVerifyDealerObj;
import ALBasicServer.ALVerifyObj._IALVerifyFun;

public class ExampleServerVerifyObj implements _IALVerifyFun {

	@Override
	public void verifyIdentity(ALVerifyDealerObj arg0, String arg1, String arg2) {
		//示例不进行验证，直接返回客户端处理对象
		arg0.comfirmResult(new ExampleServerSocketListener());
	}

}

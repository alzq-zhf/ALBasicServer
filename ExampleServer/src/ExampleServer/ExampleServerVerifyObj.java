package ExampleServer;

import ALBasicServer.ALVerifyObj.ALVerifyDealerObj;
import ALBasicServer.ALVerifyObj._IALVerifyFun;
import ALServerLog.ALServerLog;

public class ExampleServerVerifyObj implements _IALVerifyFun {

	@Override
	public void verifyIdentity(ALVerifyDealerObj _verifyDealer, int _clientType, String _userName
            , String _userPassword, String _customMsg) {
		
		ALServerLog.Error("Rec Verify Custom Message: " + _customMsg);
		
		//示例不进行验证，直接返回客户端处理对象
		_verifyDealer.comfirmResult(new ExampleServerSocketListener());
	}

}

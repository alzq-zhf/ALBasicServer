package ExampleServer;

import ALBasicServer.ALVerifyObj.ALVerifyDealerObj;
import ALBasicServer.ALVerifyObj._IALVerifyFun;

public class ExampleServerVerifyObj implements _IALVerifyFun {

	@Override
	public void verifyIdentity(ALVerifyDealerObj arg0, String arg1, String arg2) {
		//ʾ����������֤��ֱ�ӷ��ؿͻ��˴������
		arg0.comfirmResult(new ExampleServerSocketListener());
	}

}

package ExampleServer;

import ALBasicServer.ALBasicServer;

public class ExampleServer {

	/**
	 * ʾ���������Ŀ���������
	 * @param args
	 */
	public static void main(String[] args) {
		//��ʼ���������������ã�������1���첽�������߳�
		ALBasicServer.initBasicServer(1);

		//ע��һ����֤��Ϣ�Ĵ������
		ALBasicServer.regVerifyObj(new ExampleServerVerifyObj());
		
		//��ʼ���м�������
		ALBasicServer.startServer();
	}

}

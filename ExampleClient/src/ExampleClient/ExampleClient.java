package ExampleClient;

import ALBasicClient.ALBasicClient;

public class ExampleClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//��ʼ�������ͻ����������
		ALBasicClient.init();
		
		//�����ͻ��˶��󲢽�������
		ExampleClientConnecter connecter = new ExampleClientConnecter("127.0.0.1", 1001);
		connecter.login("", "");
	}

}

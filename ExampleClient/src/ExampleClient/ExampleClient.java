package ExampleClient;

import ALBasicClient.ALBasicClient;

public class ExampleClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//初始化基本客户端配置相关
		ALBasicClient.init();
		
		//创建客户端对象并进行连接
		ExampleClientConnecter connecter = new ExampleClientConnecter("127.0.0.1", 1001);
		connecter.login("", "");
	}

}

package ExampleServer;

import ALBasicServer.ALBasicServer;

public class ExampleServer {

	/**
	 * 示例服务器的开启主函数
	 * @param args
	 */
	public static void main(String[] args) {
		//初始化基本服务器配置，并开启1个异步任务处理线程
		ALBasicServer.initBasicServer(1);

		//注册一个验证信息的处理对象
		ALBasicServer.regVerifyObj(new ExampleServerVerifyObj());
		
		//开始进行监听操作
		ALBasicServer.startServer();
	}

}

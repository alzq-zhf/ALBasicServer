package ALBasicServer.ALTask;

/********************
 * 服务器架构中的逻辑任务接口对象，需要进行任务操作的对象通过实现本接口的函数实现具体的函数操作过程。
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 18, 2013 10:50:58 PM
 */
public interface _IALSynTask
{
    /*****************
     * 任务的执行函数
     */
    public void run();
}

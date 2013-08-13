package ALBasicServer.ALTask;

/**********************
 * 服务器架构中异步任务的执行接口类，需要与回调配套使用
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 18, 2013 10:53:01 PM
 */
public interface _IALAsynCallTask<T>
{
    /**************************
     * 异步任务中处理实际逻辑的处理函数
     * @return
     */
    public abstract T dealAsynTask();
}

package ALBasicServer.ALTask;


/******************
 * 服务器架构中异步任务的回调执行接口类，需要与异步任务的执行接口类一同使用
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 18, 2013 10:54:14 PM
 */
public interface _IALAsynCallBackTask<T>
{
    /****************
     * 异步成功后的回调处理函数
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:54:44 PM
     */
    public abstract void dealSuc(T _obj);

    /*****************
     * 异步处理失败后的回调处理函数
     * @param _obj
     */
    public abstract void dealFail();
}

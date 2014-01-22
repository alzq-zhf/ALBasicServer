package ALBasicServer.ALServerCmd;

/********************
 * 接收系统命令行处输入的文字，并进行相关处理的处理类
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Oct 5, 2013 11:04:32 AM
 */
public interface _IALBasicServerCmdDealer
{
    /*********************
     * 根据输入的命令行进行处理
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:05:24 AM
     */
    public abstract void dealCmd(String _cmd);
}

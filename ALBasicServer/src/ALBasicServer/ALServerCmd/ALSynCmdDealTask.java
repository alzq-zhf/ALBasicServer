package ALBasicServer.ALServerCmd;

import ALBasicServer.ALTask._IALSynTask;

/*********************
 * 处理命令行中输入的命令的具体执行任务
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Oct 5, 2013 11:25:03 AM
 */
public class ALSynCmdDealTask implements _IALSynTask
{

    @Override
    public void run()
    {
        //执行命令行的处理操作
        ALCmdDealerManager.getInstance()._dealCmd();
    }

}

package ALBasicServer.ALServerCmd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALServerSynTask.ALSynTaskManager;

/**********************
 * 命令行处理对象的注册处理函数
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Oct 5, 2013 11:06:51 AM
 */
public class ALCmdDealerManager
{
    private static ALCmdDealerManager _g_instance = new ALCmdDealerManager();
    public static ALCmdDealerManager getInstance()
    {
        if(null == _g_instance)
            _g_instance = new ALCmdDealerManager();
        
        return _g_instance;
    }
    
    /** 注册的命令行处理对象队列 */
    private ArrayList<_IALBasicServerCmdDealer> _m_lCmdDealerList;
    /** 处理对象的队列锁 */
    private ReentrantLock _m_dealerMutex;
    
    /** 已经输入的命令行数据存储 */
    private LinkedList<String> _m_lCmdList;
    /** 处理内容队列锁 */
    private ReentrantLock _m_cmdMutex;
    
    protected ALCmdDealerManager()
    {
        _m_lCmdDealerList = new ArrayList<_IALBasicServerCmdDealer>();
        _m_dealerMutex = new ReentrantLock();
        
        _m_lCmdList = new LinkedList<>();
        _m_cmdMutex = new ReentrantLock();
    }
    
    /****************
     * 增加命令行对象
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:36:33 AM
     */
    public void addCmd(String _cmd)
    {
        //添加到命令行队列中，并判断是否需要开启任务
        _lockCmd();
        
        boolean needStartTask = false;
        
        if(_m_lCmdList.isEmpty())
            needStartTask = true;
        
        _m_lCmdList.add(_cmd);
        
        _unlockCmd();
        
        //原队列为空则注册任务开启对应操作的执行
        if(needStartTask)
            ALSynTaskManager.getInstance().regTask(new ALSynCmdDealTask());
    }
    
    /******************
     * 获取当前处理对象的队列
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:49:21 AM
     */
    public ArrayList<_IALBasicServerCmdDealer> getDealerList()
    {
        _lockDealer();
        
        ArrayList<_IALBasicServerCmdDealer> resList = _m_lCmdDealerList;
        
        _unlockDealer();
        
        return resList;
    }
    
    /***************
     * 注册命令处理对象队列
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:36:50 AM
     */
    public void regDealer(_IALBasicServerCmdDealer _dealer)
    {
        _lockDealer();
        
        ArrayList<_IALBasicServerCmdDealer> newCmdDealerList = 
                new ArrayList<_IALBasicServerCmdDealer>(_m_lCmdDealerList.size() + 1);
        
        newCmdDealerList.addAll(_m_lCmdDealerList);
        newCmdDealerList.add(_dealer);
        
        //替换队列
        _m_lCmdDealerList = newCmdDealerList;
        
        _unlockDealer();
    }
    
    /***************
     * 注销命令处理对象队列
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:36:50 AM
     */
    public void unregDealer(_IALBasicServerCmdDealer _dealer)
    {
        _lockDealer();
        
        ArrayList<_IALBasicServerCmdDealer> newCmdDealerList = 
                new ArrayList<_IALBasicServerCmdDealer>(_m_lCmdDealerList.size());
        
        newCmdDealerList.addAll(_m_lCmdDealerList);
        
        //移除处理对象
        newCmdDealerList.remove(_dealer);
        
        //替换队列
        _m_lCmdDealerList = newCmdDealerList;
        
        _unlockDealer();
    }
    
    /****************
     * 处理命令行操作对象
     * 
     * @author alzq.z
     * @time   Oct 5, 2013 11:36:33 AM
     */
    protected void _dealCmd()
    {
        String cmd = null;
        boolean needStartTask = false;
        
        //添加到命令行队列中，并判断是否需要开启任务
        _lockCmd();
        
        if(!_m_lCmdList.isEmpty())
        {
            //取第一个命令行
            cmd = _m_lCmdList.getFirst();
        }
        
        _unlockCmd();
        
        //获取处理队列
        ArrayList<_IALBasicServerCmdDealer> dealerList = getDealerList();
        
        if(null != cmd)
        {
            //非空才进行处理
            for(int i = 0; i < dealerList.size(); i++)
            {
                _IALBasicServerCmdDealer dealer = dealerList.get(i);
                
                if(null == dealer)
                    continue;
                
                try
                {
                    dealer.dealCmd(cmd);
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        _lockCmd();
        
        //取出第一个命令行
        _m_lCmdList.pop();
        
        //判断命令行是否为空
        if(!_m_lCmdList.isEmpty())
            needStartTask = true;
        
        _unlockCmd();
        
        //原队列为空则注册任务开启对应操作的执行
        if(needStartTask)
            ALSynTaskManager.getInstance().regTask(new ALSynCmdDealTask());
    }
    
    protected void _lockCmd()
    {
        _m_cmdMutex.lock();
    }
    protected void _unlockCmd()
    {
        _m_cmdMutex.unlock();
    }
    
    protected void _lockDealer()
    {
        _m_dealerMutex.lock();
    }
    protected void _unlockDealer()
    {
        _m_dealerMutex.unlock();
    }
}

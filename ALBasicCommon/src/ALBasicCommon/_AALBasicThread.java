package ALBasicCommon;

import ALBasicCommon.ALBasicEnum.ThreadStat;

/********************
 * 本函数包中基本的线程类对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 20, 2013 10:44:51 PM
 */
public abstract class _AALBasicThread extends Thread
{
    /** 线程状态 */
    private ThreadStat _m_eThreadStat;
    
    public _AALBasicThread()
    {
        _m_eThreadStat = ThreadStat.INIT;
    }
    
    public ThreadStat getThreadStat() {return _m_eThreadStat;}
    
    /******************
     * 线程执行函数
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:46:17 PM
     */
    public final void run()
    {
        _m_eThreadStat = ThreadStat.RUNNING;
        
        _run();

        _m_eThreadStat = ThreadStat.STOP;
    }
    
    /****************
     * 需要重载的线程执行体
     * 
     * @author alzq.z
     * @time   Feb 20, 2013 10:46:45 PM
     */
    protected abstract void _run();
}

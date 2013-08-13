package ALBasicServer.ALThread;

import java.util.concurrent.locks.ReentrantLock;

import ALBasicServer.ALBasicServerConf;
import ALServerLog.ALServerLog;

/*************************
 * city of steam 项目中基本单元对象，单元对象中包含锁定解锁操作，需要自行在线程中控制<br>
 * 每个线程对象同一时间只允许获取到一个锁，超过一个锁的获取操作将引发警告或错误<br>
 * 线程锁根据可能分为可能的三个等级，已经获取某个等级锁的对象只允许获取下一等级对象的锁，如果发现逆序获取锁的现象将产生错误<br>
 * 带入构造函数的等级数字越大表示锁等级越低<br>
 * 对于锁等级的探测监控可进行开关，可在发布版本中关闭该对象<br>
 * 
 * @author alzq
 *
 */
public class ALMutex
{
    /** 存储实际的锁对象 */
    private ReentrantLock _m_lMutex;
    /** 锁对应的优先等级，从0开始，数字越小表示越高级 */
    private int _m_iMutexPriority;
    
    public ALMutex(int _mutexLevel)
    {
        /** 锁对象初始化，带入true表示使用公平队列的加解锁排序方式 */
        _m_lMutex = new ReentrantLock(true);
        _m_iMutexPriority = _mutexLevel;
    }
    
    /****************
     * 获取锁的优先级
     * @return
     */
    public int getPriority()
    {
        return _m_iMutexPriority;
    }

    /***************
     * 增加锁的优先级
     */
    public void addPriority()
    {
        _m_iMutexPriority--;
        
        if(_m_iMutexPriority < 0)
            _m_iMutexPriority = 0;
    }
    public void addPriority(int _deltaPriority)
    {
        _m_iMutexPriority -= _deltaPriority;
        
        if(_m_iMutexPriority < 0)
            _m_iMutexPriority = 0;
    }
    
    /****************
     * 降低锁的优先级
     */
    public void reducePriority()
    {
        _m_iMutexPriority++;
    }
    public void reducePriority(int _deltaPriority)
    {
        _m_iMutexPriority += _deltaPriority;
    }
    
    /******************
     * 锁定操作
     * @throws CosThreadUnregThreadException 
     */
    public void lock()
    {
        //判断是否检查加锁顺序合法性
        if(ALBasicServerConf.getInstance().getCheckMutex())
        {
            //获取当前线程ID
            long curThreadID = Thread.currentThread().getId();
            
            //获取线程锁信息
            ALThreadMutexMgr threadMutexMgr = ALThreadManager.getInstance().getThreadMutexRegister(curThreadID);
            
            if(null == threadMutexMgr)
            {
                ALServerLog.Fatal("Unreg Thread try to lock mutex");
                return ;
            }
            
            if(threadMutexMgr.tryLock(this))
            {
                //仅当加锁成功后才进行锁定操作
                _m_lMutex.lock();
            }
        }
        else
        {
            //不检测则直接加锁
            _m_lMutex.lock();
        }
    }
    
    /******************
     * 解锁操作
     * @throws CosThreadUnregThreadException 
     */
    public void unlock()
    {
        //判断是否检查加锁顺序合法性
        if(ALBasicServerConf.getInstance().getCheckMutex())
        {
            //获取当前线程ID
            long curThreadID = Thread.currentThread().getId();
            
            //获取线程锁信息
            ALThreadMutexMgr threadMutexMgr = ALThreadManager.getInstance().getThreadMutexRegister(curThreadID);
            
            if(null == threadMutexMgr)
            {
                ALServerLog.Fatal("Unreg Thread try to unlock mutex");
                return ;
            }
            
            if(threadMutexMgr.tryUnlock(this))
            {
                //仅当加锁成功后才进行解锁操作
                _m_lMutex.unlock();
            }
        }
        else
        {
            _m_lMutex.unlock();
        }
    }
}

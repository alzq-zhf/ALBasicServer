package ALBasicServer.ALThread;

import java.util.ArrayList;

import ALServerLog.ALServerLog;

/************************
 * 线程中锁对象信息保存控制<br>
 * 存储一个线程获取的锁对象的信息<br>
 * 
 * @author alzq
 *
 */
public class ALThreadMutexMgr
{
    /** 所属线程ID */
    private long _m_lThreadID;
    /** 线程已经获取的锁对象信息存储表 */
    private ArrayList<ALMutexInfo> _m_lThreadMutexList;
    
    public ALThreadMutexMgr(long _threadID)
    {
        _m_lThreadID = _threadID;
        _m_lThreadMutexList = new ArrayList<ALMutexInfo>();
    }
    
    /************
     * 获取线程ID
     * @return
     */
    public long getThreadID()
    {
        return _m_lThreadID;
    }
    
    /********************
     * 尝试对对象进行加锁
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:27:51 PM
     */
    public boolean tryLock(ALMutex _cosObj)
    {
        if(null == _cosObj)
            return false;
        
        //默认当前加锁对象为空，表示无已经加锁的相同对象
        ALMutexInfo mutexInfo = null;
        if(!_isMutexListEmpty())
        {
            //根据最低等级锁对象判断是否可对该对象进行加锁
            ALMutexInfo topLvMutexInfo = _getLowestPriorityMutex();
            
            //判断最低等级锁是否比加锁对象还高级，当比加锁对象高级时，表示可以直接加锁
            if(topLvMutexInfo.getMutexPriority() >= _cosObj.getPriority())
            {
                //当需要枷锁的对象锁等级比最低等级还要高时(数字小)
                //判断加锁对象是否在队列中已经加过锁了，如果已经加过锁此时是安全的
                mutexInfo = _getMutexByPriority(_cosObj.getPriority());
                if(null == mutexInfo || mutexInfo.getObj() != _cosObj)
                {
                    //尝试获取等级高的锁，返回错误
                    ALServerLog.Fatal("无法获取高等级锁");
                    //输出堆栈
                    new Exception().printStackTrace();
                    //直接返回，不进行加锁处理
                    return false;
                }
            }
        }
        
        if(null == mutexInfo)
        {
            mutexInfo = new ALMutexInfo(_cosObj);
            //将锁信息添加到队列中，由于可以加锁的对象一定是最低级的锁，所以直接使用添加最低优先级锁函数
            _addLowestPriorityMutex(mutexInfo);
        }
        
        //加锁
        mutexInfo.addLockTime();
        
        return true;
    }
    
    /***************
     * 尝试对对象进行解锁
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:27:42 PM
     */
    public boolean tryUnlock(ALMutex _cosObj)
    {
        if(null == _cosObj)
            return false;

        //获取同优先级的锁对象
        ALMutexInfo mutexInfo = _getMutexByPriority(_cosObj.getPriority());

        //本线程并未获取该优先级的锁，或同优先级锁并不是同一个时，表示解锁失败
        if(null == mutexInfo || mutexInfo.getObj() != _cosObj)
        {
            ALServerLog.Fatal("尝试释放未获取的锁");
            //输出堆栈
            new Exception().printStackTrace();
            //直接返回，不进行加锁处理
            return false;
        }
        
        //释放锁
        mutexInfo.reduceLockTime();
        
        if(mutexInfo.getLockTime() <= 0)
        {
            //次数低于0时从队列删除
            _removeMutexLevelInfo(mutexInfo);
        }
        
        return true;
    }
    
    /*****************
     * 判断当前线程是否释放所有锁
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:22:22 PM
     */
    public boolean judgeAllMutexRelease()
    {
        return _m_lThreadMutexList.isEmpty();
    }
    
    /********************
     * 释放所有本线程获取的对象锁
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 11:22:29 PM
     */
    public void releaseAllMutex()
    {
        //清空队列中每个锁对象的加锁信息
        for(int i = 0; i < _m_lThreadMutexList.size(); i++)
        {
            ALMutexInfo info = _m_lThreadMutexList.get(0);
            
            if(null == info)
                continue;
            
            //释放所有锁
            info.releaseAllLock();
        }

        //清空队列
        _m_lThreadMutexList.clear();
    }
    
    /**********
     * 加锁队列是否为空
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:24:59 PM
     */
    protected boolean _isMutexListEmpty()
    {
        return _m_lThreadMutexList.isEmpty();
    }
    
    /*******************
     * 查询本线程中指定优先级的锁对象
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:17:26 PM
     */
    protected ALMutexInfo _getMutexByPriority(int _priority)
    {
        for(int i = 0; i < _m_lThreadMutexList.size(); i ++)
        {
            ALMutexInfo info = _m_lThreadMutexList.get(i);
            
            if(info.getMutexPriority() == _priority)
                return info;
        }
        
        return null;
    }
    
    /***************
     * 获取已经加锁的最低等级锁
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:23:02 PM
     */
    protected ALMutexInfo _getLowestPriorityMutex()
    {
        return _m_lThreadMutexList.get(_m_lThreadMutexList.size() - 1);
    }
    
    /***************
     * 增加一个最低等级锁到加锁队列中
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:23:02 PM
     */
    protected void _addLowestPriorityMutex(ALMutexInfo _mutexInfo)
    {
        _m_lThreadMutexList.add(_mutexInfo);
    }
    
    /*****************
     * 从加锁队列中删除对象
     * 
     * @author alzq.z
     * @time   Feb 18, 2013 10:02:29 PM
     */
    protected void _removeMutexLevelInfo(ALMutexInfo _info)
    {
        _m_lThreadMutexList.remove(_info);
    }
}

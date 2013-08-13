package ALBasicServer.ALThread;

public class ALMutexInfo
{
    /** 对应锁的对象 */
    private ALMutex _m_iCosObject;
    /** 加锁次数 */
    private int _m_iLockTime;
    
    public ALMutexInfo(ALMutex _cosObj)
    {
        _m_iCosObject = _cosObj;
        _m_iLockTime = 0;
    }
    
    /*************
     * 获取锁等级
     * @return
     */
    public int getMutexPriority()
    {
        return _m_iCosObject.getPriority();
    }
    
    /*************
     * 获取锁次数
     * @return
     */
    public int getLockTime()
    {
        return _m_iLockTime;
    }
    
    /**************
     * 增加锁次数
     */
    public void addLockTime()
    {
        _m_iLockTime++;
    }
    
    /**************
     * 减少锁次数
     */
    public void reduceLockTime()
    {
        _m_iLockTime--;
    }
    
    /***************
     * 根据记录释放所有锁
     */
    public void releaseAllLock()
    {
        while(_m_iLockTime > 0)
        {
            if(null != _m_iCosObject)
                _m_iCosObject.unlock();
            
            reduceLockTime();
        }
    }
    
    /**************
     * 获取加锁对象
     * @return
     */
    public ALMutex getObj()
    {
        return _m_iCosObject;
    }
}

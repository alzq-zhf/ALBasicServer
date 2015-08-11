package ALBasicProtocolPack.BasicObj;

import java.nio.ByteBuffer;

import ALBasicCommon.ALBasicCommonFun;

/************************
 * 本类为消息处理注册对象，主协议号以byte定义，使用数组进行存储以增加查询速度
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 10:57:07 AM
 */
public class ALBasicProtocolDispather
{
    protected _AALBasicProtocolMainOrderDealer[] _m_lDealMap;
    
    public ALBasicProtocolDispather()
    {
        _m_lDealMap = new _AALBasicProtocolMainOrderDealer[256];

        //初始化所有的256个处理对象队列
        for(int i = 0; i < 256; i ++)
        {
            _m_lDealMap[i] = null;
        }
    }
    
    /****************
     * 注册主协议处理对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:29:20 AM
     */
    public void RegProtocol(_AALBasicProtocolMainOrderDealer _dispathRegister)
    {
        int i = ALBasicCommonFun.byte2int(_dispathRegister.getMainOrder());
        _m_lDealMap[i] = _dispathRegister;
    }
    
    /**********************
     * 返回对应协议的处理对象
     * @company Isg @author alzq.zhf
     * 2014年11月15日 下午12:02:57
     */
    public _AALBasicProtocolMainOrderDealer getDealer(byte _mainOrder)
    {
        int iIndex = ALBasicCommonFun.byte2int(_mainOrder);
        if(iIndex < 0 || iIndex >= _m_lDealMap.length)
            return null;
        
        return _m_lDealMap[iIndex];
    }
    
    
	/***********************
	 * 协议处理函数
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 11:31:49 AM
	 */
	public boolean DealProtocol(_IALProtocolReceiver _receiver, ByteBuffer _msg)
	{
	    //获取主协议编号
	    byte mainOrder = _msg.get();
	    
	    //获取协议处理对象
	    _AALBasicProtocolMainOrderDealer dealer = getDealer(mainOrder);
	    
	    if(null == dealer)
	        return false;
	    
	    boolean res = false;
	    try
	    {
	        res = dealer.dispathProtocol(_receiver, _msg);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
        }
	    
	    return res;
	}
}

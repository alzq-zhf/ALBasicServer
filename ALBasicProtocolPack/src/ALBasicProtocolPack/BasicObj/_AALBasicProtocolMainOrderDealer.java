package ALBasicProtocolPack.BasicObj;

import java.nio.ByteBuffer;

import ALServerLog.ALServerLog;


@SuppressWarnings("rawtypes")
public abstract class _AALBasicProtocolMainOrderDealer
{
	private byte mainOrder;
	private _AALBasicProtocolSubOrderDealer[] dealArray = null;
    
	/*****************
	 * 带入主协议号以及处理的协议最大的子协议号，进行协议处理队列的初始化
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:55 AM
	 */
    public _AALBasicProtocolMainOrderDealer(byte _mainOrder, int _protocolMaxTypeNum)
    {
        mainOrder = _mainOrder;
        dealArray = new _AALBasicProtocolSubOrderDealer[_protocolMaxTypeNum + 1];
    }
    
    /************
     * 获取主协议号
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:38:46 AM
     */
    public byte getMainOrder() {return mainOrder;}
    
    /****************
     * 设置处理对象，直接设置到数组中保证处理速度
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:52:50 AM
     */
    public void regDealer(_AALBasicProtocolSubOrderDealer _dealer)
    {
        if(null == dealArray)
            return ;
        
        if(_dealer.getMainOrder() != mainOrder)
        {
            //主协议号不匹配，提示警告
            ALServerLog.Fatal(mainOrder + " doesn't match the dealer's(" + _dealer.getClass().toString() + ") main order: " + _dealer.getMainOrder() + "!");
        }
        
        int subOrder = _dealer.getSubOrder();
        if(subOrder >= dealArray.length)
        {
        	ALServerLog.Fatal(mainOrder + " Protocol dispather don't have " + subOrder + " size list to save the dealer obj!");
            return ;
        }
        
        dealArray[subOrder] = _dealer;
    }
    
    /****************
     * 强制设置指定对象为处理对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:52:50 AM
     */
    public void setSubDealer(int _subOrderID, _AALBasicProtocolSubOrderDealer _dealer)
    {
        if(null == dealArray)
            return ;
        
        if(_subOrderID >= dealArray.length)
        {
            ALServerLog.Fatal(mainOrder + " Protocol dispather don't have " + _subOrderID + " size list to save the dealer obj!");
            return ;
        }
        
        dealArray[_subOrderID] = _dealer;
    }
    
	/**********************
	 * 根据协议编号分发协议并进行处理
	 * 
	 * @author alzq.z
	 * @time   Feb 19, 2013 10:52:46 AM
	 */
	public boolean dispathProtocol(_IALProtocolReceiver _receiver, ByteBuffer _msg)
	{
        if(null == _msg)
            return false;

        //获取子协议编号
        byte subType = _msg.get();

        //编号超出数组大小，直接返回失败
        if(subType >= dealArray.length)
        {
            return false;
        }
        
        //处理具体协议对象
        if(dealArray[subType] != null)
        {
            dealArray[subType].dealProtocol(_receiver, _msg);
            return true;
        }

        return false;
	}
}

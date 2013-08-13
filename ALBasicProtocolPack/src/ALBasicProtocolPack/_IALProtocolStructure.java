package ALBasicProtocolPack;

import java.nio.ByteBuffer;

/****************
 * 协议处理的消息类对象的接口类
 * 
 * @author alzq.z
 * @time   Feb 19, 2013 10:51:53 AM
 */
public interface _IALProtocolStructure
{
    /******************
     * 获取主协议编号
     * 
     * @author alzq.z
     * @time   Jan 15, 2013 9:53:29 PM
     */
    public byte getMainOrder();
    /*******************
     * 获取副协议编号
     * 
     * @author alzq.z
     * @time   Jan 15, 2013 9:53:41 PM
     */
    public byte getSubOrder();
    
    public int GetUnzipBufSize();

    public int GetZipBufSize();

    /**********
     * 创建完整的数据包，包含协议处理编号部分
     * 
     * @author alzq.z
     * @time   Feb 25, 2013 10:56:58 PM
     */
    public ByteBuffer makeFullPackage();
    public ByteBuffer makePackage();
    public void readPackage(ByteBuffer _buf);
}

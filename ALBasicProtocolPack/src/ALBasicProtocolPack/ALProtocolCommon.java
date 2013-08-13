package ALBasicProtocolPack;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**********************
 * Protocol制作对象的基本公用函数
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Jan 23, 2013 11:25:54 PM
 */
public class ALProtocolCommon
{
    private static Charset _g_CharSet = Charset.forName("UTF-8");
    
    /****************
     * 获取Int型数据在压缩数据后的数据大小
     * 需要多一字节存放长度
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static int GetIntZipSize(int _num)
    {
        if(_num > Byte.MIN_VALUE && _num < Byte.MAX_VALUE)
            return 2;
        else if(_num > Short.MIN_VALUE && _num < Short.MAX_VALUE)
            return 3;
        
        return 5;
    }
    
    /****************
     * 获取Long型数据在压缩数据后的数据大小
     * 需要多一字节存放长度
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static int GetLongZipSize(long _num)
    {
        if(_num > Byte.MIN_VALUE && _num < Byte.MAX_VALUE)
            return 2;
        else if(_num > Short.MIN_VALUE && _num < Short.MAX_VALUE)
            return 3;
        else if(_num > Integer.MIN_VALUE && _num < Integer.MAX_VALUE)
            return 5;
        
        return 9;
    }
    
    /****************
     * 将Int数据通过压缩方式放入内存块中
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static void ZipPutIntIntoBuf(ByteBuffer _buff, int _num)
    {
        if(_num > Byte.MIN_VALUE && _num < Byte.MAX_VALUE)
        {
            //put the length
            _buff.put((byte)1);
            //put the value
            _buff.put((byte)_num);
            return ;
        }
        else if(_num > Short.MIN_VALUE && _num < Short.MAX_VALUE)
        {
            //put the length
            _buff.put((byte)2);
            //put the value
            _buff.putShort((short)_num);
            return ;
        }

        //put the length
        _buff.put((byte)4);
        //put the value
        _buff.putInt(_num);
    }
    
    /****************
     * 获取Long型数据在压缩数据后的数据大小
     * 需要多一字节存放长度
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static void ZipPutLongIntoBuf(ByteBuffer _buff, long _num)
    {
        if(_num > Byte.MIN_VALUE && _num < Byte.MAX_VALUE)
        {
            //put the length
            _buff.put((byte)1);
            //put the value
            _buff.put((byte)_num);
            return ;
        }
        else if(_num > Short.MIN_VALUE && _num < Short.MAX_VALUE)
        {
            //put the length
            _buff.put((byte)2);
            //put the value
            _buff.putShort((short)_num);
            return ;
        }
        else if(_num > Integer.MIN_VALUE && _num < Integer.MAX_VALUE)
        {
            //put the length
            _buff.put((byte)4);
            //put the value
            _buff.putInt((int)_num);
            return ;
        }


        //put the length
        _buff.put((byte)8);
        //put the value
        _buff.putLong(_num);
    }
    
    /****************
     * 将Int数据通过压缩方式从内存方式
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static int ZipGetIntFromBuf(ByteBuffer _buff)
    {
        byte size = _buff.get();
        
        if(size == 1)
            return _buff.get();
        else if(size == 2)
            return _buff.getShort();
        
        return _buff.getInt();
    }
    
    /****************
     * 将Long数据通过压缩方式从内存方式
     * 
     * @author alzq.z
     * @time   Jan 23, 2013 11:25:38 PM
     */
    public static long ZipGetLongFromBuf(ByteBuffer _buff)
    {
        byte size = _buff.get();
        
        if(size == 1)
            return _buff.get();
        else if(size == 2)
            return _buff.getShort();
        else if(size == 4)
            return _buff.getInt();
        
        return _buff.getLong();
    }
    
    /***********
     * 获取发送字符串所需要的包长度
     * @param str
     * @return
     */
    public static int GetStringBufSize(String str)
    {
        if(null == str || str.isEmpty())
        {
            return 2;
        }

        return str.getBytes(_g_CharSet).length + 2;
    }
    
    /************
     * 将字符串放入字节包中
     * @param _str
     * @param _buf
     */
    public static void PutStringIntoBuf(ByteBuffer _buff, String _str)
    {
        if (_str != null && _str.length() > 0)
        {
            byte[] sb = _str.getBytes();
            _buff.putShort((short) sb.length);
            _buff.put(sb);
        }
        else
        {
            _buff.putShort((short) 0);
        }
    }
    
    /****************
     * 从字节数据中获取字符串
     * @param bb
     * @return
     */
    public static String GetStringFromBuf(ByteBuffer _buff)
    {
        short len = _buff.getShort();
        if(0 == len)
            return "";
        
        byte[] strbytes = new byte[len];
        _buff.get(strbytes, 0, len);
        String str;
        try
        {
            CharsetDecoder decoder = _g_CharSet.newDecoder();
            str = decoder.decode(ByteBuffer.wrap(strbytes)).toString();
        }
        catch (Exception e)
        {
            return "";
        }
        
        return str;
    }
}

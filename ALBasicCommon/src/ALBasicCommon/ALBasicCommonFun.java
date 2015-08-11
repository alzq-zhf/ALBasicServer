package ALBasicCommon;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Calendar;
import java.util.Random;

public class ALBasicCommonFun
{
    private static Random g_randomObj = new Random();
    private static String g_sHexStrIdxString = "0123456789ABCDEF";
    
    /*****************
     * 将字节转化为int对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:30:30 AM
     */
    public static int byte2int(byte value) { return (int)value & 0xFF; }
    
    /*****************
     * 将2个short转化为int对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:30:30 AM
     */
    public static int mergeShort(short _s1, short _s2)
    {
        return (((int)_s1) << 16) | _s2;
    }
    public static int mergeShortNum(int _s1, int _s2)
    {
        return (_s1 << 16) | _s2;
    }
    
    /*****************
     * 将2个int转化为long对象
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 11:30:30 AM
     */
    public static long mergeInt(int _i1, int _i2)
    {
        return (((long)_i1) << 32) | _i2;
    }

    public static boolean getBoolean(ByteBuffer buf)
    {
        if ( buf.get() == 1 )
        {
            return true;
        }
        
        return false;
    }
    
    public static void putBoolean(ByteBuffer buf, boolean bData)
    {
        if ( null == buf )
        {
            return;
        }
        
        if ( bData )
        {
            buf.put((byte)1);
        }
        else {
            buf.put((byte)0);
        }
    }
    
    /****************
     * 从字节数据中获取字符串
     * @param bb
     * @return
     */
    public static String getString(ByteBuffer bb)
    {
        short nStringLen = bb.getShort();
        byte[] byarrString = new byte[nStringLen];
        
        try
        {
            bb.get(byarrString, 0, nStringLen);
        }
        catch (Exception e)
        {
            return "";
        }
        
        String str;
        try
        {
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            str = decoder.decode(ByteBuffer.wrap(byarrString)).toString();
        }
        catch (CharacterCodingException e)
        {
            return "";
        }
        
        return str;
    }
    
    /***********
     * 获取发送字符串所需要的包长度
     * @param str
     * @return
     */
    public static int getStringSize(String str)
    {
        if(null == str || str.isEmpty())
        {
            return 2;
        }
        
        return str.getBytes().length + 2;
    }
    
    /************
     * 将字符串放入字节包中
     * @param _str
     * @param _buf
     */
    public static void putString(String _str, ByteBuffer _buf)
    {
        if (_str != null && _str.length() > 0)
        {
            byte[] sb = _str.getBytes();
            _buf.putShort((short) sb.length);
            _buf.put(sb);
        }
        else
        {
            _buf.putShort((short) 0);
        }
    }
    
    /**************
     * 将字符串转化为字节包
     * @param _str
     * @return
     */
    public static ByteBuffer getStringBuf(String _str)
    {
        if (_str != null && _str.length() > 0)
        {
            byte[] stringByte = _str.getBytes();

            ByteBuffer buf = ByteBuffer.allocate(stringByte.length + 2);
            buf.putShort((short) stringByte.length);
            buf.put(stringByte);
            buf.flip();
            
            return buf;
        }
        else
        {
            ByteBuffer buf = ByteBuffer.allocate(2);
            buf.putShort((short) 0);
            buf.flip();
            
            return buf;
        }
    }
    
    /**
     * 获取当前时间的小时部分（24小时制）
     * @return
     */
    public static int getNowTimeHour()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取当前时间距纪元所经历的秒数
     * @return
     */
    public static int getNowTime()
    {
        Calendar calendar = Calendar.getInstance();
        int iNowTime = (int) (calendar.getTimeInMillis() / 1000);
        
        return iNowTime;
    }
    
    /**
     * 获取当前时间距纪元所经历的毫秒数
     * @return
     */
    public static long getNowTimeMS()
    {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }
    
    /**
    * 获取当前时间信息连接起来的数字
    * @return
    */
   public static long getNowTimeNum()
   {
       Calendar calendar = Calendar.getInstance();
       
       long timeNum = calendar.get(Calendar.YEAR);
       timeNum = (timeNum * 100) + calendar.get(Calendar.MONTH) + 1;
       timeNum = (timeNum * 100) + calendar.get(Calendar.DAY_OF_MONTH);
       timeNum = (timeNum * 100) + calendar.get(Calendar.HOUR_OF_DAY);
       timeNum = (timeNum * 100) + calendar.get(Calendar.MINUTE);
       timeNum = (timeNum * 100) + calendar.get(Calendar.SECOND);
       
       return timeNum;
   }
    
    /**
     * 产生一个处于区间 0<= x < _iRangeLimit 的随机整数
     * @return
     */
    public static int getRandomIntByRange(int _iRangeLimit)
    {
        return Math.abs(g_randomObj.nextInt(_iRangeLimit));
    }
    
    /**
     * 产生一个处于区间 0<= x <= _iRangeLimit 的随机整数
     * @param iDelta
     * @return
     */
    public static int getRandomInt(int _iRangeLimit)
    {
        return Math.abs(g_randomObj.nextInt(_iRangeLimit + 1));
    }
    public static long getRandomLong()
    {
        return Math.abs(g_randomObj.nextLong());
    }
    
    /**
     * 产生一个处于区间 0<= x <= 1 的随机浮点数
     * @param iDelta
     * @return
     */
    public static float getRandomFloat()
    {
        return g_randomObj.nextFloat();
    }
    
    /**
     * 将字节数组转换成十六进制字符串
     * @param iDelta
     * @return
     */
    public static String getHexString(byte[] _buf)
    {
        if(null == _buf || _buf.length <= 0)
            return "0";
        
        //逐个字节处理
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < _buf.length; i++)
        {
            byte b = _buf[i];
            
            //取前4位
            builder.append(g_sHexStrIdxString.charAt((b >> 4) & 0x0F));
            //取后4位
            builder.append(g_sHexStrIdxString.charAt(b & 0x0F));
        }
        
        return builder.toString();
    }
    public static String getHexString(ByteBuffer _buf)
    {
        if(null == _buf)
            return "";
        
        //记录当前下标位置
        int prePos = _buf.position();
        int len = _buf.limit();
        //逐个字节处理
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < len; i++)
        {
            byte b = _buf.get();
            
            //取前4位
            builder.append(g_sHexStrIdxString.charAt((b >> 4) & 0x0F));
            //取后4位
            builder.append(g_sHexStrIdxString.charAt(b & 0x0F));
        }
        
        //重置buff下标
        _buf.position(prePos);
        
        return builder.toString();
    }
}

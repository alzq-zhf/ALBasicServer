package ALBasicCommon;

import java.util.Properties;

public class ALConfReader
{
    /**********************
     * 读取整形配置信息
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:54:27 AM
     */
    public static int readInt(Properties _properties, String _name, int _defaultValue)
    {
        int value = _defaultValue;
        
        String tmpStr = _properties.getProperty(_name);
        if(null != tmpStr && !tmpStr.isEmpty())
        {
            value = Integer.parseInt(tmpStr.trim());
        }
        System.out.println("[Conf Init] " + _name + " = " + tmpStr);
        
        return value;
    }
    
    /**********************
     * 读取长整形配置信息
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:54:27 AM
     */
    public static long readLong(Properties _properties, String _name, long _defaultValue)
    {
        long value = _defaultValue;
        
        String tmpStr = _properties.getProperty(_name);
        if(null != tmpStr && !tmpStr.isEmpty())
        {
            value = Long.parseLong(tmpStr.trim());
        }
        System.out.println("[Conf Init] " + _name + " = " + tmpStr);
        
        return value;
    }
    
    /**********************
     * 读取字符串配置信息
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:54:23 AM
     */
    public static String readStr(Properties _properties, String _name, String _defaultValue)
    {
        String value = _defaultValue;
        
        String tmpStr = _properties.getProperty(_name);
        if(null != tmpStr && !tmpStr.isEmpty())
        {
            value = tmpStr;
        }
        System.out.println("[Conf Init] " + _name + " = " + tmpStr);
        
        return value;
    }
    
    /**********************
     * 读取密码类配置信息，输出不同
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:54:23 AM
     */
    public static String readPassword(Properties _properties, String _name, String _defaultValue)
    {
        String value = _defaultValue;
        
        String tmpStr = _properties.getProperty(_name);
        if(null != tmpStr && !tmpStr.isEmpty())
        {
            value = tmpStr;
        }
        System.out.println("[Conf Init] " + _name + " = ******");
        
        return value;
    }
    
    /**********************
     * 读取布尔型配置信息
     * 
     * @author alzq.z
     * @time   Feb 19, 2013 10:54:19 AM
     */
    public static Boolean readBool(Properties _properties, String _name, Boolean _defaultValue)
    {
        Boolean value = _defaultValue;
        
        String tmpStr = _properties.getProperty(_name);
        if(null != tmpStr && !tmpStr.isEmpty())
        {
            value = tmpStr.trim().equalsIgnoreCase("true");
        }
        System.out.println("[Conf Init] " + _name + " = " + tmpStr);
        
        return value;
    }
}

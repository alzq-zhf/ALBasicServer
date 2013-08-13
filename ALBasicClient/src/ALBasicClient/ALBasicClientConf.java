package ALBasicClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ALBasicCommon.ALConfReader;

/******************
 * 基本的Java客户端配置对象
 * 
 * @author alzq.z
 * @email  zhuangfan@vip.163.com
 * @time   Feb 19, 2013 9:34:31 PM
 */
public class ALBasicClientConf
{
    private static ALBasicClientConf g_instance;

    public static ALBasicClientConf getInstance()
    {
        if(null == g_instance)
            g_instance = new ALBasicClientConf();
        
        return g_instance;
    }

    /** Socket消息发送的线程数量 */
    private int _m_iSendThreadNum;
    /** 端口接收BUF缓冲区长度 */
    private int _m_iRecBufferLen;
    
    public ALBasicClientConf()
    {
        _m_iSendThreadNum = 1;
        _m_iRecBufferLen = 131072;
    }

    public int getSendThreadNum() {return _m_iSendThreadNum;}
    public int getRecBufferLen() {return _m_iRecBufferLen;}
    
    /*******************
     * 初始化属性设置对象
     * @param _properties
     */
    public boolean init()
    {
    	Properties properties = new Properties();
        
        InputStream propertiesInputStream = null;
        
        try 
        {
			propertiesInputStream = new FileInputStream("./conf/ALBasicClientConf.properties");
		} 
        catch (IOException e) 
		{
			e.printStackTrace();
		}
        
        if(null == propertiesInputStream)
        {
            return false;
        }
        
        //输入有效则开始读取对应配置
        try
        {
            properties.load(propertiesInputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        try
        {
        	System.out.println("[Conf init] Start load ALBasicClient Properties ... ...");

        	//获取消息发送线程的数量
        	_m_iSendThreadNum
                = ALConfReader.readInt(properties, "ALBasicClient.SendThreadNum", _m_iRecBufferLen);
        	
        	//获取缓冲区长度
            _m_iRecBufferLen 
                = ALConfReader.readInt(properties, "ALBasicClient.RecSocketBufLen", _m_iRecBufferLen);

            System.out.println("[Conf init] Finish load ALBasicClient Properties ... ...");
        }
        catch (Exception e) 
        {
            System.out.println("[Conf Init Error] Load ALBasicClient Properties Error!!"); 
        	e.printStackTrace();
        }
        finally
        {
        	try 
        	{
				propertiesInputStream.close();
			} 
        	catch (IOException e) 
        	{
				e.printStackTrace();
			}
        }
        
        return true;
    }
}

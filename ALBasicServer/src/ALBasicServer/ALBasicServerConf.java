package ALBasicServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ALBasicCommon.ALConfReader;


public class ALBasicServerConf
{
    private static ALBasicServerConf g_instance;

    public static ALBasicServerConf getInstance()
    {
        if(null == g_instance)
            g_instance = new ALBasicServerConf();
        
        return g_instance;
    }
    
    /** 服务器标识 */
    private String _m_sServerTag;
    /** 是否检测锁对象的获取 */
    private boolean _m_bCheckMutex;
    /** 开启执行任务的线程数量 */
    private int _m_iSynTaskThreadNum;
    /** Socket消息发送的线程数量 */
    private int _m_iSendThreadNum;
    /** 定时任务检测时间的精度 */
    private int _m_iTimerCheckTime;
    /** 服务器开启端口 */
    private int _m_iServerPort;
    /** 服务器端口接收BUF长度 */
    private int _m_iServerRecBufferLen;
    
    public ALBasicServerConf()
    {
        _m_sServerTag = "AL Server";
        _m_bCheckMutex = true;
        _m_iSynTaskThreadNum = 4;
        _m_iSendThreadNum = 1;
        _m_iTimerCheckTime = 50;
        _m_iServerPort = 9527;
        _m_iServerRecBufferLen = 131072;
    }

    public String getServerTag() {return _m_sServerTag;}
    public boolean getCheckMutex() {return _m_bCheckMutex;}
    public int getSynTaskThreadNum() {return _m_iSynTaskThreadNum;}
    public int getSendThreadNum() {return _m_iSendThreadNum;}
    public int getTimerCheckTime() {return _m_iTimerCheckTime;}
    public int getServerPort() {return _m_iServerPort;}
    public int getServerRecBufferLen() {return _m_iServerRecBufferLen;}
    
    /*******************
     * 初始化属性设置对象，返回是否成功
     * @param _properties
     */
    public boolean init()
    {
    	Properties properties = new Properties();
        
        InputStream propertiesInputStream = null;
        
        try 
        {
			propertiesInputStream = new FileInputStream("./conf/ALBasicServerConf.properties");
		} 
        catch (Exception e) 
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
        	System.out.println("[Conf init] Start load ALBasicServer Properties ... ...");
            
            //服务器标记
            _m_sServerTag 
                = ALConfReader.readStr(properties, "ALBasicServer.ServerTag", _m_sServerTag);

            //服务器开启同步逻辑处理的线程数量
            _m_iSynTaskThreadNum 
                = ALConfReader.readInt(properties, "ALBasicServer.SynTaskDealThreadNum", _m_iSynTaskThreadNum);

            //服务器Socket返回消息的发送线程数量
            _m_iSendThreadNum 
                = ALConfReader.readInt(properties, "ALBasicServer.ServerSendBackDealThreadNum", _m_iSendThreadNum);

            //服务器内部任务处理的时间间隔最小片段长度
            _m_iTimerCheckTime 
                = ALConfReader.readInt(properties, "ALBasicServer.TimerCheckTime", _m_iTimerCheckTime);

            //是否在线程内进行死锁的检测
            _m_bCheckMutex 
                = ALConfReader.readBool(properties, "ALBasicServer.CheckMutex", _m_bCheckMutex);

            //服务器开启的端口
            _m_iServerPort 
                = ALConfReader.readInt(properties, "ALBasicServer.ServerPort", _m_iServerPort);

            //服务器端口中接收消息的BUF长度
            _m_iServerRecBufferLen 
                = ALConfReader.readInt(properties, "ALBasicServer.ServerSocketBufLen", _m_iServerRecBufferLen);

            System.out.println("[Conf init] Finish load ALBasicServer Properties ... ...");
        }
        catch (Exception e) 
        {
        	System.out.println("[Conf Init Error] Load ALBasicServer Properties Error!!"); 
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

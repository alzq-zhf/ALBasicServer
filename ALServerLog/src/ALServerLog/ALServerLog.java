package ALServerLog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import ALBasicCommon.ALConfReader;

public class ALServerLog
{
    private static LogLevel         g_logLevel;
    private static boolean          g_bInit = false;

    public static void initALServerLog()
    {
        if(g_bInit)
            return ;
        
        g_bInit = true;
        
        Properties properties = new Properties();
        
        InputStream propertiesInputStream = null;
        
        try 
        {
			propertiesInputStream = new FileInputStream("./conf/ALServerLog.properties");
		} 
        catch (IOException e) 
		{
			e.printStackTrace();
		}
        
        if(null == propertiesInputStream)
        {
            return;
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
        	System.out.println("[Conf init] Load ALServerLog Properties start......");
        	
            String tmpStr
                    = ALConfReader.readStr(properties, "ALServerLog.LogLevel", "ERROR");
            g_logLevel = LogLevel.valueOf(tmpStr.toUpperCase().trim());

            System.out.println("[Conf init] Finish load ALServerLog Properties ... ...");
        }
        catch (Exception e)
        {
            System.out.println("[Conf Init Error] Load ALServerLog Properties Error!!"); 
            g_logLevel = LogLevel.ERROR;
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
    }
    
    public static LogLevel getLogLevel()
    {
        return g_logLevel;
    }

    public static void Debug(String text)
    {
        _Log (LogLevel.DEBUG, text);
    }
    public static void Info(String text)
    {
        _Log (LogLevel.INFO, text);
    }    
    public static void Warning(String text)
    {
        _Log (LogLevel.WARNING, text);
    } 
    public static void Error(String text)
    {
        _Log (LogLevel.ERROR, text);
    }
    public static void Fatal(String text)
    {
        _Log (LogLevel.FATAL, "");
        _Log (LogLevel.FATAL, "=================== FATAL ERR =================");
        _Log (LogLevel.FATAL, text);
        _Log (LogLevel.FATAL, "===============================================");
        _Log (LogLevel.FATAL, "");
    }
    public static void Sys(String text)
    {
        _Log (LogLevel.SYS, text);
    }
    
    public static void Info(ByteBuffer _buffer)
    {
        if (g_logLevel.compareTo(LogLevel.INFO) <= 0)
        {
            StringBuffer str = new StringBuffer();
            str.append(" ---- \n");
            for(int i = _buffer.position(); i < _buffer.remaining(); i++)
            {
                byte b = _buffer.get(i);
                str.append(b + "\n");
            }
            
            ALServerLog.Info(str.toString());
        }
    }

    protected static void _Log(LogLevel lev, String logstr)
    {
        if (g_logLevel.compareTo(lev) <= 0)
        {
            System.out.println("[" + lev.toString() + "] " + logstr);
        }
    }

    public static enum LogLevel
    {
        DEBUG, // 调试信息提示
        INFO, // 比较重要的信息提示
        WARNING, // 可能存在的潜在问题的提示
        ERROR, // 系统发生异常的提示
        FATAL, // 系统发生了致命的错误的提示
        SYS,
    }
}

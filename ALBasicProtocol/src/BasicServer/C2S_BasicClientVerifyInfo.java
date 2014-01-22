package BasicServer;

import java.nio.ByteBuffer;
public class C2S_BasicClientVerifyInfo implements ALBasicProtocolPack._IALProtocolStructure {
private Integer clientType;
private String userName;
private String userPassword;
private String customMsg;


public C2S_BasicClientVerifyInfo() {
	clientType = 0;
	userName = "";
	userPassword = "";
	customMsg = "";
}

public final byte getMainOrder() { return (byte)0; }

public final byte getSubOrder() { return (byte)0; }

public int getClientType() { return clientType; }
public void setClientType(int _clientType) { clientType = _clientType; }
public String getUserName() { return userName; }
public void setUserName(String _userName) { userName = _userName; }
public String getUserPassword() { return userPassword; }
public void setUserPassword(String _userPassword) { userPassword = _userPassword; }
public String getCustomMsg() { return customMsg; }
public void setCustomMsg(String _customMsg) { customMsg = _customMsg; }


public final int GetUnzipBufSize() {
	int _size = 4;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userPassword);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(customMsg);

	return _size;
}

public final int GetZipBufSize() {
	int _size = 0;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetIntZipSize(clientType);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userPassword);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(customMsg);

	return _size;
}



public final void ReadUnzipBuf(ByteBuffer _buf) {
	clientType = _buf.getInt();
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	userPassword = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	customMsg = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
}

public final void ReadZipBuf(ByteBuffer _buf) {
	clientType = ALBasicProtocolPack.ALProtocolCommon.ZipGetIntFromBuf(_buf);
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	userPassword = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	customMsg = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
}

public final void PutUnzipBuf(ByteBuffer _buf) {
	_buf.putInt(clientType);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userPassword);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, customMsg);
}

public final void PutZipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.ZipPutIntIntoBuf(_buf, clientType);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userPassword);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, customMsg);
}

public final ByteBuffer makeFullPackage() {
	int _bufSize = GetUnzipBufSize() + 2;
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	_buf.put((byte)0);
	_buf.put((byte)0);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final ByteBuffer makePackage() {
	int _bufSize = GetUnzipBufSize();
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final void readPackage(ByteBuffer _buf) {
	ReadUnzipBuf(_buf);
}
}


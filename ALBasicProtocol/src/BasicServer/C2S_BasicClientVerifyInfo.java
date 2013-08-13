package BasicServer;

import java.nio.ByteBuffer;
public class C2S_BasicClientVerifyInfo implements ALBasicProtocolPack._IALProtocolStructure {
private String userName;
private String userPassword;


public C2S_BasicClientVerifyInfo() {
	userName = "";
	userPassword = "";
}

public final byte getMainOrder() { return (byte)0; }

public final byte getSubOrder() { return (byte)0; }

public String getUserName() { return userName; }
public void setUserName(String _userName) { userName = _userName; }
public String getUserPassword() { return userPassword; }
public void setUserPassword(String _userPassword) { userPassword = _userPassword; }


public final int GetUnzipBufSize() {
	int _size = 0;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userPassword);

	return _size;
}

public final int GetZipBufSize() {
	int _size = 0;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userPassword);

	return _size;
}



public final void ReadUnzipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	userPassword = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
}

public final void ReadZipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	userPassword = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
}

public final void PutUnzipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userPassword);
}

public final void PutZipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userPassword);
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


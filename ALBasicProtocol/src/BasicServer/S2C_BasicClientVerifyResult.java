package BasicServer;

import java.nio.ByteBuffer;
public class S2C_BasicClientVerifyResult implements ALBasicProtocolPack._IALProtocolStructure {
private String userName;
private Long socketID;


public S2C_BasicClientVerifyResult() {
	userName = "";
	socketID = (long)0;
}

public final byte getMainOrder() { return (byte)0; }

public final byte getSubOrder() { return (byte)0; }

public String getUserName() { return userName; }
public void setUserName(String _userName) { userName = _userName; }
public long getSocketID() { return socketID; }
public void setSocketID(long _socketID) { socketID = _socketID; }


public final int GetUnzipBufSize() {
	int _size = 8;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);

	return _size;
}

public final int GetZipBufSize() {
	int _size = 0;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetLongZipSize(socketID);

	return _size;
}



public final void ReadUnzipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	socketID = _buf.getLong();
}

public final void ReadZipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	socketID = ALBasicProtocolPack.ALProtocolCommon.ZipGetLongFromBuf(_buf);
}

public final void PutUnzipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	_buf.putLong(socketID);
}

public final void PutZipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.ZipPutLongIntoBuf(_buf, socketID);
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


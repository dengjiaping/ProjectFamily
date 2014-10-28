package com.chuxin.androidpush.sdk.push.net;

import com.chuxin.androidpush.sdk.push.utils.Utilities;

public class Packer {
	private Packer impl;
	protected Packer() {}

	public Packer(byte version) {
		if (version == 0) {
			impl = new Packer_v0(version);
		}
	}

	public byte[] login(String udid) {
		return impl.login(udid);
	}
	
	public byte[] ping(String udid) {
		return impl.ping(udid);
	}
	
	public byte[] register(String udid) {
		return impl.register(udid);
	}
	
	public byte[] unregister(String udid, String app, String uid) {
		return impl.unregister(udid, app, uid);
	}
	
	public byte[] read(String udid, String app, int msgid) {
		return impl.read(udid, app, msgid);
	}
	
	static class Packer_v0 extends Packer {
		private Request request = new Request(0);

		public Packer_v0(byte p1) {}
		
	    private byte[] networkBytes(byte[] data) {
	    	byte[] len = new byte[4];
	    	Utilities.htonl(data.length, len);
	    	
	    	byte[] result = new byte[1 + len.length + data.length];
	    	result[0] = (byte)0;    	
	    	System.arraycopy(len, 0, result, 1, len.length);
	    	System.arraycopy(data, 0, result, 1 + len.length, data.length);
	    	return result;
	    }

		public byte[] login(String udid) {
			return networkBytes(request.login(udid).toBytes());
	    }
	    
	    public byte[] ping(String udid) {
	    	return networkBytes(request.ping(udid).toBytes());
	    }
	    
	    public byte[] register(String udid) {
	    	return networkBytes(request.register(udid).toBytes());
	    }
	    
	    public byte[] unregister(String udid, String app, String uid) {
	    	return networkBytes(request.unregister(udid, app, uid).toBytes());
	    }
	    
	    public byte[] read(String udid, String app, int msgid) {
	    	return networkBytes(request.read(udid, app, msgid).toBytes());
	    }
	};
}

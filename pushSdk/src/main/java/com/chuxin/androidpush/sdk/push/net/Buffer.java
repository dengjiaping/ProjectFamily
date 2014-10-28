package com.chuxin.androidpush.sdk.push.net;

public class Buffer {
    public byte[] data;
    public int length;
    
    public Buffer(int length) {
        this.data = new byte[length];
        this.length = 0;
    }
    
    public void shift(int offset) {
        assert(length > offset);
 
        if (length == offset) {
        	length = 0;
        } else {
	        System.arraycopy(data, offset, data, 0, length - offset);
	        length -= offset;
        }
    }
}

package com.chuxin.androidpush.sdk.push.net;

import com.chuxin.androidpush.sdk.push.utils.Utilities;

public class Parser {
	
	public Message parse(Buffer buffer) {
        if (buffer.length < 5)
            return null;

        if (buffer.data[0] == 0) {
            return parse_v0(buffer);
        }

        return null;
    }
	    
    private Message parse_v0(Buffer buffer) {
        if (buffer.length < 5)
            return null;
        
        int dataLength = Utilities.ntohl(buffer.data, 1);
        
        if ((dataLength + 5) > buffer.length) {
            return null;
        }
	        
        Message message = Message.parse(buffer.data, 5, dataLength);
        // It's find, all unknown messages will be ignored.
        buffer.shift(dataLength + 5);
        
        return message;
    }
}

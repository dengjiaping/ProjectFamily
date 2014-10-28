package com.chuxin.androidpush.sdk.push.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
    
    public static int ntoll(byte[] bytes, int begin) {
        int number = (bytes[begin] & 0xff) | ((bytes[begin+1] << 8) & 0xff00) |
                ((bytes[begin+2] << 24) >>> 8) | (bytes[begin+3] << 24);
        return number;
    }

    public static int ntohl(byte[] bytes, int begin) {
        int number = (((0xFF & bytes[begin]) << 24) | (((0xFF & bytes[begin+1]) << 16) & 0xff0000) |
                (((0xFF & bytes[begin+2]) << 8) & 0xff00) | (0xFF & bytes[begin+3]));
        return number;
    }

    public static int ntohl(byte[] bytes) {
        return ntohl(bytes, 0);
    }
    
    public static void htonl(int number, byte[] bytes, int begin) {
    	bytes[begin] = (byte)(number >>> 24);
    	bytes[begin+1] = (byte)((number >> 16) & 0xff);
    	bytes[begin+2] = (byte)((number >> 8) & 0xff);
        bytes[begin+3] = (byte)(number & 0xff);
         
    }

    public static void htoll(int number, byte[] bytes, int begin) {
        bytes[begin] = (byte)(number & 0xff);
        bytes[begin+1] = (byte)((number >> 8) & 0xff);
        bytes[begin+2] = (byte)((number >> 16) & 0xff);
        bytes[begin+3] = (byte)(number >>> 24); 
    }

    public static void htonl(int number, byte[] bytes) {
        htonl(number, bytes, 0); 
    }
    
    public static String hexToString(byte[] hex) {
    	return hexToString(hex, 0, hex.length);
    }

    public static String shuffle(String s) {
    	StringBuilder sb = new StringBuilder();
    	int len = s.length();
    	
    	if (((len >> 1) << 1) == len) {
    		for (int i = 0; i < len; i+=2) {
    			sb.append(s.charAt(i));
    		}
    		for (int i = 1; i < len; i+=2) {
    			sb.append(s.charAt(i));
    		}
    	} else {
    		--len;
    		sb.append(s.charAt(s.charAt(len)));
    		
    		for (int i = 0; i < len; i+=2) {
    			sb.append(s.charAt(i));
    		}
    		for (int i = 1; i < len; i+=2) {
    			sb.append(s.charAt(i));
    		}    		
    	}
    	
    	return sb.toString();
    }

    public static String hexToString(byte[] hex, int offset, int length) {
	    StringBuilder sb = new StringBuilder();
	    
	    if ((offset + length) > hex.length)
	    	length = hex.length - offset;

	    for (int i = offset; i < length; i++) {
	        sb.append(String.format("%02x", hex[i]));
	    }

	    return sb.toString();
    }
    
    public static String sign(String message, long timestamp, int flag) {
		String data = message + '|' + timestamp + '|' + flag;
		
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
	    	byte[] hex = md5.digest(data.getBytes());
	    	return Utilities.hexToString(hex);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		return null;
    }
}

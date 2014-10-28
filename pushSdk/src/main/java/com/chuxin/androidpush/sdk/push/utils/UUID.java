package com.chuxin.androidpush.sdk.push.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class UUID {
	
	private static String sUDID = null;
	private static final String TAG = "UUID";
	private static HashMap<Character, HashMap<Character, Character>> sEncoding = new HashMap<Character, HashMap<Character, Character>>();
	
	private static String encode(String s) {
		if (sEncoding.size() == 0) {
			
//'0' : 'C2BF1AE7D3594086',
//'1' : '716F5E2094B3ADC8',
//'2' : 'E38154F0CA267DB9',
//'3' : '9A642F1CEB75038D',
//'4' : 'F7A804DC9E1265B3',
//'5' : '3E025C9F71BD648A',
//'6' : '0A3D46FC7B59E218',
//'7' : '165A9E3C0DF2B487',
//'8' : 'F0ED473A56C1829B',
//'9' : '5B1906EC27F8A43D',
//'A' : '68F7A319B25E4C0D',
//'B' : '603FEA918425C7DB',
//'C' : 'C5DEF847B031296A',
//'D' : '295BDAEC618F7034',
//'E' : '071B45F3C82AED69',
//'F' : 'BF14863E209A57DC',

			HashMap<Character, Character> eA = new HashMap<Character, Character>();
			eA.put('0', '6');
			eA.put('1', '8');
			eA.put('2', 'F');
			eA.put('3', '7');
			eA.put('4', 'A');
			eA.put('5', '3');
			eA.put('6', '1');
			eA.put('7', '9');
			eA.put('8', 'B');
			eA.put('9', '2');
			eA.put('A', '5');
			eA.put('B', 'E');
			eA.put('C', '4');
			eA.put('D', 'C');
			eA.put('E', '0');
			eA.put('F', 'D');
			sEncoding.put('A', eA);
			HashMap<Character, Character> eC = new HashMap<Character, Character>();
			eC.put('0', 'C');
			eC.put('1', '5');
			eC.put('2', 'D');
			eC.put('3', 'E');
			eC.put('4', 'F');
			eC.put('5', '8');
			eC.put('6', '4');
			eC.put('7', '7');
			eC.put('8', 'B');
			eC.put('9', '0');
			eC.put('A', '3');
			eC.put('B', '1');
			eC.put('C', '2');
			eC.put('D', '9');
			eC.put('E', '6');
			eC.put('F', 'A');
			sEncoding.put('C', eC);
			HashMap<Character, Character> eB = new HashMap<Character, Character>();
			eB.put('0', '6');
			eB.put('1', '0');
			eB.put('2', '3');
			eB.put('3', 'F');
			eB.put('4', 'E');
			eB.put('5', 'A');
			eB.put('6', '9');
			eB.put('7', '1');
			eB.put('8', '8');
			eB.put('9', '4');
			eB.put('A', '2');
			eB.put('B', '5');
			eB.put('C', 'C');
			eB.put('D', '7');
			eB.put('E', 'D');
			eB.put('F', 'B');
			sEncoding.put('B', eB);
			HashMap<Character, Character> eE = new HashMap<Character, Character>();
			eE.put('0', '0');
			eE.put('1', '7');
			eE.put('2', '1');
			eE.put('3', 'B');
			eE.put('4', '4');
			eE.put('5', '5');
			eE.put('6', 'F');
			eE.put('7', '3');
			eE.put('8', 'C');
			eE.put('9', '8');
			eE.put('A', '2');
			eE.put('B', 'A');
			eE.put('C', 'E');
			eE.put('D', 'D');
			eE.put('E', '6');
			eE.put('F', '9');
			sEncoding.put('E', eE);
			HashMap<Character, Character> eD = new HashMap<Character, Character>();
			eD.put('0', '2');
			eD.put('1', '9');
			eD.put('2', '5');
			eD.put('3', 'B');
			eD.put('4', 'D');
			eD.put('5', 'A');
			eD.put('6', 'E');
			eD.put('7', 'C');
			eD.put('8', '6');
			eD.put('9', '1');
			eD.put('A', '8');
			eD.put('B', 'F');
			eD.put('C', '7');
			eD.put('D', '0');
			eD.put('E', '3');
			eD.put('F', '4');
			sEncoding.put('D', eD);
			HashMap<Character, Character> eF = new HashMap<Character, Character>();
			eF.put('0', 'B');
			eF.put('1', 'F');
			eF.put('2', '1');
			eF.put('3', '4');
			eF.put('4', '8');
			eF.put('5', '6');
			eF.put('6', '3');
			eF.put('7', 'E');
			eF.put('8', '2');
			eF.put('9', '0');
			eF.put('A', '9');
			eF.put('B', 'A');
			eF.put('C', '5');
			eF.put('D', '7');
			eF.put('E', 'D');
			eF.put('F', 'C');
			sEncoding.put('F', eF);
			HashMap<Character, Character> e1 = new HashMap<Character, Character>();
			e1.put('0', '7');
			e1.put('1', '1');
			e1.put('2', '6');
			e1.put('3', 'F');
			e1.put('4', '5');
			e1.put('5', 'E');
			e1.put('6', '2');
			e1.put('7', '0');
			e1.put('8', '9');
			e1.put('9', '4');
			e1.put('A', 'B');
			e1.put('B', '3');
			e1.put('C', 'A');
			e1.put('D', 'D');
			e1.put('E', 'C');
			e1.put('F', '8');
			sEncoding.put('1', e1);
			HashMap<Character, Character> e0 = new HashMap<Character, Character>();
			e0.put('0', 'C');
			e0.put('1', '2');
			e0.put('2', 'B');
			e0.put('3', 'F');
			e0.put('4', '1');
			e0.put('5', 'A');
			e0.put('6', 'E');
			e0.put('7', '7');
			e0.put('8', 'D');
			e0.put('9', '3');
			e0.put('A', '5');
			e0.put('B', '9');
			e0.put('C', '4');
			e0.put('D', '0');
			e0.put('E', '8');
			e0.put('F', '6');
			sEncoding.put('0', e0);
			HashMap<Character, Character> e3 = new HashMap<Character, Character>();
			e3.put('0', '9');
			e3.put('1', 'A');
			e3.put('2', '6');
			e3.put('3', '4');
			e3.put('4', '2');
			e3.put('5', 'F');
			e3.put('6', '1');
			e3.put('7', 'C');
			e3.put('8', 'E');
			e3.put('9', 'B');
			e3.put('A', '7');
			e3.put('B', '5');
			e3.put('C', '0');
			e3.put('D', '3');
			e3.put('E', '8');
			e3.put('F', 'D');
			sEncoding.put('3', e3);
			HashMap<Character, Character> e2 = new HashMap<Character, Character>();
			e2.put('0', 'E');
			e2.put('1', '3');
			e2.put('2', '8');
			e2.put('3', '1');
			e2.put('4', '5');
			e2.put('5', '4');
			e2.put('6', 'F');
			e2.put('7', '0');
			e2.put('8', 'C');
			e2.put('9', 'A');
			e2.put('A', '2');
			e2.put('B', '6');
			e2.put('C', '7');
			e2.put('D', 'D');
			e2.put('E', 'B');
			e2.put('F', '9');
			sEncoding.put('2', e2);
			HashMap<Character, Character> e5 = new HashMap<Character, Character>();
			e5.put('0', '3');
			e5.put('1', 'E');
			e5.put('2', '0');
			e5.put('3', '2');
			e5.put('4', '5');
			e5.put('5', 'C');
			e5.put('6', '9');
			e5.put('7', 'F');
			e5.put('8', '7');
			e5.put('9', '1');
			e5.put('A', 'B');
			e5.put('B', 'D');
			e5.put('C', '6');
			e5.put('D', '4');
			e5.put('E', '8');
			e5.put('F', 'A');
			sEncoding.put('5', e5);
			HashMap<Character, Character> e4 = new HashMap<Character, Character>();
			e4.put('0', 'F');
			e4.put('1', '7');
			e4.put('2', 'A');
			e4.put('3', '8');
			e4.put('4', '0');
			e4.put('5', '4');
			e4.put('6', 'D');
			e4.put('7', 'C');
			e4.put('8', '9');
			e4.put('9', 'E');
			e4.put('A', '1');
			e4.put('B', '2');
			e4.put('C', '6');
			e4.put('D', '5');
			e4.put('E', 'B');
			e4.put('F', '3');
			sEncoding.put('4', e4);
			HashMap<Character, Character> e7 = new HashMap<Character, Character>();
			e7.put('0', '1');
			e7.put('1', '6');
			e7.put('2', '5');
			e7.put('3', 'A');
			e7.put('4', '9');
			e7.put('5', 'E');
			e7.put('6', '3');
			e7.put('7', 'C');
			e7.put('8', '0');
			e7.put('9', 'D');
			e7.put('A', 'F');
			e7.put('B', '2');
			e7.put('C', 'B');
			e7.put('D', '4');
			e7.put('E', '8');
			e7.put('F', '7');
			sEncoding.put('7', e7);
			HashMap<Character, Character> e6 = new HashMap<Character, Character>();
			e6.put('0', '0');
			e6.put('1', 'A');
			e6.put('2', '3');
			e6.put('3', 'D');
			e6.put('4', '4');
			e6.put('5', '6');
			e6.put('6', 'F');
			e6.put('7', 'C');
			e6.put('8', '7');
			e6.put('9', 'B');
			e6.put('A', '5');
			e6.put('B', '9');
			e6.put('C', 'E');
			e6.put('D', '2');
			e6.put('E', '1');
			e6.put('F', '8');
			sEncoding.put('6', e6);
			HashMap<Character, Character> e9 = new HashMap<Character, Character>();
			e9.put('0', '5');
			e9.put('1', 'B');
			e9.put('2', '1');
			e9.put('3', '9');
			e9.put('4', '0');
			e9.put('5', '6');
			e9.put('6', 'E');
			e9.put('7', 'C');
			e9.put('8', '2');
			e9.put('9', '7');
			e9.put('A', 'F');
			e9.put('B', '8');
			e9.put('C', 'A');
			e9.put('D', '4');
			e9.put('E', '3');
			e9.put('F', 'D');
			sEncoding.put('9', e9);
			HashMap<Character, Character> e8 = new HashMap<Character, Character>();
			e8.put('0', 'F');
			e8.put('1', '0');
			e8.put('2', 'E');
			e8.put('3', 'D');
			e8.put('4', '4');
			e8.put('5', '7');
			e8.put('6', '3');
			e8.put('7', 'A');
			e8.put('8', '5');
			e8.put('9', '6');
			e8.put('A', 'C');
			e8.put('B', '1');
			e8.put('C', '8');
			e8.put('D', '2');
			e8.put('E', '9');
			e8.put('F', 'B');
			sEncoding.put('8', e8);
		}
		
		HashMap<Character, Character> codeset = sEncoding.get(Character.toUpperCase(s.charAt(s.length() - 1)));
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(s.charAt(s.length() - 1)));
		for (byte i : s.getBytes()) {
			sb.append(codeset.get(Character.toUpperCase((char)i)));
		}
		
		return sb.toString();
	}
	
	@SuppressLint("NewApi")
	public static String devUUID(Context context) {
		if (sUDID != null)
			return sUDID;

        try {
			final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

			String deviceId = "[DID]" + tm.getDeviceId();
			String androidId = "[AID]" + android.provider.Settings.Secure.getString(
							        		context.getContentResolver(),
							        		android.provider.Settings.Secure.ANDROID_ID);
			String serialNo = "[SN]";
			
			if(Build.VERSION.SDK_INT >= 9)
				serialNo = "[SN]" + android.os.Build.SERIAL;
			String packageName = "[PN]";
			
			sUDID = (new java.util.UUID( ((long)packageName.hashCode() << 32) | deviceId.hashCode(),
							    			((long)androidId.hashCode() << 32 | serialNo.hashCode()))).toString().toUpperCase().replace("-", "");
			return sUDID;
		} catch (Exception e) {
			e.printStackTrace();
			return "sUDID"+System.currentTimeMillis();
		}
	}

	public static String appUUID(Context context, String appID, String appSecret) {
		String raw1;
		String hash;
		String uuid;
		
		if (sUDID == null)
			devUUID(context.getApplicationContext());
		
		raw1 = appID + "|" + appSecret + "|" + sUDID;
		try {
			hash = Utilities.hexToString(MessageDigest.getInstance("MD5").digest(raw1.getBytes()));
//			TeeLog.d(TAG, "raw=" + raw1 + ", hash=" + hash);
			uuid = encode(hash.substring(0, 4) + sUDID + hash.substring(4, 9));
			if (null == uuid) {
				return "uuid"+System.currentTimeMillis();
			}
			return uuid;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
//			return null;//modify to avoid null point by niechao
			return "uuid"+System.currentTimeMillis();
		}
	}
}

package com.carrotlord.string;

//Language = Java 6
//credit to carrotlord for pretty much the entire contents of this file
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class StrTools {

	public static String CString(byte[] buf, String encoding) {
		int l = 0;
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == 0) {
				l = i;
				break;
			}
		}
		byte[] cbuf = Arrays.copyOf(buf, l);
		try {
			return new String(cbuf, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "encoding_err";
	}

	public static String CString(byte[] buf) {
		return CString(buf, "UTF-8");
	}
}

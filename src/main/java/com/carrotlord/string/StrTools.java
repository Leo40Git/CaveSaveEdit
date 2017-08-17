package com.carrotlord.string;

// Language = Java 6
// credit to carrotlord for pretty much the entire contents of this file
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class StrTools {

	public static final String DEFAULT_ENCODING = "Cp943C";
	public static final String ENCODING_ERROR = "encoding_err";

	public static String CString(byte[] buf, String encoding) {
		int l = 0;
		while (l < buf.length && buf[l] > 0)
			l++;
		byte[] cbuf = Arrays.copyOf(buf, l);
		try {
			return new String(cbuf, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ENCODING_ERROR;
	}

	public static String CString(byte[] buf) {
		return CString(buf, DEFAULT_ENCODING);
	}
}

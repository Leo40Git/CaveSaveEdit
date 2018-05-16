package com.leo.cse.backend;

// Language = Java 6
// credit to carrotlord for pretty much the entire contents of this file
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * String utility methods.
 *
 * @author Carrotlord
 *
 */
public class StrTools {

	/**
	 * Default string encoding.
	 */
	public static final String DEFAULT_ENCODING = "Cp943C";
	/**
	 * Encoding error message.<br />
	 * If this is returned from
	 * {@link #CString(byte[], String)}, something went wrong.
	 */
	public static final String ENCODING_ERROR = "encoding_err";

	public static String CString(byte[] buf, String encoding) {
		int l = 0;
		while (l < buf.length && buf[l] > 0)
			l++;
		byte[] cbuf = Arrays.copyOf(buf, l);
		try {
			return new String(cbuf, encoding);
		} catch (UnsupportedEncodingException e) {
			BackendLogger.error("Unsupported encoding: " + encoding, e);
		}
		return ENCODING_ERROR;
	}

	public static String CString(byte[] buf) {
		return CString(buf, DEFAULT_ENCODING);
	}

	public static int ascii2Num_CS(String str) {
		int result = 0;
		int radix = 1;
		for (int i = 0; i < str.length(); i++) {
			if (i > 7)
				break;
			if (i > 0)
				radix *= 10;
			result += (str.charAt(str.length() - i - 1) - '0') * radix;
		}
		return result;
	}
}

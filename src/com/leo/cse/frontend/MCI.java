package com.leo.cse.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MCI {

	private MCI() {
	}

	private static Properties mci = new Properties();

	public static void readDefault() throws IOException {
		try (InputStream is = MCI.class.getResourceAsStream("default.mci")) {
			mci.load(is);
		}
	}

	public static void read(File file) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			mci.load(fis);
		}
	}

	public static boolean contains(String key) {
		return mci.containsKey(key);
	}

	public static String get(String key) {
		return mci.getProperty(key, key);
	}

	public static String get(String type, String value) {
		final String key = type + "." + value;
		return mci.getProperty(key, mci.getProperty(type + ".None", key));
	}

	public static String get(String type, int id) {
		return get(type, Integer.toString(id));
	}

	public static String getNullable(String type, String value) {
		final String key = type + "." + value;
		return mci.getProperty(key);
	}

	public static String getNullable(String type, int id) {
		return getNullable(type, Integer.toString(id));
	}

	public static int getNumber(String type) {
		int ret = 0;
		for (Object obj : mci.keySet()) {
			if (!(obj instanceof String))
				continue;
			String key = (String) obj;
			if (key.startsWith(type + "."))
				ret++;
		}
		return ret;
	}

	public static Map<Integer, String> getAll(String type) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		for (Map.Entry<Object, Object> entry : mci.entrySet())
			if (((String) entry.getKey()).startsWith(type + ".")) {
				String key = (String) entry.getKey();
				String id = key.substring(key.lastIndexOf('.') + 1);
				try {
					ret.put(Integer.parseInt(id), (String) entry.getValue());
				} catch (Exception e) {
					continue;
				}
			}
		return ret;
	}

	public static int getId(String type, String value) {
		final Map<Integer, String> map = getAll(type);
		if (!map.containsValue(value))
			return -1;
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			if (entry.getValue().equals(value))
				return entry.getKey();
		}
		return -1;
	}

	public static boolean getSpecial(String value) {
		return Boolean.parseBoolean(mci.getProperty("Special." + value, "false"));
	}

	public static String getSpecials() {
		String ret = "None";
		if (getSpecial("VarHack")) {
			ret = "TSC+ <VAR Hack";
			if (getSpecial("PhysVarHack"))
				ret += " + <PHY Addon";
		} else if (getSpecial("MimHack"))
			ret = "<MIM Hack";
		if (getSpecial("DoubleRes"))
			if (ret.equals("None"))
				ret = "2x Res";
			else
				ret += ", 2x Res";
		return ret;
	}

}

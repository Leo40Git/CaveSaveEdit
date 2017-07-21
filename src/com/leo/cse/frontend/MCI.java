package com.leo.cse.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MCI {

	public static class MCIException extends Exception {
		private static final long serialVersionUID = 2809309984939251682L;

		public MCIException(String message) {
			super(message);
		}
	}

	private MCI() {
	}

	private static Properties mci = new Properties();

	public static void readDefault() throws IOException, MCIException {
		try (InputStream is = MCI.class.getResourceAsStream("default.mci")) {
			mci.load(is);
		}
		validate();
	}

	public static void read(File file) throws IOException, MCIException {
		try (FileInputStream fis = new FileInputStream(file)) {
			mci.load(fis);
		}
		validate();
	}

	private static void validate() throws MCIException {
		int fps = getInteger("Game.FPS", 50);
		if (fps == 0)
			throw new MCIException("Game.FPS cannot be equal to 0!");
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

	public static String getNullable(String key) {
		return mci.getProperty(key);
	}

	public static String getNullable(String type, String value) {
		return mci.getProperty(type + "." + value);
	}

	public static String getNullable(String type, int id) {
		return getNullable(type, Integer.toString(id));
	}

	public static int getInteger(String key, int def) {
		String val = getNullable(key);
		if (val == null)
			return def;
		Integer ret;
		try {
			ret = Integer.parseUnsignedInt(val);
		} catch (NumberFormatException ignore) {
			return def;
		}
		return ret;
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
		Integer i = FrontUtils.getKey(getAll(type), value);
		if (i == null)
			return -1;
		return i;
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
		else if (getSpecial("BuyHack"))
			ret = "<BUY Hack";
		int res = getInteger("Game.GraphicsResolution", 1);
		if (res != 1) {
			if (ret.equals("None"))
				ret = res + "x Resolution";
			else
				ret += ", " + res + "x Res";
		}
		return ret;
	}

}

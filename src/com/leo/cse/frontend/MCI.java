package com.leo.cse.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

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

	private static void readList(Properties p, String group, Object obj) {
		if (obj instanceof NativeArray) {
			NativeArray na = (NativeArray) obj;
			for (int d = 0; d < na.size(); d++) {
				if (na.get(d) == Scriptable.NOT_FOUND || na.get(d) == null)
					continue;
				p.put(group + "." + d, na.get(d));
			}
		} else if (obj instanceof String[]) {
			String[] list = (String[]) obj;
			for (int d = 0; d < list.length; d++) {
				if (list[d] == null)
					continue;
				p.put(group + "." + d, list[d]);
			}
		} else
			throw new RuntimeException("Unsupported list type: " + obj.getClass().getName());
	}

	private static Object invokeFunction(Context cx, Scriptable scope, String name, Object... args) {
		Object fObj = scope.get(name, scope);
		if (!(fObj instanceof Function)) {
			System.out.println(name + " is not a function or is undefined.");
			return null;
		} else {
			Function f = (Function) fObj;
			return f.call(cx, scope, scope, args);
		}
	}

	private static Context cx;
	private static Scriptable scope;

	private static Object invokeFunction(String name, Object... args) {
		return invokeFunction(cx, scope, name, args);
	}

	private static void read0(InputStream is, File src) throws IOException {
		cx = Context.enter();
		scope = cx.initStandardObjects();
		try (InputStreamReader isr = new InputStreamReader(is);) {
			cx.evaluateReader(scope, isr, src.getName(), 0, null);
		}
		Properties tmp = new Properties();
		try {
			// Metadata
			tmp.put("Meta.Name", invokeFunction("getName"));
			tmp.put("Meta.Author", invokeFunction("getAuthor"));
			// Game information
			tmp.put("Game.ExeName", invokeFunction("getExeName"));
			tmp.put("Game.ArmsImageYStart", invokeFunction("getArmsImageYStart"));
			tmp.put("Game.ArmsImageSize", invokeFunction("getArmsImageSize"));
			tmp.put("Game.FPS", invokeFunction("getFPS"));
			tmp.put("Game.GraphicsResolution", invokeFunction("getGraphicsResolution"));
			// Special support
			Object oss = invokeFunction("getSpecials");
			if (oss instanceof NativeArray) {
				NativeArray specials = (NativeArray) oss;
				if (specials.contains("MimHack"))
					tmp.put("Special.MimHack", true);
				if (specials.contains("VarHack"))
					tmp.put("Special.VarHack", true);
				if (specials.contains("PhysVarHack"))
					tmp.put("Special.PhysVarHack", true);
				if (specials.contains("BuyHack"))
					tmp.put("Special.BuyHack", true);
			}
			// Map names
			readList(tmp, "Map", invokeFunction("getMapNames"));
			// Song names
			readList(tmp, "Song", invokeFunction("getSongNames"));
			// Equip names
			readList(tmp, "Equip", invokeFunction("getEquipNames"));
			// Weapon names
			readList(tmp, "Weapon", invokeFunction("getWeaponNames"));
			// Item names
			readList(tmp, "Item", invokeFunction("getItemNames"));
			// Warp menu names
			readList(tmp, "Warp", invokeFunction("getWarpNames"));
			// Warp location names
			readList(tmp, "WarpLoc", invokeFunction("getWarpLocNames"));
			// Flag descriptions
			tmp.put("Flag.SaveID", invokeFunction("getSaveFlagID"));
			readList(tmp, "Flag", invokeFunction("getFlagDescriptions"));
		} catch (Exception e) {
			System.err.println("MCI: Exception while assigning script results to properties object!");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"An exception has occured!\nPlease send the error log (\"cse.log\") to the developer,\nalong with a description of what you did leading up to the exception.",
					"Something went wrong", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} finally {
			Context.exit();
			mci.clear();
			mci.putAll(tmp);
		}
	}

	public static void readDefault() throws IOException, MCIException {
		read0(MCI.class.getResourceAsStream("default.mci"), new File("default.mci"));
		validate();
	}

	public static void read(File file) throws IOException, MCIException {
		read0(new FileInputStream(file), file);
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

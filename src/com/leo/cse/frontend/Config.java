package com.leo.cse.frontend;

import java.awt.Color;
import java.util.prefs.Preferences;

public class Config {

	private Config() {
	}
	
	public static final long BUILD = 1;

	public static final String KEY_CONFIG_BUILD = "config_build";
	public static final String KEY_LAST_PROFIE = "last_profile";
	public static final String KEY_LAST_MCI_FILE = "last_defines";
	public static final String KEY_HIDE_UNDEFINED_FLAGS = "hide_undefined_flags";
	public static final String KEY_HIDE_SYSTEM_FLAGS = "hide_system_flags";
	public static final String KEY_SORT_MAPS_ALPHABETICALLY = "sort_maps_alphabetically";
	public static final String KEY_LINE_COLOR = "line_color";
	public static final String KEY_LOAD_NPCS = "load_npcs";
	public static final String KEY_ENCODING = "encoding";
	
	@Deprecated
	private static final String KEY_LINE_COLOR_OLD = "custom_color";

	private static final Preferences CONFIG = Preferences.userNodeForPackage(Main.class);

	public static void init() {
		long confBuild = CONFIG.getLong(KEY_CONFIG_BUILD, 0);
		if (confBuild == 0) {
			// pre version system change
			String color = get(KEY_LINE_COLOR_OLD, "-1");
			CONFIG.remove(KEY_LINE_COLOR_OLD);
			set(KEY_LINE_COLOR, color);
		}
		CONFIG.putLong(KEY_CONFIG_BUILD, BUILD);
	}

	public static String get(String key, String def) {
		return CONFIG.get(key, def);
	}

	public static void set(String key, String value) {
		CONFIG.put(key, value);
	}

	public static boolean getBoolean(String key, boolean def) {
		return CONFIG.getBoolean(key, def);
	}

	public static void setBoolean(String key, boolean value) {
		CONFIG.putBoolean(key, value);
	}

	public static Color getColor(String key, Color def) {
		String nm = get(key, null);
		if (nm == null)
			return def;
		return Color.decode(nm);
	}

	public static void setColor(String key, Color value) {
		set(key, String.valueOf(value.getRGB()));
	}

}

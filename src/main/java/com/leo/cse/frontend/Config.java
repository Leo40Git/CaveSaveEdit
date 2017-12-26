package com.leo.cse.frontend;

import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {

	private Config() {
	}

	public static final long BUILD = 1;

	public static final String KEY_CONFIG_BUILD = "config_build";
	public static final String KEY_LAST_PROFIE = "last_profile";
	public static final String KEY_LAST_MCI_FILE = "last_defines";
	public static final String KEY_LAST_MOD = "last_mod";
	public static final String KEY_LAST_NIKU = "last_niku";
	public static final String KEY_HIDE_UNDEFINED_FLAGS = "hide_undefined_flags";
	public static final String KEY_HIDE_SYSTEM_FLAGS = "hide_system_flags";
	public static final String KEY_SORT_MAPS_ALPHABETICALLY = "sort_maps_alphabetically";
	public static final String KEY_SHOW_MAP_GRID = "show_map_grid";
	public static final String KEY_LINE_COLOR = "line_color";
	public static final String KEY_LOAD_NPCS = "load_npcs";
	public static final String KEY_ENCODING = "encoding";
	public static final String KEY_SKIP_UPDATE_CHECK = "skip_update_check";
	public static final String KEY_AUTOLOAD_EXE = "autoload_exe";

	private static Preferences config;

	public static void init() {
		if (config == null)
			config = Preferences.userNodeForPackage(Main.class);
		config.putLong(KEY_CONFIG_BUILD, BUILD);
	}
	
	public static void wipe() {
		try {
			config.removeNode();
			config = null;
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key, String def) {
		return config.get(key, def);
	}

	public static void set(String key, String value) {
		config.put(key, value);
	}

	public static boolean getBoolean(String key, boolean def) {
		return config.getBoolean(key, def);
	}

	public static void setBoolean(String key, boolean value) {
		config.putBoolean(key, value);
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

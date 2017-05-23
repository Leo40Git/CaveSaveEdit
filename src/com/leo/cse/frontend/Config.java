package com.leo.cse.frontend;

import java.util.prefs.Preferences;

public class Config {

	private Config() {
	}

	public static final String KEY_LAST_PROFIE = "last_profile";
	public static final String KEY_LAST_DEFINES = "last_defines";
	public static final String KEY_HIDE_UNDEFINED_FLAGS = "hide_undefined_flags";
	public static final String KEY_HIDE_SYSTEM_FLAGS = "hide_system_flags";
	public static final String KEY_SORT_MAPS_ALPHABETICALLY = "sort_maps_alphabetically";

	private static final Preferences CONFIG = Preferences.userNodeForPackage(Main.class);

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

}

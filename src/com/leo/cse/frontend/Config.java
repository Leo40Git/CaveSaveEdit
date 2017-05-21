package com.leo.cse.frontend;

import java.util.prefs.Preferences;

public class Config {
	
	private Config() {
	}
	
	public static final String KEY_LAST_PROFIE = "last_profile";
	public static final String KEY_LAST_DEFINES = "last_defines";
	
	private static final Preferences CONFIG = Preferences.userNodeForPackage(Main.class);
	
	public static String get(String key, String def) {
		return CONFIG.get(key, def);
	}
	
	public static void set(String key, String value) {
		CONFIG.put(key, value);
	}

}

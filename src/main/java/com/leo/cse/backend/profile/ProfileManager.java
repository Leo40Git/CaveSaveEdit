package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;

import com.leo.cse.backend.exe.ExeData;

public class ProfileManager {

	public enum ProfileType {
		NORMAL, CSPLUS;
	}

	private static ProfileType implType;

	public static ProfileType getType() {
		return implType;
	}

	private static Profile impl;
	private static File file;

	public static File getFile() {
		return file;
	}

	public static void load(File file) throws IOException {
		if (ExeData.isPlusMode()) {
			// load CS+ profile
		} else {
			// load normal profile
		}
	}

	public static void load(String path) throws IOException {
		load(new File(path));
	}

	public static String getHeader() {
		return impl.getHeader();
	}

	public static void setHeader(String header) {
		impl.setHeader(header);
	}

	public static String getFlagHeader() {
		return impl.getFlagHeader();
	}

	public static void setFlagHeader(String flagH) {
		impl.setFlagHeader(flagH);
	}

	public static boolean hasField(String field) {
		return impl.hasField(field);
	}

	public static Class<?> getFieldType(String field) {
		return impl.getFieldType(field);
	}

	public static boolean fieldAcceptsValue(String field, Object value) {
		if (!impl.hasField(field))
			return false;
		Class<?> type = impl.getFieldType(field);
		if (type == null)
			return false;
		return type.isInstance(value);
	}

	public static Object getField(String field) {
		return impl.getField(field);
	}

	public static void setField(String field, Object value) {
		impl.setField(field, value);
	}

}

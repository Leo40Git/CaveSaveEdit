package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile.ProfileFieldException;

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

	public static void read(File file) throws IOException {
		if (ExeData.isPlusMode()) {
			// load CS+ profile
		} else {
			impl = new NormalProfile();
		}
		impl.read(file);
	}

	public static void read(String path) throws IOException {
		read(new File(path));
	}

	public static void write(File file) throws IOException {
		if (impl == null)
			return;
		impl.write(file);
	}
	
	public static void write(String path) throws IOException {
		write(new File(path));
	}
	
	public static void write() throws IOException {
		if (impl == null)
			return;
		write(impl.getLoadedFile());
	}
	
	public static File getLoadedFile() {
		if (impl == null)
			return null;
		return impl.getLoadedFile();
	}
	
	public static boolean isLoaded() {
		return getLoadedFile() != null;
	}
	
	public static boolean isModified() {
		if (impl == null)
			return false;
		return impl.isModified();
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

	public static boolean hasField(String field) throws ProfileFieldException {
		return impl.hasField(field);
	}

	public static Class<?> getFieldType(String field) throws ProfileFieldException {
		return impl.getFieldType(field);
	}

	public static boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException {
		return impl.fieldAcceptsValue(field, value);
	}

	public static Object getField(String field, int index) throws ProfileFieldException {
		return impl.getField(field, index);
	}

	public static Object getField(String field) throws ProfileFieldException {
		return impl.getField(field);
	}

	public static void setField(String field, int index, Object value) throws ProfileFieldException {
		impl.setField(field, index, value);
	}

	public static void setField(String field, Object value) throws ProfileFieldException {
		impl.setField(field, value);
	}

}

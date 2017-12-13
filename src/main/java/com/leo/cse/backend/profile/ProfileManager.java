package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.backend.profile.IProfile.ProfileFieldException;
import com.leo.cse.backend.profile.IProfile.ProfileMethodException;

public class ProfileManager {

	/**
	 * Used to notify {@link ProfileListener}s of the current profile being
	 * saved.
	 */
	public static final String EVENT_SAVE = "event.save";
	/**
	 * Used to notify {@link ProfileListener}s of a new profile being loaded.
	 */
	public static final String EVENT_LOAD = "event.load";

	public static final String EVENT_UNLOAD = "event.unload";

	private static Class<? extends IProfile> implClass = NormalProfile.class;

	@SuppressWarnings("unchecked")
	public static void setClass(String className) {
		Class<?> tmpClass;
		try {
			tmpClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("Profile class not found: " + className + "\nUsing default NormalProfile class instead");
			e.printStackTrace();
			tmpClass = NormalProfile.class;
		}
		if (!IProfile.class.isAssignableFrom(tmpClass)) {
			System.err.println("Profile class does not implement Profile interface: " + className
					+ "\nUsing default NormalProfile class instead");
			tmpClass = NormalProfile.class;
		}
		implClass = (Class<? extends IProfile>) tmpClass;
	}

	private static IProfile impl;

	public static Class<? extends IProfile> getType() {
		if (impl == null)
			return null;
		return impl.getClass();
	}

	private static File file;

	public static File getFile() {
		return file;
	}

	private static List<ProfileListener> listeners;
	private static boolean modified;

	public static void addListener(ProfileListener l) {
		if (listeners == null)
			listeners = new LinkedList<>();
		listeners.add(l);
	}

	public static void removeListener(ProfileListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	private static void notifyListeners(String field, int id, Object oldValue, Object newValue) {
		if (listeners == null)
			return;
		for (ProfileListener l : listeners)
			l.onChange(field, id, oldValue, newValue);
	}

	private static UndoManager undoMan;

	/**
	 * Represents an edit to a field in the profile.
	 * 
	 * @author Leo
	 *
	 */
	public static class ProfileEdit implements UndoableEdit {

		/**
		 * The name of the field that was modified.
		 */
		private String field;
		/**
		 * The index of the field that was modified.
		 */
		private int index;
		/**
		 * The field's old value.
		 */
		private Object oldVal;
		/**
		 * The field's new value.
		 */
		private Object newVal;
		/**
		 * If <code>true</code>, this edit has been undone and thus can be redone.
		 */
		private boolean hasBeenUndone;

		/**
		 * Constructs a new <code>ProfileEdit</code>.
		 * 
		 * @param field
		 *            the name of the field that was modified
		 * @param index
		 *            the index of the field that was modified
		 * @param oldVal
		 *            the field's old value
		 * @param newVal
		 *            the field's new value
		 */
		public ProfileEdit(String field, int index, Object oldVal, Object newVal) {
			this.field = field;
			this.index = index;
			this.oldVal = oldVal;
			this.newVal = newVal;
		}

		@Override
		public void undo() throws CannotUndoException {
			System.out.println("Attempting to undo: " + getUndoPresentationName());
			try {
				setField(field, index, oldVal, false);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			hasBeenUndone = true;
		}

		@Override
		public boolean canUndo() {
			return !hasBeenUndone;
		}

		@Override
		public void redo() throws CannotRedoException {
			System.out.println("Attempting to redo: " + getRedoPresentationName());
			try {
				setField(field, index, newVal, false);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
			hasBeenUndone = false;
		}

		@Override
		public boolean canRedo() {
			return hasBeenUndone;
		}

		@Override
		public void die() {
			oldVal = null;
			newVal = null;
		}

		@Override
		public boolean addEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean isSignificant() {
			return true;
		}

		@Override
		public String getPresentationName() {
			String fn = field;
			if (index > -1)
				fn += "[" + fn + "]";
			return fn;
		}

		@Override
		public String getUndoPresentationName() {
			String pn = getPresentationName();
			return "undo " + pn + " from " + newVal + " to " + oldVal;
		}

		@Override
		public String getRedoPresentationName() {
			String pn = getPresentationName();
			return "redo " + pn + " from " + oldVal + " to " + newVal;
		}

	}

	private static boolean undoManExists() {
		return impl != null && impl.getLoadedFile() != null && undoMan != null;
	}

	public static boolean canUndo() {
		if (!undoManExists())
			return false;
		return undoMan.canUndo();
	}

	/**
	 * Undoes an edit.
	 */
	public static void undo() {
		if (!canUndo())
			return;
		undoMan.undo();
	}

	public static boolean canRedo() {
		if (!undoManExists())
			return false;
		return undoMan.canRedo();
	}

	/**
	 * Redoes an edit.
	 */
	public static void redo() {
		if (!canRedo())
			return;
		undoMan.redo();
	}
	
	private static void makeImpl() {
		if (implClass == null)
			implClass = NormalProfile.class;
		Object implObj;
		try {
			implObj = implClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			System.err.println("Profile class could not be initialized: " + implClass.getName()
					+ "\nUsing default NormalProfile class instead");
			e.printStackTrace();
			implObj = new NormalProfile();
		}
		impl = (IProfile) implObj;
	}
	
	private static void postLoad() {
		undoMan = new UndoManager();
		modified = false;
		// notify listeners
		notifyListeners(EVENT_LOAD, -1, null, null);
	}
	
	public static void create() {
		unload();
		makeImpl();
		impl.create();
		postLoad();
	}

	public static void load(File file) throws IOException {
		unload();
		makeImpl();
		impl.load(file);
		postLoad();
	}

	public static void load(String path) throws IOException {
		load(new File(path));
	}

	public static void reload() throws IOException {
		if (impl == null)
			return;
		File loadedFile = impl.getLoadedFile();
		if (loadedFile == null)
			return;
		load(loadedFile);
	}

	public static void save(File file) throws IOException {
		if (impl == null)
			return;
		impl.save(file);
		modified = false;
		// notify listeners
		notifyListeners(EVENT_SAVE, -1, null, null);
	}

	public static void save(String path) throws IOException {
		save(new File(path));
	}

	public static void save() throws IOException {
		if (impl == null)
			return;
		save(impl.getLoadedFile());
	}

	public static void unload() {
		impl = null;
		file = null;
		modified = false;
		undoMan = null;
		notifyListeners(EVENT_UNLOAD, -1, null, null);
	}

	public static File getLoadedFile() {
		if (impl == null)
			return null;
		return impl.getLoadedFile();
	}

	public static boolean isLoaded() {
		if (impl == null)
			return false;
		return true;
	}

	public static boolean isModified() {
		if (impl == null)
			return false;
		if (getLoadedFile() == null)
			return true;
		return modified;
	}

	public static String getHeader() {
		if (impl == null)
			return null;
		return impl.getHeader();
	}

	public static void setHeader(String header) {
		if (impl == null)
			return;
		impl.setHeader(header);
	}

	public static String getFlagHeader() {
		if (impl == null)
			return null;
		return impl.getFlagHeader();
	}

	public static void setFlagHeader(String flagH) {
		if (impl == null)
			return;
		impl.setFlagHeader(flagH);
	}

	public static boolean hasField(String field) throws ProfileFieldException {
		if (impl == null)
			return false;
		return impl.hasField(field);
	}

	public static Class<?> getFieldType(String field) throws ProfileFieldException {
		if (impl == null)
			return null;
		return impl.getFieldType(field);
	}

	public static boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException {
		if (impl == null)
			return false;
		return impl.fieldAcceptsValue(field, value);
	}

	public static Object getField(String field, int index) throws ProfileFieldException {
		if (impl == null)
			return null;
		return impl.getField(field, index);
	}

	public static Object getField(String field) throws ProfileFieldException {
		return getField(field, 0);
	}

	public static void setField(String field, int index, Object value, boolean addUndo) throws ProfileFieldException {
		if (impl == null)
			return;
		Class<?> type = getFieldType(field);
		Class<?> compType = type.getComponentType();
		String valueStr = type.cast(value).toString();
		if (compType != null)
			valueStr = compType.getName() + "[" + "]";
		System.out.println("setting field " + field + "[" + index + "] to " + valueStr);
		Object oldValue = impl.getField(field, index);
		if (type.cast(oldValue) != type.cast(value)) {
			modified = true;
			notifyListeners(field, index, oldValue, value);
			if (addUndo)
				undoMan.addEdit(new ProfileEdit(field, index, oldValue, value));
		}
		impl.setField(field, index, value);
	}

	public static void setField(String field, int index, Object value) throws ProfileFieldException {
		setField(field, index, value, true);
	}

	public static void setField(String field, Object value) throws ProfileFieldException {
		setField(field, 0, value);
	}

	public static boolean hasMethod(String method) throws ProfileMethodException {
		if (impl == null)
			return false;
		return impl.hasMethod(method);
	}

	public static int getMethodArgNum(String method) throws ProfileMethodException {
		if (impl == null)
			return -1;
		return impl.getMethodArgNum(method);
	}

	public static Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException {
		if (impl == null)
			return null;
		return impl.getMethodArgTypes(method);
	}

	public static Class<?> getMethodRetType(String method) throws ProfileMethodException {
		if (impl == null)
			return null;
		return impl.getMethodRetType(method);
	}

	public static Object callMethod(String method, Object... args) throws ProfileMethodException {
		if (impl == null)
			return null;
		Object ret = impl.callMethod(method, args);
		String[] modFields = impl.getMethodModifiedFields(method);
		if (modFields != null) {
			modified = true;
			for (String field : modFields)
				notifyListeners(field, -1, null, null);
		}
		return ret;
	}

}

package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.backend.profile.Profile.ProfileFieldException;
import com.leo.cse.frontend.MCI;

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

	private static Profile impl;
	
	public static Class<? extends Profile> getType() {
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

	public static void load(File file, int section) throws IOException {
		unload();
		String profileClass = MCI.getNullable("Game.ProfileClass");
		if (profileClass == null)
			impl = new NormalProfile();
		else {
			Class<?> implClass;
			try {
				implClass = Class.forName(profileClass);
			} catch (ClassNotFoundException e) {
				System.err.println(
						"Profile class not found: " + profileClass + "\nUsing default NormalProfile class instead");
				e.printStackTrace();
				implClass = NormalProfile.class;
			}
			if (!Profile.class.isAssignableFrom(implClass)) {
				System.err.println("Profile class does not implement Profile interface: " + profileClass
						+ "\nUsing default NormalProfile class instead");
				implClass = NormalProfile.class;
			}
			Object implObj;
			try {
				implObj = implClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				System.err.println("Profile class could not be initialized: " + profileClass
						+ "\nUsing default NormalProfile class instead");
				e.printStackTrace();
				implObj = new NormalProfile();
			}
			impl = (Profile) implObj;
		}
		impl.load(file, section);
		undoMan = new UndoManager();
		modified = false;
		// notify listeners
		notifyListeners(EVENT_LOAD, 0, null, null);
	}

	public static void load(String path, int section) throws IOException {
		load(new File(path), section);
	}

	public static void save(File file, int section) throws IOException {
		if (impl == null)
			return;
		impl.save(file, section);
		modified = false;
		// notify listeners
		notifyListeners(EVENT_SAVE, 0, null, null);
	}

	public static void save(String path, int section) throws IOException {
		save(new File(path), section);
	}

	public static void save() throws IOException {
		if (impl == null)
			return;
		save(impl.getLoadedFile(), impl.getLoadedSection());
	}

	public static void unload() {
		impl = null;
		file = null;
		modified = false;
		undoMan = null;
		notifyListeners(EVENT_UNLOAD, 0, null, null);
	}

	public static File getLoadedFile() {
		if (impl == null)
			return null;
		return impl.getLoadedFile();
	}

	public static int getLoadedSection() {
		if (impl == null)
			return 0;
		return impl.getLoadedSection();
	}

	public static boolean isLoaded() {
		return getLoadedFile() != null;
	}

	public static boolean isModified() {
		if (impl == null)
			return false;
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

}

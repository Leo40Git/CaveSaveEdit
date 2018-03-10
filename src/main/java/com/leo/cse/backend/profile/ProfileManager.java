package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.frontend.Main;

/**
 * Stores information for a profile.
 * 
 * @author Leo
 *
 */
public class ProfileManager {

	/**
	 * Exception from accessing profile fields.
	 * 
	 * @author Leo
	 *
	 */
	public static class ProfileFieldException extends Exception {

		private static final long serialVersionUID = 1L;

		public ProfileFieldException(String message) {
			super(message);
		}

	}

	/**
	 * Exception from accessing profile methods.
	 * 
	 * @author Leo
	 *
	 */
	public static class ProfileMethodException extends Exception {

		private static final long serialVersionUID = 1L;

		public ProfileMethodException(String message) {
			super(message);
		}

	}

	/**
	 * Used to record field changes from executing methods.
	 * 
	 * @author Leo
	 *
	 */
	interface FieldChangeRecorder {
		/**
		 * Record a change.
		 * 
		 * @param field
		 *            field name
		 * @param index
		 *            index of field
		 * @param oldVal
		 *            old value
		 * @param newVal
		 *            new value
		 */
		public void addChange(String field, int index, Object oldVal, Object newVal);
	}

	/**
	 * Used to notify {@link ProfileListener}s of the current profile being
	 * modified. Basically a generic "catch-all" event for when something
	 * unspecified happens.
	 */
	public static final String EVENT_DATA_MODIFIED = "event.data_mod";
	/**
	 * Used to notify {@link ProfileListener}s of the current profile being
	 * saved.
	 */
	public static final String EVENT_SAVE = "event.save";
	/**
	 * Used to notify {@link ProfileListener}s of a new profile being loaded.
	 */
	public static final String EVENT_LOAD = "event.load";

	/**
	 * Used to notify {@link ProfileListener}s of the current profile being
	 * unloaded.
	 */
	public static final String EVENT_UNLOAD = "event.unload";

	/**
	 * The current implementation class for profiles.
	 */
	private static Class<? extends Profile> implClass = NormalProfile.class;

	/**
	 * Sets the implementation class for profiles.
	 * 
	 * @param className
	 *            name of new implementation class
	 */
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
		if (!Profile.class.isAssignableFrom(tmpClass)) {
			System.err.println("Profile class does not implement Profile interface: " + className
					+ "\nUsing default NormalProfile class instead");
			tmpClass = NormalProfile.class;
		}
		implClass = (Class<? extends Profile>) tmpClass;
		makeImpl();
	}

	/**
	 * The current profile implementation - an instance of {@link #implClass}.
	 */
	private static Profile impl;

	/**
	 * Gets the current profile implementation type.<br>
	 * Note that if this is <b>not</b>
	 * equal to {@link #implClass}, something has gone wrong.
	 * 
	 * @return type of current profile implementation
	 */
	public static Class<? extends Profile> getType() {
		if (impl == null)
			return null;
		return impl.getClass();
	}

	/**
	 * A list of {@link ProfileListener}s that will be notified if a field gets
	 * modified.
	 */
	private static List<ProfileListener> listeners;
	/**
	 * Modified flag. If <code>true</code>, profile data has been modified since the
	 * last save.
	 */
	private static boolean modified;

	/**
	 * Adds a listener.
	 * 
	 * @param l
	 *            listener
	 */
	public static void addListener(ProfileListener l) {
		if (listeners == null)
			listeners = new LinkedList<>();
		listeners.add(l);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param l
	 *            listener
	 */
	public static void removeListener(ProfileListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	/**
	 * Notifies all listeners of a field being modified.
	 * 
	 * @param field
	 *            field that was modified
	 * @param id
	 *            index of field that was modified (if applicable)
	 * @param oldValue
	 *            old value of field
	 * @param newValue
	 *            new value of field
	 */
	private static void notifyListeners(String field, int id, Object oldValue, Object newValue) {
		if (listeners == null)
			return;
		for (ProfileListener l : listeners)
			l.onChange(field, id, oldValue, newValue);
	}

	/**
	 * Manages undoing and redoing profile edits.
	 */
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
			setField(field, index, oldVal, false);
			hasBeenUndone = true;
		}

		@Override
		public boolean canUndo() {
			return !hasBeenUndone;
		}

		@Override
		public void redo() throws CannotRedoException {
			System.out.println("Attempting to redo: " + getRedoPresentationName());
			setField(field, index, newVal, false);
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

	/**
	 * Checks if the undo manager exists.
	 * 
	 * @return <code>true</code> if undo manager exists, <code>false</code>
	 *         otherwise.
	 */
	private static boolean undoManExists() {
		return impl != null && impl.getLoadedFile() != null && undoMan != null;
	}

	/**
	 * Checks if an edit can be undone.
	 * 
	 * @return <code>true</code> if an edit can be undone, <code>false</code>
	 *         otherwise
	 */
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

	/**
	 * Checks if an edit can be redone.
	 * 
	 * @return <code>true</code> if an edit can be redone, <code>false</code>
	 *         otherwise
	 */
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

	/**
	 * Creates an instance ({@link #impl}) of the current profile implementation
	 * ({@link #implClass}).
	 */
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
		impl = (Profile) implObj;
	}

	/**
	 * Called after loading a profile.
	 */
	private static void postLoad() {
		undoMan = new UndoManager();
		modified = false;
		// notify listeners
		notifyListeners(EVENT_LOAD, -1, null, null);
	}

	/**
	 * Creates a new blank profile.
	 */
	public static void create() {
		unload();
		makeImpl();
		impl.create();
		postLoad();
	}

	/**
	 * Loads a profile.
	 * 
	 * @param file
	 *            profile to load
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void load(File file) throws IOException {
		if (impl == null)
			makeImpl();
		else
			unload();
		try {
			impl.load(file);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Profile loading failed.");
			JOptionPane.showMessageDialog(Main.window, "An error occured while loading the profile file:\n" + e,
					"Could not load profile file!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		postLoad();
	}

	/**
	 * Loads a profile.
	 * 
	 * @param path
	 *            path to profile to load
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void load(String path) throws IOException {
		load(new File(path));
	}

	/**
	 * Reloads an already-loaded profile.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void reload() throws IOException {
		File loadedFile = getLoadedFile();
		if (loadedFile == null)
			return;
		load(loadedFile);
	}

	/**
	 * Saves a profile.
	 * 
	 * @param file
	 *            file to save to
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void save(File file) throws IOException {
		if (impl == null)
			return;
		impl.save(file);
		modified = false;
		// notify listeners
		notifyListeners(EVENT_SAVE, -1, null, null);
	}

	/**
	 * Saves a profile.
	 * 
	 * @param file
	 *            path to file to save to
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void save(String path) throws IOException {
		save(new File(path));
	}

	/**
	 * Saves a profile to the file it was loaded from.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void save() throws IOException {
		if (impl == null)
			return;
		save(impl.getLoadedFile());
	}

	/**
	 * Unloads the currently loaded profile.
	 */
	public static void unload() {
		if (!isLoaded())
			return;
		impl.unload();
		modified = false;
		undoMan = null;
		notifyListeners(EVENT_UNLOAD, -1, null, null);
	}

	/**
	 * Gets the currently loaded file.
	 * 
	 * @return currently loaded file, or <code>null</code> if none is loaded
	 */
	public static File getLoadedFile() {
		if (impl == null)
			return null;
		return impl.getLoadedFile();
	}

	/**
	 * Checks if a file is currently loaded.
	 * 
	 * @return <code>true</code> if a file is loaded, <code>false</code> otherwise.
	 */
	public static boolean isLoaded() {
		return getLoadedFile() != null;
	}

	/**
	 * Checks if the file has been modified since the last save.
	 * 
	 * @return <code>true</code> if file has been modified, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isModified() {
		if (impl == null)
			return false;
		if (getLoadedFile() == null)
			return true;
		return modified;
	}

	/**
	 * Gets the profile header.
	 * 
	 * @return profile header
	 */
	public static String getHeader() {
		if (impl == null)
			return null;
		return impl.getHeader();
	}

	/**
	 * Sets the profile header for validating profiles.
	 * 
	 * @param header
	 *            new profile header
	 */
	public static void setHeader(String header) {
		if (impl == null)
			return;
		impl.setHeader(header);
	}

	/**
	 * Gets the flag section header.
	 * 
	 * @return flag header
	 */
	public static String getFlagHeader() {
		if (impl == null)
			return null;
		return impl.getFlagHeader();
	}

	/**
	 * Sets the flag section header for validating profiles.
	 * 
	 * @param flagH
	 *            new flag header
	 */
	public static void setFlagHeader(String flagH) {
		if (impl == null)
			return;
		impl.setFlagHeader(flagH);
	}

	public static List<String> getAllFields() {
		if (impl == null)
			return null;
		return impl.getAllFields();
	}

	/**
	 * Checks if a field exists.
	 * 
	 * @param field
	 *            field to check
	 * @return <code>true</code> if it exists, <code>false</code> otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static boolean hasField(String field) {
		if (impl == null)
			return false;
		boolean ret = false;
		try {
			ret = impl.hasField(field);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Gets a field's value type.
	 * 
	 * @param field
	 *            field to check
	 * @return the field's type
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static Class<?> getFieldType(String field) {
		if (impl == null)
			return null;
		Class<?> ret = null;
		try {
			ret = impl.getFieldType(field);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Checks if a field has indexes.
	 * 
	 * @param field
	 *            field to check
	 * @return <code>true</code> if it has indexes, <code>false</code> otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static boolean fieldHasIndexes(String field) {
		if (impl == null)
			return false;
		boolean ret = false;
		try {
			ret = impl.fieldHasIndexes(field);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Gets the minimum index of a field.
	 * 
	 * @param field
	 *            field to check
	 * @return minimum field index
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static int getFieldMinimumIndex(String field) {
		if (impl == null)
			return -1;
		int ret = -1;
		try {
			ret = impl.getFieldMinimumIndex(field);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Gets the maximum index of a field.
	 * 
	 * @param field
	 *            field to check
	 * @return maximum field index
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static int getFieldMaximumIndex(String field) {
		if (impl == null)
			return -1;
		int ret = -1;
		try {
			ret = impl.getFieldMaximumIndex(field);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Checks if a field accepts a value.
	 * 
	 * @param field
	 *            field to check against
	 * @param index
	 *            index to check against
	 * @param value
	 *            value to check
	 * @return <code>true</code> if the value is acceptable, <code>false</code>
	 *         otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static boolean fieldAcceptsValue(String field, int index, Object value) {
		if (impl == null)
			return false;
		boolean ret = false;
		try {
			ret = impl.fieldAcceptsValue(field, index);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Checks if a field accepts a value.
	 * 
	 * @param field
	 *            field to check against
	 * @param value
	 *            value to check
	 * @return <code>true</code> if the value is acceptable, <code>false</code>
	 *         otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static boolean fieldAcceptsValue(String field, Object value) {
		return fieldAcceptsValue(field, -1, value);
	}

	/**
	 * Gets a field's value.
	 * 
	 * @param field
	 *            field to get
	 * @param index
	 *            index to get. will be ignored if
	 *            the field {@linkplain #fieldHasIndexes(String) doesn't have
	 *            indexes}
	 * @return value of the field
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static Object getField(String field, int index) {
		if (impl == null)
			return null;
		Object ret;
		try {
			ret = impl.getField(field, index);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 * Gets a field's value.
	 * 
	 * @param field
	 *            field to get
	 * @return value of the field
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static Object getField(String field) {
		return getField(field, -1);
	}

	/**
	 * Sets a field's value.
	 * 
	 * @param field
	 *            field to set
	 * @param index
	 *            index to set. will be ignored if
	 *            the field {@linkplain #fieldHasIndexes(String) doesn't have
	 *            indexes}
	 * @param value
	 *            value to set
	 * @param addUndo
	 *            <code>true</code> to add change to the {@linkplain #undoMan undo
	 *            manager}, <code>false</code> otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static void setField(String field, int index, Object value, boolean addUndo) {
		if (impl == null)
			return;
		Class<?> type = getFieldType(field);
		Class<?> compType = type.getComponentType();
		String fieldStr = field;
		if (fieldHasIndexes(fieldStr))
			fieldStr += "[" + index + "]";
		String valueStr = type.cast(value).toString();
		if (compType != null) {
			valueStr = compType.getName() + "[";
			Object[] valArray = (Object[]) value;
			for (int i = 0; i < valArray.length; i++) {
				valueStr += compType.cast(valArray[i]);
				if (i < valArray.length - 1)
					valueStr += ",";
			}
			valueStr += "]";
		}
		System.out.println("setting field " + fieldStr + " to " + valueStr);
		Object oldValue = getField(field, index);
		boolean different = false;
		if (compType == null)
			different = !type.cast(oldValue).equals(type.cast(value));
		else {
			Object[] oldArr = (Object[]) type.cast(oldValue), newArr = (Object[]) type.cast(value);
			if (oldArr.length != newArr.length)
				different = true;
			if (!different)
				for (int i = 0; i < oldArr.length; i++) {
					different = !compType.cast(oldArr[i]).equals(compType.cast(newArr[i]));
					if (different)
						break;
				}
		}
		if (different) {
			modified = true;
			notifyListeners(field, index, oldValue, value);
			if (addUndo)
				undoMan.addEdit(new ProfileEdit(field, index, oldValue, value));
		}
		try {
			impl.setField(field, index, value);
		} catch (ProfileFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets a field's value.
	 * 
	 * @param field
	 *            field to set
	 * @param index
	 *            index to set. will be ignored if
	 *            the field {@linkplain #fieldHasIndexes(String) doesn't have
	 *            indexes}
	 * @param value
	 *            value to set
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static void setField(String field, int index, Object value) {
		setField(field, index, value, true);
	}

	/**
	 * Sets a field's value.
	 * 
	 * @param field
	 *            field to set
	 * @param value
	 *            value to set
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public static void setField(String field, Object value) {
		setField(field, -1, value);
	}

	public static List<String> getAllMethods() {
		if (impl == null)
			return null;
		return impl.getAllMethods();
	}

	public static boolean hasMethod(String method) {
		if (impl == null)
			return false;
		boolean ret = false;
		try {
			ret = impl.hasMethod(method);
		} catch (ProfileMethodException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	public static int getMethodArgNum(String method) {
		if (impl == null)
			return -1;
		int ret = -1;
		try {
			ret = impl.getMethodArgNum(method);
		} catch (ProfileMethodException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	public static Class<?>[] getMethodArgTypes(String method) {
		if (impl == null)
			return null;
		Class<?>[] ret = null;
		try {
			ret = impl.getMethodArgTypes(method);
		} catch (ProfileMethodException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	public static Class<?> getMethodRetType(String method) {
		if (impl == null)
			return null;
		Class<?> ret = null;
		try {
			ret = impl.getMethodRetType(method);
		} catch (ProfileMethodException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	public static Object callMethod(String method, Object... args) {
		if (impl == null)
			return null;
		Object ret = null;
		try {
			ret = impl.callMethod(method, new FieldChangeRecorder() {
				@Override
				public void addChange(String field, int index, Object oldVal, Object newVal) {
					notifyListeners(field, index, oldVal, newVal);
				}
			}, args);
		} catch (ProfileMethodException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

}

package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.leo.cse.backend.profile.ProfileManager.FieldChangeRecorder;
import com.leo.cse.backend.profile.ProfileManager.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager.ProfileMethodException;

public abstract class Profile {

	public interface ProfileField {

		public Class<?> getType();

		public boolean acceptsValue(Object value);

		public Object getValue(int index);

		public void setValue(int index, Object value);

		public default boolean hasIndexes() {
			return false;
		}

		public default int getMinumumIndex() {
			return -1;
		}

		public default int getMaximumIndex() {
			return -1;
		}

	}

	public interface ProfileMethod {

		public Class<?>[] getArgTypes();

		public Class<?> getRetType();

		public Object call(FieldChangeRecorder fcr, Object... args);

	}

	protected File loadedFile;
	protected String header;
	protected String flagH;

	/**
	 * Creates a new blank profile.
	 */
	public abstract void create();

	/**
	 * Loads a profile.
	 * 
	 * @param file
	 *            file to load
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract void load(File file) throws IOException;

	/**
	 * Save a profile.
	 * 
	 * @param file
	 *            file to save to
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public abstract void save(File file) throws IOException;

	/**
	 * Unloads the currently loaded profile.
	 */
	public abstract void unload();

	/**
	 * Gets the loaded file.
	 * 
	 * @return loaded file, or <code>null</code> if no file is loaded
	 */
	public File getLoadedFile() {
		return loadedFile;
	}

	/**
	 * Gets the profile header.
	 * 
	 * @return profile header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Sets the profile header for validation.
	 * 
	 * @param header
	 *            new profile header
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Gets the flag section header.
	 * 
	 * @return flag header
	 */
	public String getFlagHeader() {
		return flagH;
	}

	/**
	 * Sets the flag section header for validation.
	 * 
	 * @param flagH
	 *            new flag header
	 */
	public void setFlagHeader(String flagH) {
		this.flagH = flagH;
	}

	protected Map<String, ProfileField> fields;
	protected List<String> fieldNames;
	protected Map<String, ProfileMethod> methods;
	protected List<String> methodNames;

	protected Profile() {
		fields = new HashMap<>();
		fieldNames = new LinkedList<>();
		methods = new HashMap<>();
		methodNames = new LinkedList<>();
	}

	protected void addField(String fieldName, ProfileField field) throws ProfileFieldException {
		if (fieldName == null)
			throw new ProfileFieldException("fieldName == null!");
		if (fields.containsKey(fieldName))
			throw new ProfileFieldException("Field " + fieldName + " is already defined!");
		if (field == null)
			throw new ProfileFieldException("field == null!");
		if (field.getType() == null)
			throw new ProfileFieldException("field.getType() == null!");
		fields.put(fieldName, field);
		fieldNames.add(fieldName);
	}

	protected void addMethod(String methodName, ProfileMethod method) throws ProfileMethodException {
		if (methodName == null)
			throw new ProfileMethodException("methodName == null!");
		if (methods.containsKey(methodName))
			throw new ProfileMethodException("Method " + methodName + " is already defined!");
		if (method == null)
			throw new ProfileMethodException("method == null!");
		methods.put(methodName, method);
		methodNames.add(methodName);
	}

	public List<String> getAllFields() {
		return Collections.unmodifiableList(fieldNames);
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
	public boolean hasField(String field) throws ProfileFieldException {
		return fields.containsKey(field);
	}

	protected void assertHasField(String field) throws ProfileFieldException {
		if (!hasField(field))
			throw new ProfileFieldException("Field " + field + " is not defined!");
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
	public Class<?> getFieldType(String field) throws ProfileFieldException {
		assertHasField(field);
		return fields.get(field).getType();
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
	public boolean fieldHasIndexes(String field) throws ProfileFieldException {
		assertHasField(field);
		return fields.get(field).hasIndexes();
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
	public int getFieldMinimumIndex(String field) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (!fieldObj.hasIndexes())
			throw new ProfileFieldException("Field " + field + " is not indexed!");
		return fieldObj.getMinumumIndex();
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
	public int getFieldMaximumIndex(String field) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (!fieldObj.hasIndexes())
			throw new ProfileFieldException("Field " + field + " is not indexed!");
		return fieldObj.getMaximumIndex();
	}

	/**
	 * Checks if a field accepts a value.
	 * 
	 * @param field
	 *            field to check
	 * @param value
	 *            value to check
	 * @return <code>true</code> if the value is acceptable, <code>false</code>
	 *         otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException {
		assertHasField(field);
		Class<?> fieldType = getFieldType(field);
		if (fieldType == null)
			throw new ProfileFieldException("Field " + field + " does not have value type!");
		if (!fieldType.isInstance(value))
			return false;
		return fields.get(field).acceptsValue(value);
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
	public Object getField(String field, int index) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (fieldObj.hasIndexes())
			if (index < fieldObj.getMinumumIndex() || index > fieldObj.getMaximumIndex())
				throw new ProfileFieldException("Index " + index + " is out of bounds for field!");
		return fieldObj.getValue(index);
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
	public void setField(String field, int index, Object value) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (fieldObj.hasIndexes())
			if (index < fieldObj.getMinumumIndex() || index > fieldObj.getMaximumIndex())
				throw new ProfileFieldException("Index " + index + " is out of bounds for field " + field + "!");
		fieldObj.setValue(index, value);
	}

	public List<String> getAllMethods() {
		return Collections.unmodifiableList(methodNames);
	}

	public boolean hasMethod(String method) throws ProfileMethodException {
		return methods.containsKey(method);
	}

	protected void assertHasMethod(String method) throws ProfileMethodException {
		if (!hasMethod(method))
			throw new ProfileMethodException("Method " + method + " is not defined!");
	}

	public int getMethodArgNum(String method) throws ProfileMethodException {
		assertHasMethod(method);
		Class<?>[] argTypes = methods.get(method).getArgTypes();
		if (argTypes == null)
			return 0;
		return argTypes.length;
	}

	public Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getArgTypes();
	}

	public Class<?> getMethodRetType(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getRetType();
	}

	public Object callMethod(String method, FieldChangeRecorder fmcr, Object... args) throws ProfileMethodException {
		assertHasMethod(method);
		int argNum = getMethodArgNum(method);
		if (argNum > 0 && args == null)
			throw new ProfileMethodException("Amount of arguments is insufficent, is 0 but should be " + argNum + "!");
		if (args.length < argNum)
			throw new ProfileMethodException(
					"Amount of arguments is insufficent, is " + args.length + " but should be " + argNum + "!");
		Class<?>[] argTypes = getMethodArgTypes(method);
		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++)
				if (!argTypes[i].isInstance(args[i]))
					throw new ProfileMethodException("Argument " + i + " has bad type: " + argTypes[i].getName()
							+ " was expected, but " + args[i].getClass().getName() + " was received instead!");
		}
		return methods.get(method).call(fmcr, args);
	}

}

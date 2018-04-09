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

	/**
	 * Represents a field in the profile.
	 *
	 * @author Leo
	 *
	 */
	public interface ProfileField {

		/**
		 * Gets the type of the field.<br />
		 * <i>This cannot return <code>null</code>,</i> obviously. That'd be like having
		 * a <code>void</code> Java field.
		 *
		 * @return field type
		 */
		public Class<?> getType();

		/**
		 * Checks if the field accepts this value.
		 *
		 * @param index
		 *            index to check against (or -1 if {@linkplain #hasIndexes()
		 *            the field is indexless})
		 * @param value
		 *            value to check
		 * @return <code>true</code> if value is acceptable, <code>false</code>
		 *         otherwise
		 */
		public boolean acceptsValue(int index, Object value);

		/**
		 * Gets the field's value.
		 *
		 * @param index
		 *            index to get value from (or -1 if {@linkplain #hasIndexes() the
		 *            field is indexless})
		 * @return value of field
		 */
		public Object getValue(int index);

		/**
		 * Sets the field's value.
		 *
		 * @param index
		 *            index to set (or -1 if {@linkplain #hasIndexes() the field is
		 *            indexless})
		 * @param value
		 *            value to set to
		 */
		public void setValue(int index, Object value);

		/**
		 * Checks if the field is indexed.
		 *
		 * @return <code>true</code> if has indexes, <code>false</code> otherwise.
		 */
		public default boolean hasIndexes() {
			return false;
		}

		/**
		 * Gets the minimum index of the field.
		 *
		 * @return minimum index of the field (or -1 if {@linkplain #hasIndexes() the
		 *         field is indexless})
		 */
		public default int getMinimumIndex() {
			return -1;
		}

		/**
		 * Gets the maximum index of the field
		 *
		 * @return maximum index of the field (or -1 if {@linkplain #hasIndexes() the
		 *         field is indexless})
		 */
		public default int getMaximumIndex() {
			return -1;
		}

		/**
		 * Checks if an index is valid.
		 * 
		 * @param index
		 *            index to check
		 * @return <code>true</code> if index is valid, <code>false</code> otherwise
		 */
		public default boolean isValidIndex(int index) {
			return index >= getMinimumIndex() && index <= getMaximumIndex();
		}

	}

	/**
	 * Represents a profile method - basically a function that does whatever to the
	 * profile. Can also take arguments.
	 *
	 * @author Leo
	 *
	 */
	public interface ProfileMethod {

		/**
		 * 0-length type array to return from {@link #getArgTypes()} if the method
		 * doesn't require any arguments.
		 */
		public Class<?>[] NO_ARGS = new Class<?>[0];

		/**
		 * Gets the method argument's types.<br />
		 * This can also be used to check the number
		 * of arguments this method has, by checking the <code>length</code> field of
		 * the returned array.<br />
		 * <i>This cannot return <code>null</code>.</i> Methods without arguments will
		 * return a 0-length array when this method is called.
		 *
		 * @return array of argument types
		 */
		public default Class<?>[] getArgTypes() {
			return NO_ARGS;
		}

		/**
		 * Gets the method's return value type.<br />
		 * This can also return <code>null</code>, which is equivalent to a Java
		 * <code>void</code> method in that it doesn't return anything.
		 *
		 * @return type of method return value, or <code>null</code> if method doesn't
		 *         return anything
		 */
		public default Class<?> getRetType() {
			return null;
		}

		/**
		 * Invokes the method.
		 *
		 * @param fcr
		 *            field change recorder
		 * @param args
		 *            arguments passed to the method, or <code>null</code> if no
		 *            arguments were passed
		 * @return result of the method, or <code>null</code> if method doesn't return
		 *         anything
		 */
		public Object call(FieldChangeRecorder fcr, Object... args);

	}

	/**
	 * The currently loaded file.
	 */
	protected File loadedFile;
	/**
	 * Profile header. Used for verifying if the current profile is valid.
	 */
	protected String header;
	/**
	 * Flag section header. Used for verifying if the current profile is valid.
	 */
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

	/**
	 * Map of registered fields, with the keys being the field names.
	 */
	protected Map<String, ProfileField> fields;
	/**
	 * List of registered field names.
	 */
	protected List<String> fieldNames;
	/**
	 * Map of registered methods, with the keys being the method names.
	 */
	protected Map<String, ProfileMethod> methods;
	/**
	 * List of registered method names.
	 */
	protected List<String> methodNames;

	/**
	 * Initializes the field and method maps.
	 */
	protected Profile() {
		fields = new HashMap<>();
		fieldNames = new LinkedList<>();
		methods = new HashMap<>();
		methodNames = new LinkedList<>();
	}

	/**
	 * Registers a new field.
	 *
	 * @param fieldName
	 *            name to register field as
	 * @param field
	 *            the field itself
	 * @throws ProfileFieldException
	 *             if the field is detected to be invalid or duplicate.
	 */
	protected void addField(String fieldName, ProfileField field) throws ProfileFieldException {
		if (fieldName == null)
			throw new ProfileFieldException("fieldName == null!");
		if (fieldName.isEmpty())
			throw new ProfileFieldException("fieldName cannot be empty!");
		if (fields.containsKey(fieldName))
			throw new ProfileFieldException("Field " + fieldName + " is already defined!");
		if (field == null)
			throw new ProfileFieldException("field == null!");
		if (field.getType() == null)
			throw new ProfileFieldException("field.getType() == null!");
		fields.put(fieldName, field);
		fieldNames.add(fieldName);
	}

	/**
	 * Registers a new method.
	 *
	 * @param methodName
	 *            name to register method as
	 * @param method
	 *            the method itself
	 * @throws ProfileMethodException
	 *             if the method is detected to be invalid or duplicate.
	 */
	protected void addMethod(String methodName, ProfileMethod method) throws ProfileMethodException {
		if (methodName == null)
			throw new ProfileMethodException("methodName == null!");
		if (methodName.isEmpty())
			throw new ProfileMethodException("methodName cannot be empty!");
		if (methods.containsKey(methodName))
			throw new ProfileMethodException("Method " + methodName + " is already defined!");
		if (method == null)
			throw new ProfileMethodException("method == null!");
		if (method.getArgTypes() == null)
			throw new ProfileMethodException(
					"method.getArgTypes() == null! If method doesn't take any arguments, return a 0-length array!");
		methods.put(methodName, method);
		methodNames.add(methodName);
	}

	/**
	 * Gets all registered field names.
	 *
	 * @return all field names
	 */
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

	/**
	 * Like {@link #hasField(String)}, but throws an exception if the field doesn't
	 * exist.
	 *
	 * @param field
	 *            field to check
	 * @throws ProfileFieldException
	 *             if the field doesn't exist.
	 */
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
		return fieldObj.getMinimumIndex();
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
	public boolean fieldAcceptsValue(String field, int index, Object value) throws ProfileFieldException {
		assertHasField(field);
		Class<?> fieldType = getFieldType(field);
		if (fieldType == null)
			throw new ProfileFieldException("Field " + field + " does not have value type!");
		if (!fieldType.isInstance(value))
			return false;
		return fields.get(field).acceptsValue(index, value);
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
			if (!fieldObj.isValidIndex(index))
				throw new ProfileFieldException("Index " + index + " is invalid for field!");
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
			if (index < fieldObj.getMinimumIndex() || index > fieldObj.getMaximumIndex())
				throw new ProfileFieldException("Index " + index + " is out of bounds for field " + field + "!");
		fieldObj.setValue(index, value);
	}

	/**
	 * Gets all registered method names.
	 *
	 * @return all method names
	 */
	public List<String> getAllMethods() {
		return Collections.unmodifiableList(methodNames);
	}

	/**
	 * Checks if a method exists.
	 *
	 * @param method
	 *            method to check
	 * @return <code>true</code> if it exists, <code>false</code> otherwise
	 * @throws ProfileMethodException
	 *             if a method-related exception occurs.
	 */
	public boolean hasMethod(String method) throws ProfileMethodException {
		return methods.containsKey(method);
	}

	/**
	 * Like {@link #hasMethod(String)}, but throws an exception if the field doesn't
	 * exist.
	 *
	 * @param method
	 *            method to check
	 * @throws ProfileMethodException
	 *             if the method doesn't exist.
	 */
	protected void assertHasMethod(String method) throws ProfileMethodException {
		if (!hasMethod(method))
			throw new ProfileMethodException("Method " + method + " is not defined!");
	}

	/**
	 * Gets the number of arguments a method requires.
	 *
	 * @param method
	 *            method to check
	 * @return number of arguments for method
	 * @throws ProfileMethodException
	 *             if a method-related exception occurs.
	 */
	public int getMethodArgNum(String method) throws ProfileMethodException {
		assertHasMethod(method);
		Class<?>[] argTypes = methods.get(method).getArgTypes();
		if (argTypes == null)
			return 0;
		return argTypes.length;
	}

	/**
	 * Gets the types of arguments a method requires.
	 *
	 * @param method
	 *            method to check
	 * @return types of arguments for method
	 * @throws ProfileMethodException
	 *             if a method-related exception occurs.
	 */
	public Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getArgTypes();
	}

	/**
	 * Gets the type of the value a method returns.
	 *
	 * @param method
	 *            method to check
	 * @return return type of method
	 * @throws ProfileMethodException
	 *             if a method-related exception occurs.
	 */
	public Class<?> getMethodRetType(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getRetType();
	}

	/**
	 * Invokes a method.
	 * 
	 * @param method
	 *            method to invoke
	 * @param fcr
	 *            field change recorder to pass to method
	 * @param args
	 *            arguments to pass to method, or <code>null</code> if it doesn't
	 *            require any arguments
	 * @return return value of method, or <code>null</code> if it doesn't return
	 *         anything
	 * @throws ProfileMethodException
	 *             if a method-related exception occurs.
	 */
	public Object callMethod(String method, FieldChangeRecorder fcr, Object... args) throws ProfileMethodException {
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
		return methods.get(method).call(fcr, args);
	}

}

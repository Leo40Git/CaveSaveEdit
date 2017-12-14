package com.leo.cse.backend.profile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.leo.cse.backend.profile.ProfileManager.FieldModChangeRecorder;

public abstract class Profile implements IProfile {

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

		public Object call(FieldModChangeRecorder fmcr, Object... args);

	}

	protected File loadedFile;
	protected String header;
	protected String flagH;

	@Override
	public File getLoadedFile() {
		return loadedFile;
	}

	@Override
	public String getHeader() {
		return header;
	}

	@Override
	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String getFlagHeader() {
		return flagH;
	}

	@Override
	public void setFlagHeader(String flagH) {
		this.flagH = flagH;
	}

	protected Map<String, ProfileField> fields;
	protected Map<String, ProfileMethod> methods;

	protected Profile() {
		fields = new HashMap<>();
		methods = new HashMap<>();
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
	}

	protected void addMethod(String methodName, ProfileMethod method) throws ProfileMethodException {
		if (methodName == null)
			throw new ProfileMethodException("methodName == null!");
		if (methods.containsKey(methodName))
			throw new ProfileMethodException("Method " + methodName + " is already defined!");
		if (method == null)
			throw new ProfileMethodException("method == null!");
		methods.put(methodName, method);
	}

	@Override
	public boolean hasField(String field) throws ProfileFieldException {
		return fields.containsKey(field);
	}

	protected void assertHasField(String field) throws ProfileFieldException {
		if (!hasField(field))
			throw new ProfileFieldException("Field " + field + " is not defined!");
	}

	@Override
	public Class<?> getFieldType(String field) throws ProfileFieldException {
		assertHasField(field);
		return fields.get(field).getType();
	}

	@Override
	public boolean fieldHasIndexes(String field) throws ProfileFieldException {
		assertHasField(field);
		return fields.get(field).hasIndexes();
	}

	@Override
	public int getFieldMinimumIndex(String field) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (!fieldObj.hasIndexes())
			throw new ProfileFieldException("Field " + field + " is not indexed!");
		return fieldObj.getMinumumIndex();
	}

	@Override
	public int getFieldMaximumIndex(String field) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (!fieldObj.hasIndexes())
			throw new ProfileFieldException("Field " + field + " is not indexed!");
		return fieldObj.getMaximumIndex();
	}

	@Override
	public boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException {
		assertHasField(field);
		Class<?> fieldType = getFieldType(field);
		if (fieldType == null)
			throw new ProfileFieldException("Field " + field + " does not have value type!");
		if (!fieldType.isInstance(value))
			return false;
		return fields.get(field).acceptsValue(value);
	}

	@Override
	public Object getField(String field, int index) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (fieldObj.hasIndexes())
			if (index < fieldObj.getMinumumIndex() || index > fieldObj.getMaximumIndex())
				throw new ProfileFieldException("Index " + index + " is out of bounds for field!");
		return fieldObj.getValue(index);
	}

	@Override
	public void setField(String field, int index, Object value) throws ProfileFieldException {
		assertHasField(field);
		ProfileField fieldObj = fields.get(field);
		if (fieldObj.hasIndexes())
			if (index < fieldObj.getMinumumIndex() || index > fieldObj.getMaximumIndex())
				throw new ProfileFieldException("Index " + index + " is out of bounds for field " + field + "!");
		fieldObj.setValue(index, value);
	}

	@Override
	public boolean hasMethod(String method) throws ProfileMethodException {
		return methods.containsKey(method);
	}

	protected void assertHasMethod(String method) throws ProfileMethodException {
		if (!hasMethod(method))
			throw new ProfileMethodException("Method " + method + " is not defined!");
	}

	@Override
	public int getMethodArgNum(String method) throws ProfileMethodException {
		assertHasMethod(method);
		Class<?>[] argTypes = methods.get(method).getArgTypes();
		if (argTypes == null)
			return 0;
		return argTypes.length;
	}

	@Override
	public Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getArgTypes();
	}

	@Override
	public Class<?> getMethodRetType(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getRetType();
	}

	@Override
	public Object callMethod(String method, FieldModChangeRecorder fmcr, Object... args) throws ProfileMethodException {
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

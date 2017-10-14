package com.leo.cse.backend.profile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class CommonProfile implements Profile {

	public interface ProfileField {

		public Class<?> getType();

		public boolean acceptsValue(Object value);

		public Object getValue(int index);

		public void setValue(int index, Object value);

	}

	public interface ProfileMethod {

		public int getArgNum();

		public Class<?>[] getArgType();

		public Class<?> getRetType();

		public Object call(Object... args);

	}

	protected File loadedFile;
	protected int loadedSection;
	protected String header;
	protected String flagH;

	@Override
	public File getLoadedFile() {
		return loadedFile;
	}

	@Override
	public int getLoadedSection() {
		return loadedSection;
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

	protected CommonProfile() {
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
		if (method.getArgType() == null)
			throw new ProfileMethodException("method.getArgType() == null!");
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
	public boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException {
		assertHasField(field);
		Class<?> fieldType = getFieldType(field);
		if (fieldType == null)
			throw new ProfileFieldException("Field does not have value type!");
		if (!fieldType.isInstance(value))
			return false;
		return fields.get(field).acceptsValue(value);
	}

	@Override
	public Object getField(String field, int index) throws ProfileFieldException {
		assertHasField(field);
		return fields.get(field).getValue(index);
	}

	@Override
	public void setField(String field, int index, Object value) throws ProfileFieldException {
		assertHasField(field);
		fields.get(field).setValue(index, value);
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
		return methods.get(method).getArgNum();
	}

	@Override
	public Class<?>[] getMethodArgType(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getArgType();
	}

	@Override
	public Class<?> getMethodRetType(String method) throws ProfileMethodException {
		assertHasMethod(method);
		return methods.get(method).getRetType();
	}

	@Override
	public Object callMethod(String method, Object... args) throws ProfileMethodException {
		assertHasMethod(method);
		if (args.length < getMethodArgNum(method))
			throw new ProfileMethodException("Amount of arguments is incorrect, is " + args.length + " but should be "
					+ getMethodArgNum(method) + "!");
		Class<?>[] argTypes = getMethodArgType(method);
		if (argTypes == null && getMethodArgNum(method) != 0)
			throw new ProfileMethodException("Method does not have argument type array!");
		if (argTypes.length < args.length)
			throw new ProfileMethodException("Method does not have enough argument types, is " + argTypes.length
					+ " but should be " + args.length + "!");
		for (int i = 0; i < args.length; i++)
			if (!argTypes[i].isInstance(args[i]))
				throw new ProfileMethodException("Argument " + i + " has bad type!");
		return methods.get(method).call(args);
	}

}

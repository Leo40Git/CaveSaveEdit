package com.leo.cse.backend.profile;

import java.util.HashMap;
import java.util.Map;

public class CommonProfile extends Profile {

	public interface ProfileField {

		public Class<?> getType();

		public abstract boolean acceptsValue(Object value);

		public abstract Object getValue();

		public abstract void setValue(Object value);

	}

	public interface ProfileMethod {

		public int getArgNum();

		public Class<?>[] getArgType();

		public Class<?> getRetType();

		public abstract Object call(Object... args);

	}

	protected String header;
	protected String flagH;

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
	
	protected void addField(String fieldName, ProfileField field) {
		if (fieldName == null)
			return;
		if (fields.containsKey(fieldName))
			return;
		if (field == null)
			return;
		if (field.getType() == null)
			return;
		fields.put(fieldName, field);
	}
	
	protected void addMethod(String methodName, ProfileMethod method) {
		if (methodName == null)
			return;
		if (methods.containsKey(methodName))
			return;
		if (method == null)
			return;
		if (method.getArgType() == null)
			return;
		methods.put(methodName, method);
	}

	@Override
	public boolean hasField(String field) {
		return fields.containsKey(field);
	}

	@Override
	public Class<?> getFieldType(String field) {
		if (!hasField(field))
			return null;
		return fields.get(field).getType();
	}

	@Override
	public boolean fieldAcceptsValue(String field, Object value) {
		if (!hasField(field))
			return false;
		Class<?> fieldType = getFieldType(field);
		if (fieldType == null)
			return false;
		if (!fieldType.isInstance(value))
			return false;
		return fields.get(field).acceptsValue(value);
	}

	@Override
	public Object getField(String field) {
		if (!hasField(field))
			return null;
		return fields.get(field).getValue();
	}

	@Override
	public void setField(String field, Object value) {
		if (!hasField(field))
			return;
		fields.get(field).setValue(value);
	}

	@Override
	public boolean hasMethod(String method) {
		return methods.containsKey(method);
	}

	@Override
	public int getMethodArgNum(String method) {
		if (!hasMethod(method))
			return 0;
		return methods.get(method).getArgNum();
	}

	@Override
	public Class<?>[] getMethodArgType(String method) {
		if (!hasMethod(method))
			return null;
		return methods.get(method).getArgType();
	}

	@Override
	public Class<?> getMethodRetType(String method) {
		if (!hasMethod(method))
			return null;
		return methods.get(method).getRetType();
	}

	@Override
	public Object callMethod(String method, Object... args) {
		if (!hasMethod(method))
			return null;
		if (args.length != getMethodArgNum(method))
			return null;
		Class<?>[] argTypes = getMethodArgType(method);
		if (argTypes == null)
			return null;
		if (argTypes.length != args.length)
			return null;
		for (int i = 0; i < args.length; i++)
			if (!argTypes[i].isInstance(args[i]))
				return null;
		return methods.get(method).call(args);
	}

}

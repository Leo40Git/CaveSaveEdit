package com.leo.cse.backend.profile;

/**
 * 
 * @author Leo
 *
 */
public abstract class Profile {

	public abstract String getHeader();

	public abstract void setHeader(String header);

	public abstract String getFlagHeader();

	public abstract void setFlagHeader(String flagH);

	public abstract boolean hasField(String field);

	public abstract Class<?> getFieldType(String field);
	
	public abstract boolean fieldAcceptsValue(String field, Object value);

	public abstract Object getField(String field);

	public abstract void setField(String field, Object value);
	
	public abstract boolean hasMethod(String method);
	
	public abstract int getMethodArgNum(String method);
	
	public abstract Class<?>[] getMethodArgType(String method);
	
	public abstract Class<?> getMethodRetType(String method);
	
	public abstract Object callMethod(String method, Object... args);

}

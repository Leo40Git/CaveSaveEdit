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

	public abstract Object getField(String field);

	public abstract void setField(String field, Object value);
	
	public abstract boolean hasFunction(String func);
	
	public abstract int getFunctionArgNum(String func);
	
	public abstract Class<?>[] getFunctionArgType(String func);
	
	public abstract Class<?> getFunctionRetType(String func);
	
	public abstract void doFunction(String func, Object[] args);

}

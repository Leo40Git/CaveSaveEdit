package com.leo.cse.backend.profile;

/**
 * 
 * @author Leo
 *
 */
public abstract class Profile {
	
	public class ProfileFieldException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public ProfileFieldException(String message) {
	        super(message);
	    }
		
	}
	
	public class ProfileMethodException extends Exception {

		private static final long serialVersionUID = 1L;
		
		public ProfileMethodException(String message) {
	        super(message);
	    }
		
	}

	public abstract String getHeader();

	public abstract void setHeader(String header);

	public abstract String getFlagHeader();

	public abstract void setFlagHeader(String flagH);

	public abstract boolean hasField(String field) throws ProfileFieldException;

	public abstract Class<?> getFieldType(String field) throws ProfileFieldException;
	
	public abstract boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException;

	public abstract Object getField(String field) throws ProfileFieldException;

	public abstract void setField(String field, Object value) throws ProfileFieldException;
	
	public abstract boolean hasMethod(String method) throws ProfileMethodException;
	
	public abstract int getMethodArgNum(String method) throws ProfileMethodException;
	
	public abstract Class<?>[] getMethodArgType(String method) throws ProfileMethodException;
	
	public abstract Class<?> getMethodRetType(String method) throws ProfileMethodException;
	
	public abstract Object callMethod(String method, Object... args) throws ProfileMethodException;

}

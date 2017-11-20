package com.leo.cse.backend.profile;

import java.io.File;
import java.io.IOException;

/**
 * Basic profile interface. Superinterface to all classes that can be used as
 * implementations in the {@link ProfileManager}.
 * 
 * @author Leo
 *
 */
public interface IProfile {

	/**
	 * 
	 * @author Leo
	 *
	 */
	public class ProfileFieldException extends Exception {

		private static final long serialVersionUID = 1L;

		public ProfileFieldException(String message) {
			super(message);
		}

	}

	/**
	 * 
	 * @author Leo
	 *
	 */
	public class ProfileMethodException extends Exception {

		private static final long serialVersionUID = 1L;

		public ProfileMethodException(String message) {
			super(message);
		}

	}

	public void load(File file, int section) throws IOException;

	public void loadSection(int section) throws IOException;

	public void save(File file, int section) throws IOException;

	public File getLoadedFile();

	public boolean supportsSections();

	public int getLoadedSection();

	public String getHeader();

	public void setHeader(String header);

	public String getFlagHeader();

	public void setFlagHeader(String flagH);

	public boolean hasField(String field) throws ProfileFieldException;

	public Class<?> getFieldType(String field) throws ProfileFieldException;
	
	public boolean fieldHasIndexes(String field) throws ProfileFieldException;
	
	public int getFieldMinimumIndex(String field) throws ProfileFieldException;
	
	public int getFieldMaximumIndex(String field) throws ProfileFieldException;

	public boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException;

	public Object getField(String field, int index) throws ProfileFieldException;

	public void setField(String field, int index, Object value) throws ProfileFieldException;

	public boolean hasMethod(String method) throws ProfileMethodException;

	public int getMethodArgNum(String method) throws ProfileMethodException;

	public Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException;

	public Class<?> getMethodRetType(String method) throws ProfileMethodException;

	public Object callMethod(String method, Object... args) throws ProfileMethodException;

}

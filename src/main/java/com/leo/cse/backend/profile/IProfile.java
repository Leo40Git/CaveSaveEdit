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
	 * Exception from accessing profile fields.
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
	 * Exception from accessing profile methods.
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

	/**
	 * Loads a profile.
	 * 
	 * @param file
	 *            file to load
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void load(File file) throws IOException;

	/**
	 * Save a profile.
	 * 
	 * @param file
	 *            file to save to
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void save(File file) throws IOException;

	/**
	 * Gets the loaded file.
	 * 
	 * @return loaded file
	 */
	public File getLoadedFile();

	/**
	 * Gets the profile header.
	 * 
	 * @return profile header
	 */
	public String getHeader();

	/**
	 * Sets the profile header.
	 * 
	 * @param header
	 *            new profile header
	 */
	public void setHeader(String header);

	/**
	 * Gets the flag profile header.
	 * 
	 * @return flags profile header
	 */
	public String getFlagHeader();

	/**
	 * Sets the flag profile header.
	 * 
	 * @param flagH
	 *            new flag profile header
	 */
	public void setFlagHeader(String flagH);

	/**
	 * Checks if a field exists.
	 * 
	 * @param field
	 *            field to check
	 * @return <code>true</code> if it exists, <code>false</code> otherwise
	 * @throws ProfileFieldException
	 */
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

	public String[] getMethodModifiedFields(String method) throws ProfileMethodException;

	public Object callMethod(String method, Object... args) throws ProfileMethodException;

}

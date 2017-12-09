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
	 *             if a field-related exception occurs.
	 */
	public boolean hasField(String field) throws ProfileFieldException;

	/**
	 * Gets a field's value type.
	 * 
	 * @param field
	 *            field to check
	 * @return the field's type
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public Class<?> getFieldType(String field) throws ProfileFieldException;

	/**
	 * Checks if a field has indexes.
	 * 
	 * @param field
	 *            field to check
	 * @return <code>true</code> if it has indexes, <code>false</code> otherwise
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public boolean fieldHasIndexes(String field) throws ProfileFieldException;

	/**
	 * Gets the minimum index of a field.
	 * 
	 * @param field
	 *            field to check
	 * @return minimum field index
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public int getFieldMinimumIndex(String field) throws ProfileFieldException;

	/**
	 * Gets the maximum index of a field.
	 * 
	 * @param field
	 *            field to check
	 * @return maximum field index
	 * @throws ProfileFieldException
	 *             if a field-related exception occurs.
	 */
	public int getFieldMaximumIndex(String field) throws ProfileFieldException;

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
	public boolean fieldAcceptsValue(String field, Object value) throws ProfileFieldException;

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
	public Object getField(String field, int index) throws ProfileFieldException;

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
	public void setField(String field, int index, Object value) throws ProfileFieldException;

	public boolean hasMethod(String method) throws ProfileMethodException;

	public int getMethodArgNum(String method) throws ProfileMethodException;

	public Class<?>[] getMethodArgTypes(String method) throws ProfileMethodException;

	public Class<?> getMethodRetType(String method) throws ProfileMethodException;

	public String[] getMethodModifiedFields(String method) throws ProfileMethodException;

	public Object callMethod(String method, Object... args) throws ProfileMethodException;

}

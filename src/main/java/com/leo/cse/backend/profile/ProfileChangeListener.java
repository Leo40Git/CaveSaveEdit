package com.leo.cse.backend.profile;

/**
 * Listens to changes to the currently loaded {@link Profile}.
 * 
 * @author Leo
 *
 */
public interface ProfileChangeListener {

	/**
	 * Gets called every time a field in the profile is changed.
	 * 
	 * @param field
	 *            field being modified
	 * @param id
	 *            index of field being modified, or -1 if the field does not have
	 *            indexes
	 */
	public void onChange(String field, int id);

}

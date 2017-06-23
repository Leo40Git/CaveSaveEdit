package com.leo.cse.backend;

/**
 * Listens to changes to the currently loaded {@link Profile}.
 * 
 * @author Leo
 *
 */
public interface ProfileChangeListener {

	/**
	 * Gets called every time a field in the profile is changed.
	 */
	public void onChanged();

}

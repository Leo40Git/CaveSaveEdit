package com.leo.cse.backend.exe;

/**
 * Listens to {@link ExeData} being loaded and unloaded.
 * 
 * @author Leo
 *
 */
public interface ExeLoadListener {

	/**
	 * Called before anything is done.
	 * 
	 * @param plusMode
	 *            <code>true</code> if loading CS+, <code>false</code> otherwise
	 */
	public void preLoad(boolean plusMode);

	/**
	 * Called before resource files are loaded.
	 * 
	 * @param plusMode
	 *            <code>true</code> if loading CS+, <code>false</code> otherwise
	 */
	public void load(boolean plusMode);

	/**
	 * Called after everything is done.
	 * 
	 * @param plusMode
	 *            <code>true</code> if loading CS+, <code>false</code> otherwise
	 */
	public void postLoad(boolean plusMode);

	/**
	 * Called when data gets unloaded.
	 */
	public void unload();

}

package com.leo.cse.backend.exe;

/**
 * Listens to game/mod data being loaded and unloaded.
 *
 * @author Leo
 * @see ExeData
 */
public interface ExeLoadListener {

	/**
	 * Invoked when an event occurs.
	 *
	 * @param event
	 *            event name
	 * @param loadName
	 *            resource name
	 * @param loadId
	 *            resource ID
	 * @param loadIdMax
	 *            maximum resource ID
	 */
	public void onEvent(String event, String loadName, int loadId, int loadIdMax);

	/**
	 * Invoked when a sub-event occurs.
	 *
	 * @param event
	 *            event name
	 * @param loadName
	 *            resource name
	 * @param loadId
	 *            resource ID
	 * @param loadIdMax
	 *            maximum resource ID
	 */
	public void onSubevent(String event, String loadName, int loadId, int loadIdMax);

}

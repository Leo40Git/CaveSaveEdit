package com.leo.cse.backend.exe;

/**
 * Listens to {@link ExeData} being loaded and unloaded.
 * 
 * @author Leo
 *
 */
public interface ExeLoadListener {

	public void onEvent(String event, String loadName, int loadId, int loadIdMax);

	public void onSubevent(String event, String loadName, int loadId, int loadIdMax);

}

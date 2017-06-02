package com.leo.cse.backend;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
/**
 * Stores abstract information for a map. An instance of {@link MapInfo} should be used
 * for loading maps.
 * 
 * @author Leo
 *
 */
public class Mapdata {
	
	/**
	 * The map's ID.
	 */
	private int mapNum;
	/**
	 * The map's tileset.
	 */
	private String tileset;
	/**
	 * The map's file name.
	 */
	private String fileName;
	/**
	 * The map's scroll type.
	 */
	private int scrollType;
	/**
	 * The map's background image.
	 */
	private String bgName;
	/**
	 * The map's 1st NPC sheet.
	 */
	private String npcSheet1;
	/**
	 * The map's 2nd NPC sheet.
	 */
	private String npcSheet2;
	/**
	 * The map's name.
	 */
	private String mapName;
	
	/**
	 * Creates a new empty map.
	 * @param mapNum map ID
	 */
	public Mapdata(int mapNum) {
		this.mapNum = mapNum;
		tileset = "0";
		fileName = "0";
		scrollType = 0;
		bgName = "0";
	}

	public int getMapNum() {
		return mapNum;
	}

	public void setMapNum(int mapNum) {
		this.mapNum = mapNum;
	}

	public String getTileset() {
		return tileset;
	}

	public void setTileset(String tileset) {
		this.tileset = tileset;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getScrollType() {
		return scrollType;
	}

	public void setScrollType(int scrollType) {
		this.scrollType = scrollType;
	}

	public String getBgName() {
		return bgName;
	}

	public void setBgName(String bgName) {
		this.bgName = bgName;
	}

	public String getNpcSheet1() {
		return npcSheet1;
	}

	public void setNpcSheet1(String npcSheet1) {
		this.npcSheet1 = npcSheet1;
	}

	public String getNpcSheet2() {
		return npcSheet2;
	}

	public void setNpcSheet2(String npcSheet2) {
		this.npcSheet2 = npcSheet2;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

}

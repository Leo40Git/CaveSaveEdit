package com.leo.cse.backend.exe;

// credit to Noxid for making Booster's Lab open source so I could steal code
// from it
/**
 * Stores abstract information for a map. An instance of {@link MapInfo} should
 * be used
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
	 *
	 * @param mapNum
	 *            map ID
	 */
	public Mapdata(int mapNum) {
		this.mapNum = mapNum;
		tileset = "0";
		fileName = "0";
		scrollType = 0;
		bgName = "0";
	}

	/**
	 * Gets the map's ID.
	 *
	 * @return map ID
	 */
	public int getMapNum() {
		return mapNum;
	}

	/**
	 * Sets the map's ID.
	 *
	 * @param mapNum
	 *            new map ID
	 */
	public void setMapNum(int mapNum) {
		this.mapNum = mapNum;
	}

	/**
	 * Gets the map's tileset.
	 *
	 * @return tileset
	 */
	public String getTileset() {
		return tileset;
	}

	/**
	 * Sets the map's tileset.
	 *
	 * @param tileset
	 *            new tileset
	 */
	public void setTileset(String tileset) {
		this.tileset = tileset;
	}

	/**
	 * Gets the map's file name.
	 *
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the map's file name.
	 *
	 * @param fileName
	 *            new file name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the map's scroll type.
	 *
	 * @return scroll type
	 */
	public int getScrollType() {
		return scrollType;
	}

	/**
	 * Sets the map's scroll type.
	 *
	 * @param scrollType
	 *            new scroll type
	 */
	public void setScrollType(int scrollType) {
		this.scrollType = scrollType;
	}

	/**
	 * Gets the map's background image.
	 *
	 * @return background image
	 */
	public String getBgName() {
		return bgName;
	}

	/**
	 * Sets the map's background image.
	 *
	 * @param bgName
	 *            new background image
	 */
	public void setBgName(String bgName) {
		this.bgName = bgName;
	}

	/**
	 * Gets the map's 1st NPC sheet.
	 *
	 * @return 1st NPC sheet
	 */
	public String getNpcSheet1() {
		return npcSheet1;
	}

	/**
	 * Sets the map's 1st NPC sheet.
	 *
	 * @param npcSheet1
	 *            new 1st NPC sheet
	 */
	public void setNpcSheet1(String npcSheet1) {
		this.npcSheet1 = npcSheet1;
	}

	/**
	 * Gets the map's 2nd NPC sheet.
	 *
	 * @return 2nd NPC sheet
	 */
	public String getNpcSheet2() {
		return npcSheet2;
	}

	/**
	 * Sets the map's 2nd NPC sheet.
	 *
	 * @param npcSheet2
	 *            new 2nd NPC sheet
	 */
	public void setNpcSheet2(String npcSheet2) {
		this.npcSheet2 = npcSheet2;
	}

	/**
	 * Gets the map's name.
	 *
	 * @return display name
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Sets the map's name.
	 *
	 * @param mapName
	 *            new display name
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

}

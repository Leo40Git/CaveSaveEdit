package com.leo.cse.frontend.data;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
public class Mapdata {

	private int mapNum;
	private String tileset;
	private String fileName;
	private int scrollType;
	private String bgName;
	private String npcSheet1;
	private String npcSheet2;
	private String mapName;

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

package com.leo.cse.frontend.data;

public class Mapdata {
	
	private int mapNum;
	private String tileset;
	private String fileName;
	private int scrollType;
	private String bgName;
	private String npcSheet1;
	private String npcSheet2;
	private int bossNum;
	private String mapName;
	private byte[] jpName;
	
	public Mapdata(int mapNum) {
		this.mapNum = mapNum;
		tileset = "0";
		fileName = "0";
		scrollType = 0;
		bgName = "0";
		npcSheet1 = "0";
		npcSheet2 = "0";
		bossNum = 0;
		mapName = "";
		jpName = new byte[32];
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

	public int getBossNum() {
		return bossNum;
	}

	public void setBossNum(int bossNum) {
		this.bossNum = bossNum;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public byte[] getJpName() {
		return jpName;
	}

	public void setJpName(byte[] jpName) {
		this.jpName = jpName;
	}

}

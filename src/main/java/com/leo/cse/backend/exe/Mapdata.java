package com.leo.cse.backend.exe;

import java.nio.ByteBuffer;

import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.exe.ExeData.ModType;

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
	 * The map's special boss ID.
	 */
	private int bossNum;
	/**
	 * The map's name.
	 */
	private String mapName;
	/**
	 * The map's name in Japanese. CS+ only.
	 */
	private byte[] jpName;

	public Mapdata(int num, ByteBuffer buf, ModType format, String charEncoding) {
		mapNum = num;
		switch (format) {
		case STANDARD: // from exe
			/*
			typedef struct {
				   char tileset[32];
				   char filename[32];
				   char scrollType[4];
				   char bgName[32];
				   char npc1[32];
				   char npc2[32];
				   char bossNum;
				   char mapName[35];
				}nMapData;
				*/
			byte[] buffer = new byte[0x23];
			buf.get(buffer, 0, 0x20);
			tileset = StrTools.CString(buffer, charEncoding);
			buf.get(buffer, 0, 0x20);
			fileName = StrTools.CString(buffer, charEncoding);
			scrollType = buf.getInt() & 0xFF;
			buf.get(buffer, 0, 0x20);
			bgName = StrTools.CString(buffer, charEncoding);
			buf.get(buffer, 0, 0x20);
			npcSheet1 = StrTools.CString(buffer, charEncoding);
			buf.get(buffer, 0, 0x20);
			npcSheet2 = StrTools.CString(buffer, charEncoding);
			bossNum = buf.get();
			buf.get(buffer, 0, 0x23);
			mapName = StrTools.CString(buffer, charEncoding);
			jpName = new byte[0x20];
			break;
		case PLUS: // from stage.tbl
			/*
			typedef struct {
				   char tileset[32];
				   char filename[32];
				   char scrollType[4];
				   char bgName[32];
				   char npc1[32];
				   char npc2[32];
				   char bossNum;
				   char jpName[32];
				   char mapName[32];
				}nMapData;
				*/
			byte[] buf32 = new byte[32];
			buf.get(buf32);
			tileset = StrTools.CString(buf32, charEncoding);
			buf.get(buf32);
			fileName = StrTools.CString(buf32, charEncoding);
			scrollType = buf.getInt();
			buf.get(buf32);
			bgName = StrTools.CString(buf32, charEncoding);
			buf.get(buf32);
			npcSheet1 = StrTools.CString(buf32, charEncoding);
			buf.get(buf32);
			npcSheet2 = StrTools.CString(buf32, charEncoding);
			bossNum = buf.get();
			buf.get(buf32);
			jpName = buf32.clone();
			buf.get(buf32);
			mapName = StrTools.CString(buf32, charEncoding);
			break;
		default:
			// unknown/unused
			break;
		}
	}

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
		npcSheet1 = "0";
		npcSheet2 = "0";
		bossNum = 0;
		mapName = "Null";
		jpName = new byte[0x20];
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
	 * Gets the map's tileset.
	 *
	 * @return tileset
	 */
	public String getTileset() {
		return tileset;
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
	 * Gets the map's scroll type.
	 *
	 * @return scroll type
	 */
	public int getScrollType() {
		return scrollType;
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
	 * Gets the map's 1st NPC sheet.
	 *
	 * @return 1st NPC sheet
	 */
	public String getNpcSheet1() {
		return npcSheet1;
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
	 * Gets the map's special boss ID.
	 * 
	 * @return boss number
	 */
	public int getBossNum() {
		return bossNum;
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
	 * Gets the map's name in Japanese.
	 * 
	 * @return JP name
	 */
	public byte[] getJpName() {
		return jpName.clone();
	}

}

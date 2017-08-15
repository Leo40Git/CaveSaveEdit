package com.leo.cse.backend.exe;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.carrotlord.string.StrTools;
import com.leo.cse.backend.ResUtils;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

// credit to Noxid for making Booster's Lab open source so I could steal code
// from it
/**
 * Stores information for a mod executable.
 * 
 * @author Leo
 *
 */
public class ExeData {

	/**
	 * Make sure an instance of this class cannot be created.
	 */
	private ExeData() {
	}

	// --------
	// Pointers
	// --------
	// ...well, technically not actual pointers, but file positions
	/**
	 * Pointer to file name for "ArmsItem.tsc".
	 */
	private static final int ARMSITEM_PTR = 0x8C270;
	/**
	 * Pointer to image file extension.
	 */
	private static final int IMG_EXT_PTR = 0x8C280;
	/**
	 * Pointer to name for the "npc.tbl" file.
	 */
	private static final int NPC_TBL_PTR = 0x8C3AB;
	/**
	 * Pointer to name for the "MyChar" graphics file.
	 */
	private static final int MYCHAR_PTR = 0x8C4F0;
	/**
	 * Pointer to name for the "Title" graphics file.
	 */
	private static final int TITLE_PTR = 0x8C4F8;
	/**
	 * Pointer to name for the "ArmsImage" graphics file.
	 */
	private static final int ARMSIMAGE_PTR = 0x8C500;
	/**
	 * Pointer to name for the "Arms" graphics file.
	 */
	private static final int ARMS_PTR = 0x8C50C;
	/**
	 * Pointer to name for the "ItemImage" graphics file.
	 */
	private static final int ITEMIMAGE_PTR = 0x8C514;
	/**
	 * Pointer to name for the "data" folder.
	 */
	private static final int DATA_FOLDER_PTR = 0x8C5BC;
	/**
	 * Pointer to name for the "StageImage" graphics file.
	 */
	private static final int STAGEIMAGE_PTR = 0x8C520;
	/**
	 * Pointer to name for the "NpcSym" graphics file.
	 */
	private static final int NPCSYM_PTR = 0x8C52C;
	/**
	 * Pointer to name for the "NpcRegu" graphics file.
	 */
	private static final int NPCREGU_PTR = 0x8C538;
	/**
	 * Pointer to name for the "TextBox" graphics file.
	 */
	private static final int TEXTBOX_PTR = 0x8C544;
	/**
	 * Pointer to name for the "Caret" graphics file.
	 */
	private static final int CARET_PTR = 0x8C54C;
	/**
	 * Pointer to name for the "Bullet" graphics file.
	 */
	private static final int BULLET_PTR = 0x8C554;
	/**
	 * Pointer to name for the "Face" graphics file.
	 */
	private static final int FACE_PTR = 0x8C55C;
	/**
	 * Pointer to name for the "Fade" graphics file.
	 */
	private static final int FADE_PTR = 0x8C564;
	/**
	 * Pointer to name for the "Loading" graphics file.
	 */
	private static final int LOADING_PTR = 0x8C5FC;
	/**
	 * Pointer to the PXM file tag.
	 */
	private static final int PXM_TAG_PTR = 0x8C67C;
	/**
	 * Pointer to the profile header.
	 */
	private static final int PROFILE_HEADER_PTR = 0x8C70C;
	/**
	 * Pointer to the profile flag section header.
	 */
	private static final int PROFILE_FLAGH_PTR = 0x8C720;
	/**
	 * Pointer to name for the "StageSelect.tsc" file.
	 */
	private static final int STAGESELECT_PTR = 0x8C770;
	/**
	 * Pointer to name for the "Stage" folder.
	 */
	private static final int STAGE_FOLDER_PTR = 0x8C7D4;
	/**
	 * Pointer to prefix for tileset graphics files.
	 */
	private static final int PRT_PREFIX_PTR = 0x8C7DC;
	/**
	 * Pointer to PXA (tile attributes) file extension.
	 */
	private static final int PXA_EXT_PTR = 0x8C7E8;
	/**
	 * Pointer to PXM (map) file extension.
	 */
	private static final int PXM_EXT_PTR = 0x8C7F4;
	/**
	 * Pointer to PXE (entities) file extension.
	 */
	private static final int PXE_EXT_PTR = 0x8C800;
	/**
	 * Pointer to TSC file extension.
	 */
	private static final int TSC_EXT_PTR = 0x8C80C;
	/**
	 * Pointer to name for the "Npc" folder.
	 */
	private static final int NPC_FOLDER_PTR = 0x8C81C;
	/**
	 * Pointer to prefix for NPC sheet graphics files.
	 */
	private static final int NPC_PREFIX_PTR = 0x8C820;

	/**
	 * Array of pointers which point to string values.
	 */
	private static final int[] STRING_POINTERS = new int[] { ARMSITEM_PTR, IMG_EXT_PTR, NPC_TBL_PTR, MYCHAR_PTR,
			TITLE_PTR, ARMSIMAGE_PTR, ARMS_PTR, ITEMIMAGE_PTR, DATA_FOLDER_PTR, STAGEIMAGE_PTR, NPCSYM_PTR, NPCREGU_PTR,
			TEXTBOX_PTR, CARET_PTR, BULLET_PTR, FACE_PTR, FADE_PTR, LOADING_PTR, PXM_TAG_PTR, PROFILE_HEADER_PTR,
			PROFILE_FLAGH_PTR, STAGESELECT_PTR, STAGE_FOLDER_PTR, PRT_PREFIX_PTR, PXA_EXT_PTR, PXM_EXT_PTR, PXE_EXT_PTR,
			TSC_EXT_PTR, NPC_FOLDER_PTR, NPC_PREFIX_PTR };

	/**
	 * Name for "ArmsItem.tsc".
	 */
	public static final int STRING_ARMSITEM;
	/**
	 * Image file extension.
	 */
	public static final int STRING_IMG_EXT;
	/**
	 * Name for the "npc.tbl" file.
	 */
	public static final int STRING_NPC_TBL;
	/**
	 * Name for the "MyChar" graphics file.
	 */
	public static final int STRING_MYCHAR;
	/**
	 * Name for the "Title" graphics file.
	 */
	public static final int STRING_TITLE;
	/**
	 * File name for the "ArmsImage" graphics file.
	 */
	public static final int STRING_ARMSIMAGE;
	/**
	 * File name for the "Arms" graphics file.
	 */
	public static final int STRING_ARMS;
	/**
	 * Name for the "ItemImage" graphics file.
	 */
	public static final int STRING_ITEMIMAGE;
	/**
	 * Name for the "data" folder.
	 */
	public static final int STRING_DATA_FOLDER;
	/**
	 * Name for the "StageImage" graphics file.
	 */
	public static final int STRING_STAGEIMAGE;
	/**
	 * Name for the "NpcSym" graphics file.
	 */
	public static final int STRING_NPCSYM;
	/**
	 * Name for the "NpcRegu" graphics file.
	 */
	public static final int STRING_NPCREGU;
	/**
	 * Name for the "TextBox" graphics file.
	 */
	public static final int STRING_TEXTBOX;
	/**
	 * Name for the "Caret" graphics file.
	 */
	public static final int STRING_CARET;
	/**
	 * Name for the "Bullet" graphics file.
	 */
	public static final int STRING_BULLET;
	/**
	 * Name for the "Face" graphics file.
	 */
	public static final int STRING_FACE;
	/**
	 * Name for the "Fade" graphics file.
	 */
	public static final int STRING_FADE;
	/**
	 * Name for the "Loading" graphics file.
	 */
	public static final int STRING_LOADING;
	/**
	 * The PXM file tag.
	 */
	public static final int STRING_PXM_TAG;
	/**
	 * The profile header.
	 */
	public static final int STRING_PROFILE_HEADER;
	/**
	 * The profile flag section header.
	 */
	public static final int STRING_PROFILE_FLAGH;
	/**
	 * Name for the "StageSelect.tsc" file.
	 */
	public static final int STRING_STAGESELECT;
	/**
	 * Name for the "Stage" folder.
	 */
	public static final int STRING_STAGE_FOLDER;
	/**
	 * Prefix for tileset graphics files.
	 */
	public static final int STRING_PRT_PREFIX;
	/**
	 * PXA (tile attributes) file extension.
	 */
	public static final int STRING_PXA_EXT;
	/**
	 * PXM (map) file extension.
	 */
	public static final int STRING_PXM_EXT;
	/**
	 * PXE (entities) file extension.
	 */
	public static final int STRING_PXE_EXT;
	/**
	 * TSC file extension.
	 */
	public static final int STRING_TSC_EXT;
	/**
	 * Name for the "Npc" folder.
	 */
	public static final int STRING_NPC_FOLDER;
	/**
	 * Prefix for NPC sheet graphics files.
	 */
	public static final int STRING_NPC_PREFIX;

	// Set string indexes
	static {
		Arrays.sort(STRING_POINTERS);
		STRING_ARMSITEM = Arrays.binarySearch(STRING_POINTERS, ARMSITEM_PTR);
		STRING_IMG_EXT = Arrays.binarySearch(STRING_POINTERS, IMG_EXT_PTR);
		STRING_NPC_TBL = Arrays.binarySearch(STRING_POINTERS, NPC_TBL_PTR);
		STRING_MYCHAR = Arrays.binarySearch(STRING_POINTERS, MYCHAR_PTR);
		STRING_TITLE = Arrays.binarySearch(STRING_POINTERS, TITLE_PTR);
		STRING_ARMSIMAGE = Arrays.binarySearch(STRING_POINTERS, ARMSIMAGE_PTR);
		STRING_ARMS = Arrays.binarySearch(STRING_POINTERS, ARMS_PTR);
		STRING_ITEMIMAGE = Arrays.binarySearch(STRING_POINTERS, ITEMIMAGE_PTR);
		STRING_DATA_FOLDER = Arrays.binarySearch(STRING_POINTERS, DATA_FOLDER_PTR);
		STRING_STAGEIMAGE = Arrays.binarySearch(STRING_POINTERS, STAGEIMAGE_PTR);
		STRING_NPCSYM = Arrays.binarySearch(STRING_POINTERS, NPCSYM_PTR);
		STRING_NPCREGU = Arrays.binarySearch(STRING_POINTERS, NPCREGU_PTR);
		STRING_TEXTBOX = Arrays.binarySearch(STRING_POINTERS, TEXTBOX_PTR);
		STRING_CARET = Arrays.binarySearch(STRING_POINTERS, CARET_PTR);
		STRING_BULLET = Arrays.binarySearch(STRING_POINTERS, BULLET_PTR);
		STRING_FACE = Arrays.binarySearch(STRING_POINTERS, FACE_PTR);
		STRING_FADE = Arrays.binarySearch(STRING_POINTERS, FADE_PTR);
		STRING_LOADING = Arrays.binarySearch(STRING_POINTERS, LOADING_PTR);
		STRING_PXM_TAG = Arrays.binarySearch(STRING_POINTERS, PXM_TAG_PTR);
		STRING_PROFILE_HEADER = Arrays.binarySearch(STRING_POINTERS, PROFILE_HEADER_PTR);
		STRING_PROFILE_FLAGH = Arrays.binarySearch(STRING_POINTERS, PROFILE_FLAGH_PTR);
		STRING_STAGESELECT = Arrays.binarySearch(STRING_POINTERS, STAGESELECT_PTR);
		STRING_STAGE_FOLDER = Arrays.binarySearch(STRING_POINTERS, STAGE_FOLDER_PTR);
		STRING_PRT_PREFIX = Arrays.binarySearch(STRING_POINTERS, PRT_PREFIX_PTR);
		STRING_PXA_EXT = Arrays.binarySearch(STRING_POINTERS, PXA_EXT_PTR);
		STRING_PXM_EXT = Arrays.binarySearch(STRING_POINTERS, PXM_EXT_PTR);
		STRING_PXE_EXT = Arrays.binarySearch(STRING_POINTERS, PXE_EXT_PTR);
		STRING_TSC_EXT = Arrays.binarySearch(STRING_POINTERS, TSC_EXT_PTR);
		STRING_NPC_FOLDER = Arrays.binarySearch(STRING_POINTERS, NPC_FOLDER_PTR);
		STRING_NPC_PREFIX = Arrays.binarySearch(STRING_POINTERS, NPC_PREFIX_PTR);
	}

	/**
	 * Enables CS+ compatibility, allowing stage.tbl files to be loaded as mods.
	 * Disables fancy EXE loading, however.
	 */
	private static boolean plusMode = false;

	public static boolean isPlusMode() {
		return plusMode;
	}

	public static void setPlusMode(boolean plusMode) {
		ExeData.plusMode = plusMode;
	}

	/**
	 * If <code>true</code>, NPC files (spritesheets and PXEs) will be loaded,
	 * otherwise they will be ignored.
	 */
	private static boolean loadNpc = true;

	public static boolean doLoadNpc() {
		return loadNpc;
	}

	public static void setLoadNpc(boolean loadNpc) {
		ExeData.loadNpc = loadNpc;
	}

	/**
	 * Array of strings loaded from the executable.
	 * 
	 * @see #STRING_POINTERS
	 */
	private static String[] exeStrings;
	/**
	 * Loaded flag. If <code>true</code>, an executable has been loaded.
	 */
	private static boolean loaded = false;
	/**
	 * The executable file.
	 */
	private static File base;
	/**
	 * The "data" directory.
	 */
	private static File dataDir;
	/**
	 * List of npc.tbl entries.
	 * 
	 * @see EntityData
	 */
	private static Vector<EntityData> entityList;
	/**
	 * List of map data.
	 * 
	 * @see Mapdata
	 */
	private static Vector<Mapdata> mapdata;
	/**
	 * List of map information.
	 * 
	 * @see MapInfo
	 */
	private static Vector<MapInfo> mapInfo;
	/**
	 * Image repository.
	 */
	private static Map<File, BufferedImage> imageMap;
	/**
	 * PXA file repository.
	 */
	private static Map<File, byte[]> pxaMap;
	/**
	 * "Title" graphics file.
	 */
	private static File title;
	/**
	 * "MyChar" graphics file.
	 */
	private static File myChar;
	/**
	 * "ArmsImage" graphics file.
	 */
	private static File armsImage;
	/**
	 * "Arms" graphics file.
	 */
	private static File arms;
	/**
	 * "ItemImage" graphics file.
	 */
	private static File itemImage;
	/**
	 * "StageImage" graphics file.
	 */
	private static File stageImage;
	/**
	 * "NpcSym" graphics file.
	 */
	private static File npcSym;
	/**
	 * "NpcRegu" graphics file.
	 */
	private static File npcRegu;
	/**
	 * "TextBox" graphics file.
	 */
	private static File textBox;
	/**
	 * "Caret" graphics file.
	 */
	private static File caret;
	/**
	 * "Bullet" graphics file.
	 */
	private static File bullet;
	/**
	 * "Face" graphics file.
	 */
	private static File face;
	/**
	 * "Fade" graphics file.
	 */
	private static File fade;
	/**
	 * "Loading" graphics file.
	 */
	private static File loading;

	/**
	 * Loads an executable.
	 * 
	 * @param file
	 *            executable file
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void load(File file) throws IOException {
		File base = file;
		if (base == null)
			base = new File(Profile.getFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
		if (!base.exists())
			return;
		load0(base);
		Profile.header = getExeString(STRING_PROFILE_HEADER);
		Profile.flagH = getExeString(STRING_PROFILE_FLAGH);
	}

	/**
	 * Reloads the current executable.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void reload() throws IOException {
		if (!loaded || base == null)
			return;
		load0(base);
	}

	/**
	 * Loads the executable.
	 * 
	 * @param base
	 *            executable file
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void load0(File base) throws IOException {
		ExeData.base = base;
		if (base.getName().endsWith(".tbl")) {
			// assume stage.tbl
			plusMode = true;
			loadPlus();
			return;
		} else
			plusMode = false;
		try {
			loadExeStrings();
			dataDir = new File(base.getParent() + getExeString(STRING_DATA_FOLDER));
			entityList = new Vector<EntityData>();
			mapdata = new Vector<Mapdata>();
			mapInfo = new Vector<MapInfo>();
			imageMap = new HashMap<File, BufferedImage>();
			pxaMap = new HashMap<File, byte[]>();
			loadNpcTbl();
			fillMapdata();
			loadMapInfo();
			loadGraphics();
		} catch (IOException e) {
			loaded = false;
			throw e;
		}
		loaded = true;
	}

	/**
	 * Loads mapdata from a stage.tbl file. Maybe.
	 * 
	 * @throws IOException
	 *             probably all the time because this code is designed for
	 *             executables.
	 */
	private static void loadPlus() throws IOException {
		dataDir = new File(base.getParent() + getExeString(STRING_DATA_FOLDER));
		entityList = new Vector<EntityData>();
		mapdata = new Vector<Mapdata>();
		mapInfo = new Vector<MapInfo>();
		imageMap = new HashMap<File, BufferedImage>();
		pxaMap = new HashMap<File, byte[]>();
		loadNpcTbl();
		fillMapdata();
		loadMapInfo();
		loadGraphics();
	}

	/**
	 * Unloads the executable.
	 */
	public static void unload() {
		exeStrings = null;
		loaded = false;
		base = null;
		dataDir = null;
		entityList = null;
		mapdata = null;
		mapInfo = null;
		imageMap = null;
		pxaMap = null;
		myChar = null;
		armsImage = null;
		itemImage = null;
		stageImage = null;
		npcRegu = null;
		npcSym = null;
		Profile.header = Profile.DEFAULT_HEADER;
		Profile.flagH = Profile.DEFAULT_FLAGH;
		System.gc();
	}

	/**
	 * Loads strings from the executable.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadExeStrings() throws IOException {
		String name = ExeData.base.getName();
		String ext = name.substring(name.length() - 3, name.length());
		if (!"exe".equals(ext))
			throw new IOException("Base file is not an executable!");
		exeStrings = new String[STRING_POINTERS.length];
		byte[] buffer = new byte[0x10];
		FileChannel inChan;
		FileInputStream inStream;
		inStream = new FileInputStream(ExeData.base);
		inChan = inStream.getChannel();
		ByteBuffer uBuf = ByteBuffer.allocate(0x10);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < STRING_POINTERS.length; i++) {
			inChan.position(STRING_POINTERS[i]);
			inChan.read(uBuf);
			uBuf.flip();
			uBuf.get(buffer);
			String str = StrTools.CString(buffer, Main.encoding);
			// Backslashes are Windows-only, so replace them with forward slashes
			str = str.replaceAll("\\\\", "/");
			exeStrings[i] = str;
			uBuf.clear();
		}
		inStream.close();
	}

	/**
	 * Loads the "npc.tbl" file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadNpcTbl() throws IOException {
		File tblFile = ResUtils.newFile(dataDir + "/" + getExeString(STRING_NPC_TBL));
		FileChannel inChan;
		ByteBuffer dBuf;
		FileInputStream inStream;
		int calculated_npcs;

		if (tblFile == null || !tblFile.exists())
			throw new IOException("Could not find \"" + tblFile + "\"!");

		try {
			inStream = new FileInputStream(tblFile);
			calculated_npcs = (int) (tblFile.length() / 24);
			inChan = inStream.getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		short[] flagDat;
		short[] healthDat;
		byte[] tilesetDat;
		byte[] deathDat;
		byte[] hurtDat;
		byte[] sizeDat;
		int[] expDat;
		int[] damageDat;
		byte[] hitboxDat;
		byte[] displayDat;
		// read flags section
		dBuf = ByteBuffer.allocateDirect(2 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		flagDat = new short[calculated_npcs];
		for (int i = 0; i < flagDat.length; i++) {
			flagDat[i] = dBuf.getShort();
		}

		// read health section
		dBuf = ByteBuffer.allocate(2 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		healthDat = new short[calculated_npcs];
		for (int i = 0; i < healthDat.length; i++) {
			healthDat[i] = dBuf.getShort();
		}

		// read tileset section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		tilesetDat = dBuf.array();

		// read death sound section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		deathDat = dBuf.array();

		// read hurt sound section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		hurtDat = dBuf.array();

		// read size section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		sizeDat = dBuf.array();

		// read experience section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		expDat = new int[calculated_npcs];
		for (int i = 0; i < expDat.length; i++) {
			expDat[i] = dBuf.getInt();
		}

		// read damage section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		damageDat = new int[calculated_npcs];
		for (int i = 0; i < damageDat.length; i++) {
			damageDat[i] = dBuf.getInt();
		}

		// read hitbox section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		hitboxDat = dBuf.array();

		// read display box section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		displayDat = dBuf.array();
		// finished reading file
		inChan.close();
		inStream.close();

		// build the master list
		for (int i = 0; i < calculated_npcs; i++) {
			EntityData e = new EntityData(i, damageDat[i], deathDat[i], expDat[i], flagDat[i], healthDat[i], hurtDat[i],
					sizeDat[i], tilesetDat[i],
					new Rectangle(displayDat[i * 4], displayDat[i * 4 + 1], displayDat[i * 4 + 2],
							displayDat[i * 4 + 3]),
					new Rectangle(hitboxDat[i * 4], hitboxDat[i * 4 + 1], hitboxDat[i * 4 + 2], hitboxDat[i * 4 + 3]));
			entityList.add(i, e);
		}
	}

	/**
	 * Loads map data.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void fillMapdata() throws IOException {
		FileChannel inChan;

		FileInputStream inStream;
		inStream = new FileInputStream(base);
		inChan = inStream.getChannel();

		// the hard part
		ByteBuffer uBuf = ByteBuffer.allocate(2);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		// find how many sections
		inChan.position(0x116);
		inChan.read(uBuf);
		uBuf.flip();
		int numSection = uBuf.getShort();
		// read each segment
		// find the .csmap or .swdata segment
		int mapSec = -1;
		String[] secHeaders = new String[numSection];
		for (int i = 0; i < numSection; i++) {
			uBuf = ByteBuffer.allocate(8);
			inChan.position(0x208 + 0x28 * i);
			inChan.read(uBuf);
			uBuf.flip();
			String segStr = new String(uBuf.array());

			if (segStr.contains(".csmap"))
				mapSec = i;
			else if (segStr.contains(".swdata"))
				mapSec = i;
			secHeaders[i] = segStr;
		}

		String encoding = Main.encoding;
		if (mapSec == -1) // virgin executable
		{
			int numMaps = 95;
			inChan.position(0x937B0); // seek to start of mapdatas
			for (int i = 0; i < numMaps; i++) {
				Mapdata newMap = new Mapdata(i);
				// for each map
				uBuf = ByteBuffer.allocate(200);
				uBuf.order(ByteOrder.LITTLE_ENDIAN);
				inChan.read(uBuf);
				uBuf.flip();
				byte[] buffer = new byte[0x23];
				uBuf.get(buffer, 0, 0x20);
				newMap.setTileset(StrTools.CString(buffer, encoding));
				uBuf.get(buffer, 0, 0x20);
				String fileName = StrTools.CString(buffer, encoding);
				newMap.setFileName(fileName);
				newMap.setScrollType(uBuf.getInt() & 0xFF);
				uBuf.get(buffer, 0, 0x20);
				newMap.setBgName(StrTools.CString(buffer, encoding));
				uBuf.get(buffer, 0, 0x20);
				newMap.setNpcSheet1(StrTools.CString(buffer, encoding));
				uBuf.get(buffer, 0, 0x20);
				newMap.setNpcSheet2(StrTools.CString(buffer, encoding));
				// newMap.setBossNum(uBuf.get());
				uBuf.get();
				uBuf.get(buffer, 0, 0x23);
				newMap.setMapName(StrTools.CString(buffer, encoding));
				mapdata.add(newMap);
			} // for each map
		} else { // exe has been edited probably
			if (secHeaders[mapSec].contains(".csmap")) {
				// cave editor/booster's lab
				uBuf = ByteBuffer.allocate(4);
				uBuf.order(ByteOrder.LITTLE_ENDIAN);
				inChan.position(0x208 + 0x28 * mapSec + 0x10); // read the PE header
				inChan.read(uBuf);
				uBuf.flip();
				int numMaps = uBuf.getInt() / 200;
				uBuf.flip();
				inChan.read(uBuf);
				uBuf.flip();
				int pData = uBuf.getInt();

				inChan.position(pData);// seek to start of CS map data
				for (int i = 0; i < numMaps; i++) {
					// for each map
					Mapdata newMap = new Mapdata(i);
					uBuf = ByteBuffer.allocate(200);
					uBuf.order(ByteOrder.LITTLE_ENDIAN);
					inChan.read(uBuf);
					uBuf.flip();
					byte[] buffer = new byte[0x23];
					uBuf.get(buffer, 0, 0x20);
					newMap.setTileset(StrTools.CString(buffer, encoding));
					uBuf.get(buffer, 0, 0x20);
					newMap.setFileName(StrTools.CString(buffer, encoding));
					int argh = uBuf.getInt();
					newMap.setScrollType(argh & 0xFF);
					uBuf.get(buffer, 0, 0x20);
					newMap.setBgName(StrTools.CString(buffer, encoding));
					uBuf.get(buffer, 0, 0x20);
					newMap.setNpcSheet1(StrTools.CString(buffer, encoding));
					uBuf.get(buffer, 0, 0x20);
					newMap.setNpcSheet2(StrTools.CString(buffer, encoding));
					// newMap.setBossNum(uBuf.get());
					uBuf.get();
					uBuf.get(buffer, 0, 0x23);
					newMap.setMapName(StrTools.CString(buffer, encoding));
					mapdata.add(newMap);
				} // for each map
			} else {
				// sue's workshop
				uBuf = ByteBuffer.allocate(4);
				uBuf.order(ByteOrder.LITTLE_ENDIAN);
				inChan.position(0x208 + 0x28 * mapSec + 0x10);
				inChan.read(uBuf);
				uBuf.flip();
				@SuppressWarnings("unused")
				int numMaps = uBuf.getInt() / 200;
				uBuf.flip();
				inChan.read(uBuf);
				uBuf.flip();
				int pData = uBuf.getInt();
				inChan.position(pData + 0x10);// seek to start of Sue's map data
				int nMaps = 0;
				while (true) {
					// for each map
					uBuf = ByteBuffer.allocate(200);
					inChan.read(uBuf);
					uBuf.flip();
					// check if it's the FFFFFFFFFFFFFFinal map
					if (uBuf.getInt(0) == -1)
						break;
					Mapdata newMap = new Mapdata(nMaps);
					byte[] buffer = new byte[0x23];
					uBuf.get(buffer, 0, 0x20);
					newMap.setTileset(StrTools.CString(buffer));
					uBuf.get(buffer, 0, 0x20);
					newMap.setFileName(StrTools.CString(buffer));
					newMap.setScrollType(uBuf.getInt() & 0xFF);
					uBuf.get(buffer, 0, 0x20);
					newMap.setBgName(StrTools.CString(buffer));
					uBuf.get(buffer, 0, 0x20);
					newMap.setNpcSheet1(StrTools.CString(buffer));
					uBuf.get(buffer, 0, 0x20);
					newMap.setNpcSheet2(StrTools.CString(buffer));
					// newMap.setBossNum(uBuf.get());
					uBuf.get();
					uBuf.get(buffer, 0, 0x23);
					newMap.setMapName(StrTools.CString(buffer));
					mapdata.add(newMap);
					nMaps++;
				} // for each map
			}
		}

		inStream.close();
	}

	/**
	 * Loads map info.
	 */
	private static void loadMapInfo() {
		for (int i = 0; i < mapdata.size(); i++)
			mapInfo.add(new MapInfo(mapdata.get(i)));
	}

	/**
	 * Loads an image.
	 * 
	 * @param srcFile
	 *            source file
	 * @param trans
	 *            <code>true</code> if black pixels should be transparent,
	 *            <code>false</code> otherwise
	 * @return filtered image
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static BufferedImage loadImage(File srcFile, boolean trans) throws IOException {
		if (srcFile == null)
			return null;
		try (FileInputStream is = new FileInputStream(srcFile)) {
			BufferedImage img = ImageIO.read(is);
			if (trans)
				img = ResUtils.black2Trans(img);
			int res = MCI.getInteger("Game.GraphicsResolution", 1);
			if (res == 2)
				return img;
			double scale = 2 / (double) res;
			if (scale == 1)
				return img;
			int w = img.getWidth(), h = img.getHeight();
			BufferedImage after = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
			AffineTransform at = new AffineTransform();
			at.scale(scale, scale);
			AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			after = scaleOp.filter(img, after);
			return after;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Loads an image.
	 * 
	 * @param srcFile
	 *            source image
	 * @return filtered image
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static BufferedImage loadImage(File srcFile) throws IOException {
		return loadImage(srcFile, true);
	}

	/**
	 * Loads a graphics file.
	 * 
	 * @param strid
	 *            executable string id to get file name from
	 * @return file that was loaded
	 */
	private static File loadGraphic(int strid) {
		File ret = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[strid]);
		addImage(ret);
		return ret;
	}

	/**
	 * Loads graphics files.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadGraphics() throws IOException {
		title = loadGraphic(STRING_TITLE);
		myChar = loadGraphic(STRING_MYCHAR);
		armsImage = loadGraphic(STRING_ARMSIMAGE);
		arms = loadGraphic(STRING_ARMS);
		itemImage = loadGraphic(STRING_ITEMIMAGE);
		stageImage = loadGraphic(STRING_STAGEIMAGE);
		npcSym = loadGraphic(STRING_NPCSYM);
		npcRegu = loadGraphic(STRING_NPCREGU);
		textBox = loadGraphic(STRING_TEXTBOX);
		caret = loadGraphic(STRING_CARET);
		bullet = loadGraphic(STRING_BULLET);
		face = loadGraphic(STRING_FACE);
		fade = loadGraphic(STRING_FADE);
		loading = loadGraphic(STRING_LOADING);
	}

	/**
	 * Attempts to add an image to the repository.
	 * 
	 * @param srcFile
	 *            image to load
	 */
	public static void addImage(String srcFile) {
		addImage(new File(srcFile));
	}

	/**
	 * Attempts to add an image to the repository.
	 * 
	 * @param srcFile
	 *            image to load
	 */
	public static void addImage(File srcFile) {
		if (srcFile == null)
			return;
		srcFile = ResUtils.newFile(srcFile.getAbsolutePath());
		try {
			if (imageMap.containsKey(srcFile))
				return;
			imageMap.put(srcFile, loadImage(srcFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If the file exists in the repository, replace. If not, load it anyway.
	 * 
	 * @param srcFile
	 *            image to load
	 */
	public static void reloadImage(String src) {
		reloadImage(new File(src));
	}

	/**
	 * If the file exists in the repository, replace. If not, load it anyway.
	 * 
	 * @param srcFile
	 *            image to load
	 */
	public static void reloadImage(File srcFile) {
		if (srcFile == null)
			return;
		srcFile = ResUtils.newFile(srcFile.getAbsolutePath());
		if (imageMap.containsKey(srcFile)) {
			imageMap.get(srcFile).flush();
			imageMap.remove(srcFile);
		}
		addImage(srcFile);
	}

	/**
	 * Gets an image's graphics instance.
	 * 
	 * @param key
	 *            image file
	 * @return image graphics
	 */
	public static java.awt.Graphics getImageGraphics(File key) {
		if (key == null)
			return null;
		if (imageMap.containsKey(key))
			return imageMap.get(key).getGraphics();
		System.err.println("Key not found for getImageGraphics");
		System.err.println(key);
		return null;
	}

	/**
	 * Gets an image's graphics instance.
	 * 
	 * @param key
	 *            image file
	 * @return image graphics
	 */
	public static java.awt.Graphics getImageGraphics(String key) {
		return getImageGraphics(new File(key));
	}

	/**
	 * Gets an image.
	 * 
	 * @param key
	 *            image file
	 * @return image
	 */
	public static BufferedImage getImage(File key) {
		if (key == null)
			return null;
		key = ResUtils.newFile(key.getAbsolutePath());
		if (imageMap.containsKey(key))
			return imageMap.get(key);
		System.err.println("Key not found for getImage");
		System.err.println(key);
		return null;
	}

	/**
	 * Gets an image.
	 * 
	 * @param key
	 *            image file
	 * @return image
	 */
	public static BufferedImage getImage(String key) {
		return getImage(new File(key));
	}

	/**
	 * Gets an image's height.
	 * 
	 * @param key
	 *            image file
	 * @return image height
	 */
	public static int getImageHeight(File key) {
		if (key == null)
			return -1;
		key = ResUtils.newFile(key.getAbsolutePath());
		if (imageMap.containsKey(key))
			return imageMap.get(key).getHeight();
		System.err.println("Key not found for getImageHeight");
		System.err.println(key);
		return -1;
	}

	/**
	 * Gets an image's height.
	 * 
	 * @param key
	 *            image file
	 * @return image height
	 */
	public static int getImageHeight(String key) {
		return getImageHeight(new File(key));
	}

	/**
	 * Gets an image's width.
	 * 
	 * @param key
	 *            image file
	 * @return image width
	 */
	public static int getImageWidth(File key) {
		if (key == null)
			return -1;
		key = ResUtils.newFile(key.getAbsolutePath());
		if (imageMap.containsKey(key))
			return imageMap.get(key).getWidth();
		System.err.println("Key not found for getImageWidth");
		System.err.println(key);
		return -1;
	}

	/**
	 * Gets an image's width.
	 * 
	 * @param key
	 *            image file
	 * @return image width
	 */
	public static int getImageWidth(String key) {
		return getImageWidth(new File(key));
	}

	/**
	 * Attempts to add a PXA file to the repository.
	 * 
	 * @param srcFile
	 *            source file
	 * @return PXA data
	 */
	public static byte[] addPxa(File srcFile) {
		srcFile = ResUtils.newFile(srcFile.getAbsolutePath());
		FileChannel inChan = null;
		if (pxaMap.containsKey(srcFile))
			return pxaMap.get(srcFile);
		byte[] pxaArray = null;
		boolean succ = false;
		try {
			FileInputStream inStream = new FileInputStream(srcFile);
			inChan = inStream.getChannel();
			ByteBuffer pxaBuf = ByteBuffer.allocate(256);// this is the max size. Indeed, the only size..
			inChan.read(pxaBuf);
			inChan.close();
			inStream.close();
			pxaBuf.flip();
			pxaArray = pxaBuf.array();
			pxaMap.put(srcFile, pxaArray);
			succ = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inChan != null)
				try {
					inChan.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (!succ && srcFile != null) {
				byte[] dummyArray = new byte[256];
				pxaMap.put(srcFile, dummyArray);
			}
		}
		return pxaArray;
	}

	/**
	 * Attempts to add a PXA file to the repository.
	 * 
	 * @param srcFile
	 *            source file
	 * @return PXA data
	 */
	public static byte[] addPxa(String srcFile) {
		return addPxa(new File(srcFile));
	}

	/**
	 * Gets PXA data from the repository.
	 * 
	 * @param srcFile
	 *            source file
	 * @return PXA data
	 */
	public static byte[] getPxa(File srcFile) {
		srcFile = ResUtils.newFile(srcFile.getAbsolutePath());
		return pxaMap.get(srcFile);
	}

	/**
	 * Gets PXA data from the repository.
	 * 
	 * @param srcFile
	 *            source file
	 * @return PXA data
	 */
	public static byte[] getPxa(String srcFile) {
		return getPxa(new File(srcFile));
	}

	/**
	 * Gets a string loaded from the executable.
	 * 
	 * @param id
	 *            index
	 * @return string from executable
	 */
	public static String getExeString(int id) {
		return exeStrings[id];
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static File getBase() {
		return base;
	}

	public static File getDataDir() {
		return dataDir;
	}

	/**
	 * Gets a {@link MapInfo} instance for a map.
	 * 
	 * @param num
	 *            map ID
	 * @return map information
	 */
	public static MapInfo getMapInfo(int num) {
		if (num < 0)
			throw new IndexOutOfBoundsException("Requested map number (" + num + ") is negative!");
		if (mapInfo.size() < num)
			throw new IndexOutOfBoundsException(
					"Requested map number is " + num + ", but maximum map number is " + mapdata.size() + "!");
		return mapInfo.get(num);
	}

	/**
	 * Gets the amount of loaded maps.
	 * 
	 * @return amount of maps
	 */
	public static int getMapInfoCount() {
		return mapInfo.size();
	}

	/**
	 * Gets a npc.tbl entry for an entity type.
	 * 
	 * @param entityType
	 *            entity type
	 * @return npc.tbl entry
	 */
	public static EntityData getEntityInfo(short entityType) {
		return entityList.get(entityType);
	}

	public static File getTitle() {
		return title;
	}

	public static File getMyChar() {
		return myChar;
	}

	public static File getArmsImage() {
		return armsImage;
	}

	public static File getArms() {
		return arms;
	}

	public static File getItemImage() {
		return itemImage;
	}

	public static File getStageImage() {
		return stageImage;
	}

	public static File getNpcSym() {
		return npcSym;
	}

	public static File getNpcRegu() {
		return npcRegu;
	}

	public static File getTextBox() {
		return textBox;
	}

	public static File getCaret() {
		return caret;
	}

	public static File getBullet() {
		return bullet;
	}

	public static File getFace() {
		return face;
	}

	public static File getFade() {
		return fade;
	}

	public static File getLoading() {
		return loading;
	}

}

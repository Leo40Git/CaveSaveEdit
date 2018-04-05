package com.leo.cse.backend.exe;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.leo.cse.backend.ResUtils;
import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.ProfileManager;
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
		throw new AssertionError("No " + getClass().getName() + " object for you!");
	}

	/**
	 * A list of {@link ExeLoadListener}s.
	 */
	private static List<ExeLoadListener> listeners;

	/**
	 * Attaches a load listener.
	 * 
	 * @param l
	 *            listener
	 */
	public static void addListener(ExeLoadListener l) {
		if (listeners == null)
			listeners = new LinkedList<>();
		listeners.add(l);
	}

	public static void removeListener(ExeLoadListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	public static final String EVENT_PRELOAD = "load.pre";
	public static final String EVENT_LOAD = "load.during";
	public static final String EVENT_POSTLOAD = "load.post";
	public static final String LOADNAME_POSTLOAD_SUCCESS = "success";
	public static final String LOADNAME_POSTLOAD_FAILURE = "failure";
	public static final String EVENT_UNLOAD = "load.un";

	public static final String EVENT_EXE_STRING = "load.exestr";
	public static final String EVENT_NPC_TBL = "load.npctbl";
	public static final String LOADNAME_NPC_TBL_FLAGS = "entity flags";
	public static final String LOADNAME_NPC_TBL_HEALTH = "amount of health health";
	public static final String LOADNAME_NPC_TBL_TILESET = "tileset to use";
	public static final String LOADNAME_NPC_TBL_DEATHSND = "death sound";
	public static final String LOADNAME_NPC_TBL_HURTSND = "hurt sound";
	public static final String LOADNAME_NPC_TBL_SIZE = "smoke & dropped health/missile size";
	public static final String LOADNAME_NPC_TBL_EXP = "experience dropped on defeat";
	public static final String LOADNAME_NPC_TBL_DAMAGE = "amount of damage";
	public static final String LOADNAME_NPC_TBL_HITBOX = "hitbox";
	public static final String LOADNAME_NPC_TBL_DISPLAYBOX = "display box";
	public static final String EVENT_MAP_DATA = "load.map.data";
	public static final String EVENT_GRAPHICS = "load.gfx";
	public static final String EVENT_GRAPHICS_RSRC = "load.gfx.rsrc";
	public static final String EVENT_MAP_INFO = "load.map.info";
	public static final String LOADNAME_MAP_INFO_PXA = "tileset definition";
	public static final String LOADNAME_MAP_INFO_IMAGES = "images";
	public static final String LOADNAME_MAP_INFO_PXM = "layout file";
	public static final String LOADNAME_MAP_INFO_PXE = "entities file";
	public static final String LOADNAME_MAP_INFO_TSC = "script file";

	public static final String SUBEVENT_IMAGE = "sub.img";
	public static final String SUBEVENT_PXA = "sub.pxa";
	public static final String SUBEVENT_END = "sub.end";

	/**
	 * Notifies all listeners of an event.
	 * 
	 * @param notifyType
	 *            event type
	 * @see #NOTIFY_PRELOAD
	 * @see #NOTIFY_LOAD
	 * @see #NOTIFY_POSTLOAD
	 * @see #NOTIFY_UNLOAD
	 */
	private static void notifyListeners(boolean sub, String event, String loadName, int loadId, int loadIdMax) {
		if (listeners == null)
			return;
		for (ExeLoadListener l : listeners)
			if (sub)
				l.onSubevent(event, loadName, loadId, loadIdMax);
			else
				l.onEvent(event, loadName, loadId, loadIdMax);
	}

	/**
	 * The encoding to use to read strings from the executable.
	 */
	private static String encoding = StrTools.DEFAULT_ENCODING;

	/**
	 * Gets the encoding to use to read strings.
	 * 
	 * @return encoding
	 */
	public static String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the encoding to use to read strings.
	 * 
	 * @param encoding
	 *            new encoding
	 */
	public static void setEncoding(String encoding) {
		ExeData.encoding = encoding;
	}

	/**
	 * Base pointer for the ".rdata" segment in the executable. Used to read
	 * {@linkplain #exeStrings the executable strings}.
	 */
	private static int rdataPtr;

	// -------------
	// Data Pointers
	// -------------

	// --!-- RELATIVE TO rdataPtr! --!--
	/**
	 * Pointer to file name for "ArmsItem.tsc".
	 */
	private static final int ARMSITEM_PTR = 0x270;
	/**
	 * Pointer to image file extension.
	 */
	private static final int IMG_EXT_PTR = 0x280;
	/**
	 * Pointer to name for the "Credit.tsc" file.
	 */
	private static final int CREDIT_PTR = 0x368;
	/**
	 * Pointer to name for the "npc.tbl" file.
	 */
	private static final int NPC_TBL_PTR = 0x3AB;
	/**
	 * Pointer to name for the "PIXEL" resource file.
	 */
	private static final int PIXEL_PTR = 0x4E8;
	/**
	 * Pointer to name for the "MyChar" graphics file.
	 */
	private static final int MYCHAR_PTR = 0x4F0;
	/**
	 * Pointer to name for the "Title" graphics file.
	 */
	private static final int TITLE_PTR = 0x4F8;
	/**
	 * Pointer to name for the "ArmsImage" graphics file.
	 */
	private static final int ARMSIMAGE_PTR = 0x500;
	/**
	 * Pointer to name for the "Arms" graphics file.
	 */
	private static final int ARMS_PTR = 0x50C;
	/**
	 * Pointer to name for the "ItemImage" graphics file.
	 */
	private static final int ITEMIMAGE_PTR = 0x514;
	/**
	 * Pointer to name for the "data" folder.
	 */
	private static final int DATA_FOLDER_PTR = 0x5BC;
	/**
	 * Pointer to name for the "StageImage" graphics file.
	 */
	private static final int STAGEIMAGE_PTR = 0x520;
	/**
	 * Pointer to name for the "NpcSym" graphics file.
	 */
	private static final int NPCSYM_PTR = 0x52C;
	/**
	 * Pointer to name for the "NpcRegu" graphics file.
	 */
	private static final int NPCREGU_PTR = 0x538;
	/**
	 * Pointer to name for the "TextBox" graphics file.
	 */
	private static final int TEXTBOX_PTR = 0x544;
	/**
	 * Pointer to name for the "Caret" graphics file.
	 */
	private static final int CARET_PTR = 0x54C;
	/**
	 * Pointer to name for the "Bullet" graphics file.
	 */
	private static final int BULLET_PTR = 0x554;
	/**
	 * Pointer to name for the "Face" graphics file.
	 */
	private static final int FACE_PTR = 0x55C;
	/**
	 * Pointer to name for the "Fade" graphics file.
	 */
	private static final int FADE_PTR = 0x564;
	/**
	 * Pointer to name for the "Loading" graphics file.
	 */
	private static final int LOADING_PTR = 0x5FC;
	/**
	 * Pointer to the PXM file tag.
	 */
	private static final int PXM_TAG_PTR = 0x67C;
	/**
	 * Pointer to the profile name.
	 */
	private static final int PROFILE_NAME_PTR = 0x700;
	/**
	 * Pointer to the profile header.
	 */
	private static final int PROFILE_HEADER_PTR = 0x70C;
	/**
	 * Pointer to the profile flag section header.
	 */
	private static final int PROFILE_FLAGH_PTR = 0x720;
	/**
	 * Pointer to name for the "StageSelect.tsc" file.
	 */
	private static final int STAGESELECT_PTR = 0x770;
	/**
	 * Pointer to name for the "Stage" folder.
	 */
	private static final int STAGE_FOLDER_PTR = 0x7D4;
	/**
	 * Pointer to prefix for tileset graphics files.
	 */
	private static final int PRT_PREFIX_PTR = 0x7DC;
	/**
	 * Pointer to PXA (tile attributes) file extension.
	 */
	private static final int PXA_EXT_PTR = 0x7E8;
	/**
	 * Pointer to PXM (map) file extension.
	 */
	private static final int PXM_EXT_PTR = 0x7F4;
	/**
	 * Pointer to PXE (entities) file extension.
	 */
	private static final int PXE_EXT_PTR = 0x800;
	/**
	 * Pointer to TSC file extension.
	 */
	private static final int TSC_EXT_PTR = 0x80C;
	/**
	 * Pointer to name for the "Npc" folder.
	 */
	private static final int NPC_FOLDER_PTR = 0x81C;
	/**
	 * Pointer to prefix for NPC sheet graphics files.
	 */
	private static final int NPC_PREFIX_PTR = 0x820;
	/**
	 * Pointer to name for the "Head.tsc" file.
	 */
	private static final int HEAD_PTR = 0x9A8;

	/**
	 * Array of pointers which point to string values.
	 */
	private static final int[] STRING_POINTERS = new int[] { ARMSITEM_PTR, IMG_EXT_PTR, CREDIT_PTR, NPC_TBL_PTR,
			PIXEL_PTR, MYCHAR_PTR, TITLE_PTR, ARMSIMAGE_PTR, ARMS_PTR, ITEMIMAGE_PTR, DATA_FOLDER_PTR, STAGEIMAGE_PTR,
			NPCSYM_PTR, NPCREGU_PTR, TEXTBOX_PTR, CARET_PTR, BULLET_PTR, FACE_PTR, FADE_PTR, LOADING_PTR, PXM_TAG_PTR,
			PROFILE_NAME_PTR, PROFILE_HEADER_PTR, PROFILE_FLAGH_PTR, STAGESELECT_PTR, STAGE_FOLDER_PTR, PRT_PREFIX_PTR,
			PXA_EXT_PTR, PXM_EXT_PTR, PXE_EXT_PTR, TSC_EXT_PTR, NPC_FOLDER_PTR, NPC_PREFIX_PTR, HEAD_PTR };

	/**
	 * Name for "ArmsItem.tsc".
	 */
	public static final int STRING_ARMSITEM;
	/**
	 * Image file extension.
	 */
	public static final int STRING_IMG_EXT;
	/**
	 * Name for "Credit.tsc".
	 */
	public static final int STRING_CREDIT;
	/**
	 * Name for the "npc.tbl" file.
	 */
	public static final int STRING_NPC_TBL;
	/**
	 * Name for the "PIXEL" resource file.
	 */
	public static final int STRING_PIXEL;
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
	 * Name for the profile.
	 */
	public static final int STRING_PROFILE_NAME;
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
	/**
	 * Name for "Head.tsc".
	 */
	public static final int STRING_HEAD;

	// Set string indexes
	static {
		Arrays.sort(STRING_POINTERS);
		STRING_ARMSITEM = Arrays.binarySearch(STRING_POINTERS, ARMSITEM_PTR);
		STRING_IMG_EXT = Arrays.binarySearch(STRING_POINTERS, IMG_EXT_PTR);
		STRING_CREDIT = Arrays.binarySearch(STRING_POINTERS, CREDIT_PTR);
		STRING_NPC_TBL = Arrays.binarySearch(STRING_POINTERS, NPC_TBL_PTR);
		STRING_PIXEL = Arrays.binarySearch(STRING_POINTERS, PIXEL_PTR);
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
		STRING_PROFILE_NAME = Arrays.binarySearch(STRING_POINTERS, PROFILE_NAME_PTR);
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
		STRING_HEAD = Arrays.binarySearch(STRING_POINTERS, HEAD_PTR);
	}

	/**
	 * CS+ flag. If <code>true</code>, the currently loaded "executable" is in fact
	 * a stage.tbl file.
	 */
	private static boolean plusMode = false;

	/**
	 * Checks if the current "executable" is a stage.tbl file.
	 * 
	 * @return <code>true</code> if in CS+ mode, <code>false</code> otherwise
	 */
	public static boolean isPlusMode() {
		return plusMode;
	}

	/**
	 * If <code>true</code>, NPC files (spritesheets and PXEs) will be loaded,
	 * otherwise they will be ignored.
	 */
	private static boolean loadNpc = true;

	/**
	 * Checks if NPC files will be loaded.
	 * 
	 * @return <code>true</code> if will be loaded, <code>false</code> otherwise.
	 */
	public static boolean doLoadNpc() {
		return loadNpc;
	}

	/**
	 * Enables or disables NPC file loading.
	 * 
	 * @param loadNpc
	 *            <code>true</code> to enable, <code>false</code> to disable.
	 */
	public static void setLoadNpc(boolean loadNpc) {
		ExeData.loadNpc = loadNpc;
	}

	/**
	 * If <code>true</code>, TSC files will be loaded,
	 * otherwise they will be ignored.
	 */
	private static boolean loadTSC = false;

	/**
	 * Checks if TSC files will be loaded.
	 * 
	 * @return <code>true</code> if will be loaded, <code>false</code> otherwise.
	 */
	public static boolean doLoadTSC() {
		return loadTSC;
	}

	/**
	 * Enables or disables TSC file loading.
	 * 
	 * @param loadNpc
	 *            <code>true</code> to enable, <code>false</code> to disable.
	 */
	public static void setLoadTSC(boolean loadTSC) {
		ExeData.loadTSC = loadTSC;
	}

	/**
	 * Graphics resolution.
	 */
	private static int graphicsResolution = 1;

	/**
	 * Gets the graphics resolution.
	 * 
	 * @return graphics resolution
	 */
	public static int getGraphicsResolution() {
		return graphicsResolution;
	}

	/**
	 * Sets the graphics resolution.
	 * 
	 * @param graphicsResolution
	 *            new graphics resolution
	 */
	public static void setGraphicsResolution(int graphicsResolution) {
		ExeData.graphicsResolution = graphicsResolution;
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
	 * Array of the loaded executable's PE headers.
	 * 
	 * @see ExeSec
	 */
	private static ExeSec[] headers;
	/**
	 * ".rsrc" segment information.
	 */
	private static ResourceInfo rsrcInfo;
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
	 * "PIXEL" resource file.
	 */
	private static File pixel;
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
			base = new File(ProfileManager.getLoadedFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName")
					+ ".exe");
		if (!base.exists())
			return;
		try {
			load0(base);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("EXE loading failed.");
			JOptionPane.showMessageDialog(Main.window, "An error occured while loading the executable:\n" + e,
					"Could not load executable!", JOptionPane.ERROR_MESSAGE);
		}
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
			loadPlus();
			return;
		}
		plusMode = false;
		try {
			notifyListeners(false, EVENT_PRELOAD, null, -1, -1);
			locateSegments();
			loadExeStrings();
			ProfileManager.setHeader(getExeString(STRING_PROFILE_HEADER));
			ProfileManager.setFlagHeader(getExeString(STRING_PROFILE_FLAGH));
			dataDir = new File(base.getParent() + getExeString(STRING_DATA_FOLDER));
			entityList = new Vector<EntityData>();
			mapdata = new Vector<Mapdata>();
			mapInfo = new Vector<MapInfo>();
			imageMap = new HashMap<File, BufferedImage>();
			pxaMap = new HashMap<File, byte[]>();
			loadNpcTbl();
			fillMapdata();
			notifyListeners(false, EVENT_LOAD, null, -1, -1);
			loadGraphics();
			loadRsrc();
			loadMapInfo();
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_SUCCESS, -1, -1);
		} catch (Exception e) {
			loaded = false;
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_FAILURE, -1, -1);
			throw e;
		}
		loaded = true;
	}

	/**
	 * Loads mapdata from a stage.tbl file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadPlus() throws IOException {
		plusMode = true;
		try {
			notifyListeners(false, EVENT_PRELOAD, null, -1, -1);
			initExeStringsPlus();
			ProfileManager.setHeader(getExeString(STRING_PROFILE_HEADER));
			ProfileManager.setFlagHeader(getExeString(STRING_PROFILE_FLAGH));
			dataDir = base.getParentFile();
			entityList = new Vector<EntityData>();
			mapdata = new Vector<Mapdata>();
			mapInfo = new Vector<MapInfo>();
			imageMap = new HashMap<File, BufferedImage>();
			pxaMap = new HashMap<File, byte[]>();
			loadNpcTbl();
			fillMapdataPlus();
			notifyListeners(false, EVENT_LOAD, null, -1, -1);
			loadMapInfo();
			loadGraphics();
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_SUCCESS, -1, -1);
		} catch (Exception e) {
			loaded = false;
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_FAILURE, -1, -1);
			throw e;
		}
		loaded = true;
	}

	public static File correctFile(File src) {
		if (!plusMode)
			return src;
		if (src.exists())
			return src;
		String name = src.getName();
		String parent = src.getParentFile().getName();
		if ("Stage".equalsIgnoreCase(parent))
			name = "Stage/" + name;
		if ("Npc".equals(parent))
			name = "Npc/" + name;
		File base = ResUtils.getBaseFolder(src);
		if (base == null)
			return null;
		return new File(base + "/" + name);
	}

	/**
	 * Unloads the currently loaded executable.
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
		System.gc();
		ProfileManager.setHeader(NormalProfile.DEFAULT_HEADER);
		ProfileManager.setFlagHeader(NormalProfile.DEFAULT_FLAGH);
		plusMode = false;
		notifyListeners(false, EVENT_UNLOAD, null, -1, -1);
	}

	/**
	 * PE segment descriptor
	 * <p>
	 * 0-7 tag<br>
	 * 8-B virtual size<br>
	 * C-F virtual address<br>
	 * 10-13 size of raw data<br>
	 * 14-17 raw data pointer<br>
	 * 18-1B relocations pointer<br>
	 * 1C-1F line numbers pointer<br>
	 * 20-21 # of relocations<br>
	 * 22-23 # of line #s<br>
	 * 24-27 characteristics<br>
	 */
	static class ExeSec {
		private String tag;
		private int vSize;
		private int vAddr;
		private int rSize;
		private int rAddr;
		private int pReloc;
		private int pLine;
		private short numReloc;
		private short numLine;
		private int attrib;

		private ByteBuffer data;

		public String getTag() {
			return tag;
		}

		public int getPos() {
			return rAddr;
		}

		public int getLen() {
			return rSize;
		}

		public int getPosV() {
			return vAddr;
		}

		public ByteBuffer getData() {
			return data;
		}

		public int getLenV() {
			return vSize;
		}

		public int getRelocP() {
			return pReloc;
		}

		public int getLineP() {
			return pLine;
		}

		public short getNumReloc() {
			return numReloc;
		}

		public short getNumLine() {
			return numLine;
		}

		public int getAttrib() {
			return attrib;
		}

		ExeSec(ByteBuffer in, FileChannel f) {
			in.position(0);
			byte[] tagArray = new byte[8];
			in.get(tagArray);
			tag = new String(tagArray);
			tag = tag.replaceAll("\0", "");
			vSize = in.getInt();
			vAddr = in.getInt();
			rSize = in.getInt();
			rAddr = in.getInt();
			pReloc = in.getInt();
			pLine = in.getInt();
			numReloc = in.getShort();
			numLine = in.getShort();
			attrib = in.getInt();

			data = ByteBuffer.allocate(rSize);
			data.order(ByteOrder.nativeOrder());
			try {
				f.position(rAddr);
				f.read(data);
				data.flip();
			} catch (IOException err) {
				err.printStackTrace();
			}
		}
	}

	/**
	 * Locate the executable segments.
	 * 
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	private static void locateSegments() throws IOException {
		// setup I/O stuff
		FileInputStream inStream;
		FileChannel inChan;
		inStream = new FileInputStream(base);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[512];
		while (inStream.available() > 0) {
			int l = inStream.read(buf);
			baos.write(buf, 0, l);
		}
		ByteBuffer dataBuf = ByteBuffer.wrap(baos.toByteArray());
		// NOTE: The ByteBuffer doesn't get advanced, hence the use of no-side-effects
		// functions like getInt(address).
		// If you do something that advances it,
		// use a position(0) to deal with that.
		dataBuf.order(ByteOrder.LITTLE_ENDIAN);
		rsrcInfo = getResources(dataBuf);
		if (rsrcInfo == null) {
			inStream.close();
			throw new IOException("Could not find .rsrc segment!");
		}
		inChan = inStream.getChannel();
		inChan.position(0);
		// read PE header
		ByteBuffer peHead = ByteBuffer.allocate(0x208);
		peHead.order(ByteOrder.nativeOrder());
		inChan.read(peHead);
		peHead.flip();
		ByteBuffer uBuf = ByteBuffer.allocate(2);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		// find how many sections
		inChan.position(0x116);
		inChan.read(uBuf);
		uBuf.flip();
		int numSection = uBuf.getShort();
		// read each segment
		Vector<ByteBuffer> sections = new Vector<>();
		inChan.position(0x208);
		int rdataSec = -1;
		for (int i = 0; i < numSection; i++) {
			ByteBuffer nuBuf = ByteBuffer.allocate(0x28);
			nuBuf.order(ByteOrder.nativeOrder());
			inChan.read(nuBuf);
			nuBuf.flip();
			sections.add(nuBuf);
			String segStr = new String(nuBuf.array());
			if (segStr.contains(".rdata"))
				rdataSec = i;
		}
		headers = new ExeSec[sections.size()];
		for (int i = 0; i < sections.size(); i++)
			headers[i] = new ExeSec(sections.get(i), inChan);
		inStream.close();
		if (rdataSec == -1)
			throw new IOException("Could not find .rdata segment!");
		rdataPtr = headers[rdataSec].getPos();
		System.out.println("rdataPtr=0x" + Integer.toHexString(rdataPtr).toUpperCase());
	}

	/**
	 * Loads strings from the executable.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadExeStrings() throws IOException {
		String name = base.getName();
		String ext = name.substring(name.length() - 3, name.length());
		if (!"exe".equals(ext))
			throw new IOException("Base file is not an executable!");
		// setup I/O stuff
		FileInputStream inStream;
		FileChannel inChan;
		inStream = new FileInputStream(base);
		inChan = inStream.getChannel();
		ByteBuffer uBuf = ByteBuffer.allocate(2);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		// read the text
		exeStrings = new String[STRING_POINTERS.length];
		byte[] buffer = new byte[0x10];
		uBuf = ByteBuffer.allocate(0x10);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < STRING_POINTERS.length; i++) {
			inChan.position(rdataPtr + STRING_POINTERS[i]);
			inChan.read(uBuf);
			uBuf.flip();
			uBuf.get(buffer);
			String str = StrTools.CString(buffer, encoding);
			// backslashes are Windows-only, so replace them with forward slashes
			str = str.replaceAll("\\\\", "/");
			exeStrings[i] = str;
			uBuf.clear();
			notifyListeners(false, EVENT_EXE_STRING, null, i, STRING_POINTERS.length - 1);
		}
		inStream.close();
	}

	/**
	 * Initializes executable strings for CS+.
	 */
	private static void initExeStringsPlus() {
		exeStrings = new String[STRING_POINTERS.length];
		exeStrings[STRING_ARMSITEM] = "ArmsItem.tsc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_ARMSITEM, STRING_POINTERS.length - 1);
		exeStrings[STRING_IMG_EXT] = "%s/%s.bmp";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_IMG_EXT, STRING_POINTERS.length - 1);
		exeStrings[STRING_CREDIT] = "Credit.tsc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_CREDIT, STRING_POINTERS.length - 1);
		exeStrings[STRING_NPC_TBL] = "npc.tbl";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_NPC_TBL, STRING_POINTERS.length - 1);
		exeStrings[STRING_MYCHAR] = "MyChar";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_MYCHAR, STRING_POINTERS.length - 1);
		exeStrings[STRING_TITLE] = "Title";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_TITLE, STRING_POINTERS.length - 1);
		exeStrings[STRING_ARMSIMAGE] = "ArmsImage";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_ARMSIMAGE, STRING_POINTERS.length - 1);
		exeStrings[STRING_ARMS] = "Arms";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_ARMS, STRING_POINTERS.length - 1);
		exeStrings[STRING_ITEMIMAGE] = "ItemImage";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_ITEMIMAGE, STRING_POINTERS.length - 1);
		exeStrings[STRING_STAGEIMAGE] = "StageImage";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_STAGEIMAGE, STRING_POINTERS.length - 1);
		exeStrings[STRING_NPCSYM] = "Npc/NpcSym";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_NPCSYM, STRING_POINTERS.length - 1);
		exeStrings[STRING_NPCREGU] = "Npc/NpcRegu";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_NPCREGU, STRING_POINTERS.length - 1);
		exeStrings[STRING_TEXTBOX] = "TextBox";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_TEXTBOX, STRING_POINTERS.length - 1);
		exeStrings[STRING_CARET] = "Caret";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_CARET, STRING_POINTERS.length - 1);
		exeStrings[STRING_BULLET] = "Bullet";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_BULLET, STRING_POINTERS.length - 1);
		exeStrings[STRING_FACE] = "Face";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_FACE, STRING_POINTERS.length - 1);
		exeStrings[STRING_FADE] = "Fade";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_FADE, STRING_POINTERS.length - 1);
		exeStrings[STRING_DATA_FOLDER] = ""; // not needed
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_DATA_FOLDER, STRING_POINTERS.length - 1);
		exeStrings[STRING_LOADING] = "Loading";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_LOADING, STRING_POINTERS.length - 1);
		exeStrings[STRING_PXM_TAG] = "PXM";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PXM_TAG, STRING_POINTERS.length - 1);
		exeStrings[STRING_PROFILE_HEADER] = PlusProfile.DEFAULT_HEADER;
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PROFILE_HEADER, STRING_POINTERS.length - 1);
		exeStrings[STRING_PROFILE_FLAGH] = PlusProfile.DEFAULT_FLAGH;
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PROFILE_FLAGH, STRING_POINTERS.length - 1);
		exeStrings[STRING_STAGESELECT] = "StageSelect.tsc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_STAGESELECT, STRING_POINTERS.length - 1);
		exeStrings[STRING_STAGE_FOLDER] = "Stage";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_STAGE_FOLDER, STRING_POINTERS.length - 1);
		exeStrings[STRING_PRT_PREFIX] = "%s/Prt%s";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PRT_PREFIX, STRING_POINTERS.length - 1);
		exeStrings[STRING_PXA_EXT] = "%s/%s.pxa";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PXA_EXT, STRING_POINTERS.length - 1);
		exeStrings[STRING_PXM_EXT] = "%s/%s.pxm";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PXM_EXT, STRING_POINTERS.length - 1);
		exeStrings[STRING_PXE_EXT] = "%s/%s.pxe";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_PXE_EXT, STRING_POINTERS.length - 1);
		exeStrings[STRING_TSC_EXT] = "%s/%s.tsc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_TSC_EXT, STRING_POINTERS.length - 1);
		exeStrings[STRING_NPC_FOLDER] = "Npc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_NPC_FOLDER, STRING_POINTERS.length - 1);
		exeStrings[STRING_NPC_PREFIX] = "%s/Npc%s";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_NPC_PREFIX, STRING_POINTERS.length - 1);
		exeStrings[STRING_HEAD] = "Head.tsc";
		notifyListeners(false, EVENT_EXE_STRING, null, STRING_HEAD, STRING_POINTERS.length - 1);
	}

	/**
	 * Loads the "npc.tbl" file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadNpcTbl() throws IOException {
		File tblFile = correctFile(ResUtils.newFile(dataDir + "/" + getExeString(STRING_NPC_TBL)));
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
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_FLAGS, i, calculated_npcs - 1);
		}

		// read health section
		dBuf = ByteBuffer.allocate(2 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		healthDat = new short[calculated_npcs];
		for (int i = 0; i < healthDat.length; i++) {
			healthDat[i] = dBuf.getShort();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_HEALTH, i, calculated_npcs - 1);
		}

		// read tileset section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		tilesetDat = new byte[calculated_npcs];
		for (int i = 0; i < tilesetDat.length; i++) {
			tilesetDat[i] = dBuf.get();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_TILESET, i, calculated_npcs - 1);
		}

		// read death sound section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		deathDat = new byte[calculated_npcs];
		for (int i = 0; i < deathDat.length; i++) {
			deathDat[i] = dBuf.get();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_DEATHSND, i, calculated_npcs - 1);
		}

		// read hurt sound section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		hurtDat = new byte[calculated_npcs];
		for (int i = 0; i < hurtDat.length; i++) {
			hurtDat[i] = dBuf.get();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_HURTSND, i, calculated_npcs - 1);
		}

		// read size section
		dBuf = ByteBuffer.allocate(calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		sizeDat = new byte[calculated_npcs];
		for (int i = 0; i < sizeDat.length; i++) {
			sizeDat[i] = dBuf.get();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_SIZE, i, calculated_npcs - 1);
		}

		// read experience section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		expDat = new int[calculated_npcs];
		for (int i = 0; i < expDat.length; i++) {
			expDat[i] = dBuf.getInt();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_EXP, i, calculated_npcs - 1);
		}

		// read damage section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		damageDat = new int[calculated_npcs];
		for (int i = 0; i < damageDat.length; i++) {
			damageDat[i] = dBuf.getInt();
			notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_DAMAGE, i, calculated_npcs - 1);
		}

		int npcId = 0;
		// read hitbox section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		hitboxDat = new byte[4 * calculated_npcs];
		for (int i = 0; i < hitboxDat.length; i++) {
			hitboxDat[i] = dBuf.get();
			if (i % 4 == 0)
				notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_HITBOX, npcId++, calculated_npcs - 1);
		}

		npcId = 0;
		// read display box section
		dBuf = ByteBuffer.allocate(4 * calculated_npcs);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		displayDat = new byte[4 * calculated_npcs];
		for (int i = 0; i < displayDat.length; i++) {
			displayDat[i] = dBuf.get();
			if (i % 4 == 0)
				notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_DISPLAYBOX, npcId++, calculated_npcs);
		}
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
				notifyListeners(false, EVENT_MAP_DATA, null, i, numMaps - 1);
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
					notifyListeners(false, EVENT_MAP_DATA, null, i, numMaps - 1);
				} // for each map
			} else {
				// sue's workshop
				uBuf = ByteBuffer.allocate(4);
				uBuf.order(ByteOrder.LITTLE_ENDIAN);
				inChan.position(0x208 + 0x28 * mapSec + 0x10);
				inChan.read(uBuf);
				uBuf.flip();
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
					notifyListeners(false, EVENT_MAP_DATA, null, nMaps++, numMaps - 1);
				} // for each map
			}
		}

		inStream.close();
	}

	/**
	 * Loads map data from a "stage.tbl" file.
	 * 
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	private static void fillMapdataPlus() throws IOException {
		File stageTbl = new File(dataDir + "/stage.tbl"); // int maps array data
		FileChannel inChan;
		FileInputStream inStream;
		inStream = new FileInputStream(stageTbl);
		inChan = inStream.getChannel();
		int numMaps = (int) (stageTbl.length() / 229);
		ByteBuffer dBuf = ByteBuffer.allocate(numMaps * 229);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		for (int i = 0; i < numMaps; i++) // for each map
		{
			Mapdata newMap = new Mapdata(i);
			byte[] buf32 = new byte[32];
			dBuf.get(buf32);
			newMap.setTileset(StrTools.CString(buf32, encoding));
			dBuf.get(buf32);
			newMap.setFileName(StrTools.CString(buf32, encoding));
			newMap.setScrollType(dBuf.getInt());
			dBuf.get(buf32);
			newMap.setBgName(StrTools.CString(buf32, encoding));
			dBuf.get(buf32);
			newMap.setNpcSheet1(StrTools.CString(buf32, encoding));
			dBuf.get(buf32);
			newMap.setNpcSheet2(StrTools.CString(buf32, encoding));
			// newMap.setBoss(dBuf.get()); // not needed
			dBuf.get();
			dBuf.get(buf32);
			// newMap.setJpName(buf32); // not needed
			dBuf.get(buf32);
			newMap.setMapName(StrTools.CString(buf32, encoding));
			mapdata.add(newMap);
			notifyListeners(false, EVENT_MAP_DATA, null, i, numMaps - 1);
		}
		inChan.close();
		inStream.close();
	}

	/**
	 * Loads map info.
	 */
	private static void loadMapInfo() {
		int mdSize = mapdata.size();
		for (int i = 0; i < mdSize; i++)
			mapInfo.add(new MapInfo(mapdata.get(i)));
		for (int i = 0; i < mdSize; i++) {
			mapInfo.get(i).loadImages();
			notifyListeners(false, EVENT_MAP_INFO, LOADNAME_MAP_INFO_IMAGES, i, mdSize - 1);
		}
		for (int i = 0; i < mdSize; i++) {
			mapInfo.get(i).loadPXA();
			notifyListeners(false, EVENT_MAP_INFO, LOADNAME_MAP_INFO_PXA, i, mdSize - 1);
		}
		for (int i = 0; i < mdSize; i++) {
			mapInfo.get(i).loadMap();
			notifyListeners(false, EVENT_MAP_INFO, LOADNAME_MAP_INFO_PXM, i, mdSize - 1);
		}
		if (loadNpc)
			for (int i = 0; i < mdSize; i++) {
				mapInfo.get(i).loadEntities();
				notifyListeners(false, EVENT_MAP_INFO, LOADNAME_MAP_INFO_PXE, i, mdSize - 1);
			}
		if (loadTSC)
			for (int i = 0; i < mdSize; i++) {
				mapInfo.get(i).loadTSC();
				notifyListeners(false, EVENT_MAP_INFO, LOADNAME_MAP_INFO_TSC, i, mdSize - 1);
			}
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
			if (graphicsResolution == 2)
				return img;
			double scale = 2 / (double) graphicsResolution;
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
	 * @param name
	 *            file name
	 * @return file that was loaded
	 */
	private static File loadGraphic(String name) {
		File ret = correctFile(ResUtils.getGraphicsFile(dataDir.toString(), name));
		addImage(ret);
		return ret;
	}

	/**
	 * Loads a graphics file.
	 * 
	 * @param strid
	 *            executable string id to get file name from
	 * @return file that was loaded
	 */
	private static File loadGraphic(int strid) {
		return loadGraphic(getExeString(strid));
	}

	/**
	 * Loads graphics files.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadGraphics() throws IOException {
		if (graphicsResolution <= 0)
			graphicsResolution = 1;
		notifyListeners(false, EVENT_GRAPHICS, null, -1, -1);
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

	// ".rsrc" segment code starts here
	// thanks to @20kdc for basically writing this for me

	/**
	 * Load resources from the executable.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadRsrc() throws IOException {
		// setup I/O stuff
		FileInputStream inStream;
		inStream = new FileInputStream(base);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[512];
		while (inStream.available() > 0) {
			int l = inStream.read(buf);
			baos.write(buf, 0, l);
		}
		inStream.close();
		ByteBuffer dataBuf = ByteBuffer.wrap(baos.toByteArray());
		dataBuf.order(ByteOrder.LITTLE_ENDIAN);
		notifyListeners(false, EVENT_GRAPHICS_RSRC, null, -1, -1);
		String pixelName = getExeString(STRING_PIXEL);
		// Start finding information
		DirectoryEntry bitmap = findDirectory(dataBuf, rsrcInfo,
				DirectoryEntry.getEntries(dataBuf, rsrcInfo, rsrcInfo.offset), "2/" + pixelName + "/**");
		ByteArrayInputStream bais = new ByteArrayInputStream(
				transformBitmap(pullData(dataBuf, rsrcInfo, bitmap.fileOffset)));
		BufferedImage bi = ImageIO.read(bais);
		imageMap.put((pixel = new File(pixelName)), bi);
	}

	private static byte[] transformBitmap(byte[] bytes) {
		byte[] bt = new byte[bytes.length + 14];
		// Input buffer, used to get some details
		ByteBuffer bb1 = ByteBuffer.wrap(bytes);
		bb1.order(ByteOrder.LITTLE_ENDIAN);
		int hdrSize = bb1.getInt(0);
		if (hdrSize < 40)
			throw new RuntimeException("Expected BITMAPINFOHEADER, got BITMAPCOREHEADER");
		int palSize = bb1.getInt(0x20) * 4;
		int bpp = bb1.getShort(0x0E) & 0xFFFF;
		if (palSize == 0)
			if (bpp <= 8)
				palSize = 4 << bpp;
		int start = 14 + hdrSize + palSize;
		ByteBuffer bb = ByteBuffer.wrap(bt);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put((byte) 'B');
		bb.put((byte) 'M');
		bb.putInt(bt.length);
		bb.put((byte) 20);
		bb.put((byte) 'K');
		bb.put((byte) 'D');
		bb.put((byte) 'C');
		bb.putInt(start);
		bb.put(bytes);
		return bt;
	}

	private static byte[] pullData(ByteBuffer bb, ResourceInfo ri, int fileOffset) {
		int addr = bb.getInt(fileOffset) + ri.rvaOffset;
		int size = bb.getInt(fileOffset + 4);
		dumpHex("FINADR", addr);
		bb.position(addr);
		byte[] data = new byte[size];
		bb.get(data);
		bb.position(0);
		return data;
	}

	private static DirectoryEntry findDirectory(ByteBuffer bb, ResourceInfo ri, DirectoryEntry[] entries, String s) {
		int idx = s.lastIndexOf('/');
		if (idx != -1) {
			DirectoryEntry de = findDirectory(bb, ri, entries, s.substring(0, idx));
			if (!de.directory)
				return de;
			entries = DirectoryEntry.getEntries(bb, ri, de.fileOffset);
			s = s.substring(idx + 1);
		}
		for (DirectoryEntry de : entries) {
			System.out.println(s + " : " + de);
			if (s.equals("*"))
				return de;
			if (s.equals("**")) {
				if (de.directory)
					return findDirectory(bb, ri, DirectoryEntry.getEntries(bb, ri, de.fileOffset), "**");
				return de;
			}
			if (de.name == null) {
				if (Integer.toString(de.id).equals(s))
					return de;
			} else if (de.name.equals(s)) {
				return de;
			}
		}
		return null;
	}

	private static void dumpHex(String rvaofs, int rvaOffset) {
		System.out.println(rvaofs + ": 0x" + Integer.toHexString(rvaOffset));
	}

	public static ResourceInfo getResources(ByteBuffer bb) {
		int view = bb.getInt(0x3C);
		view += 0x04; // enter IMAGE_FILE_HEADER
		int sectionCount = bb.getShort(view + 2) & 0xFFFF;
		int toSkipOH = bb.getShort(view + 0x10) & 0xFFFF;
		view += 0x14; // enter IMAGE_OPTIONAL_HEADER
		int resourcesRVA = bb.getInt(view + 0x70);
		// Now we need to calculate offset & rvaOffset
		view += toSkipOH; // enter section headers
		for (int i = 0; i < sectionCount; i++) {
			int vSize = bb.getInt(view + 8);
			int rva = bb.getInt(view + 0x0C);
			int fileAddr = bb.getInt(view + 0x14);
			if ((resourcesRVA >= rva) && (resourcesRVA < (rva + vSize))) {
				ResourceInfo ri = new ResourceInfo();
				ri.rvaOffset = fileAddr - rva;
				ri.offset = resourcesRVA + ri.rvaOffset;
				return ri;
			}
			view += 0x28;
		}
		return null;
	}

	public static class ResourceInfo {
		// offset is what to modify an rsrc offset by to turn it into a file offset.
		// In other words, it's the file address of the resource section
		// rvaOffset is what to modify an RVA by to turn it into a file offset.
		public int offset, rvaOffset;
	}

	public static class DirectoryEntry {
		// May be NULL - See ID in this case
		public String name;
		public int id;
		// The position in the file of the target
		public int fileOffset;
		public boolean directory;

		public static DirectoryEntry[] getEntries(ByteBuffer bb, ResourceInfo ri, int view) {
			dumpHex("ENTER-FA", view);
			int namedCount = bb.getShort(view + 0x0C) & 0xFFFF;
			int idCount = bb.getShort(view + 0x0E) & 0xFFFF;
			view += 0x10;
			DirectoryEntry[] entries = new DirectoryEntry[namedCount + idCount];
			for (int i = 0; i < entries.length; i++) {
				entries[i] = new DirectoryEntry(bb, ri, view);
				view += 8;
			}
			return entries;
		}

		@Override
		public String toString() {
			String ts = name;
			if (ts == null)
				ts = Integer.toString(id);
			ts += " = " + Integer.toHexString(fileOffset) + " " + directory;
			return ts;
		}

		// Parses a specific directory entry.
		public DirectoryEntry(ByteBuffer bb, ResourceInfo ri, int view) {
			id = bb.getInt(view);
			if (id < 0) {
				int strStart = ri.offset + (id ^ 0x80000000);
				name = pullString(bb, strStart);
			}
			fileOffset = bb.getInt(view + 4);
			if (fileOffset < 0) {
				fileOffset ^= 0x80000000;
				directory = true;
			}
			fileOffset += ri.offset;
		}

		public static String pullString(ByteBuffer bb, int strStart) {
			int count = bb.getShort(strStart) & 0xFFFF;
			String s = "";
			for (int i = 0; i < count; i++)
				s += (char) (bb.getShort(strStart + 2 + (i * 2)) & 0xFFFF);
			return s;
		}
	}

	// ".rsrc" segment code ends here

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
		notifyListeners(true, SUBEVENT_IMAGE, srcFile.getAbsolutePath(), -1, -1);
		try {
			if (imageMap.containsKey(srcFile))
				return;
			imageMap.put(srcFile, loadImage(srcFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		notifyListeners(true, SUBEVENT_END, srcFile.getAbsolutePath(), -1, -1);
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
		if (key.exists())
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
		notifyListeners(true, SUBEVENT_PXA, srcFile.getAbsolutePath(), -1, -1);
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
			System.err.print("Failed to load PXA:\n" + srcFile);
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
		notifyListeners(true, SUBEVENT_END, srcFile.getAbsolutePath(), -1, -1);
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
	 * Gets the amount of strings loaded from the executable.
	 * 
	 * @return amount of strings from executable
	 */
	public static int getExeStringAmount() {
		return exeStrings.length;
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

	/**
	 * Checks if an executable is currently loaded.
	 * 
	 * @return <code>true</code> if executable is loaded, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isLoaded() {
		return loaded;
	}

	/**
	 * Gets the base file - the executable.
	 * 
	 * @return base file
	 */
	public static File getBase() {
		return base;
	}

	/**
	 * Gets the executable's PE headers.
	 * 
	 * @return headers
	 */
	public static ExeSec[] getHeaders() {
		return headers;
	}

	/**
	 * Gets the "data" directory.
	 * 
	 * @return data directory
	 */
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
		if (mapInfo.size() - 1 < num)
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

	/**
	 * Gets the "Title" graphics file.
	 * 
	 * @return Title file
	 */
	public static File getTitle() {
		return title;
	}

	public static File getPixel() {
		return pixel;
	}

	/**
	 * Gets the "MyChar" graphics file.
	 * 
	 * @return MyChar file
	 */
	public static File getMyChar() {
		return myChar;
	}

	/**
	 * Gets the "ArmsImage" graphics file.
	 * 
	 * @return ArmsImage file
	 */
	public static File getArmsImage() {
		return armsImage;
	}

	/**
	 * Gets the "Arms" graphics file.
	 * 
	 * @return Arms file
	 */
	public static File getArms() {
		return arms;
	}

	/**
	 * Gets the "ItemImage" graphics file.
	 * 
	 * @return ItemImage file
	 */
	public static File getItemImage() {
		return itemImage;
	}

	/**
	 * Gets the "StageImage" graphics file.
	 * 
	 * @return StageImage file
	 */
	public static File getStageImage() {
		return stageImage;
	}

	/**
	 * Gets the "NpcSym" graphics file.
	 * 
	 * @return NpcSym file
	 */
	public static File getNpcSym() {
		return npcSym;
	}

	/**
	 * Gets the "NpcRegu" graphics file.
	 * 
	 * @return NpcRegu file
	 */
	public static File getNpcRegu() {
		return npcRegu;
	}

	/**
	 * Gets the "TextBox" graphics file.
	 * 
	 * @return TextBox file
	 */
	public static File getTextBox() {
		return textBox;
	}

	/**
	 * Gets the "Caret" graphics file.
	 * 
	 * @return Caret file
	 */
	public static File getCaret() {
		return caret;
	}

	/**
	 * Gets the "Bullet" graphics file.
	 * 
	 * @return Bullet file
	 */
	public static File getBullet() {
		return bullet;
	}

	/**
	 * Gets the "Face" graphics file.
	 * 
	 * @return Face file
	 */
	public static File getFace() {
		return face;
	}

	/**
	 * Gets the "Fade" graphics file.
	 * 
	 * @return Fade file
	 */
	public static File getFade() {
		return fade;
	}

	/**
	 * Gets the "Loading" graphics file.
	 * 
	 * @return Loading file
	 */
	public static File getLoading() {
		return loading;
	}

}

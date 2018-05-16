package com.leo.cse.backend.exe;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.leo.cse.backend.BackendLogger;
import com.leo.cse.backend.ResUtils;
import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.profile.NormalProfile;
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
	 * A list of {@link ExeLoadListener}s that will be added next update.<br />
	 * <i>NOTE: These will be notified immediately after being added.</i>
	 */
	private static List<ExeLoadListener> listenersToAdd;
	/**
	 * A list of {@link ExeLoadListener}s that will be remove next update.<br />
	 * <i>NOTE: These will be removed before being notified.</i>
	 */
	private static List<ExeLoadListener> listenersToRemove;

	/**
	 * Initializes listener lists.
	 */
	private static void initListenerLists() {
		if (listeners == null)
			listeners = new LinkedList<>();
		if (listenersToAdd == null)
			listenersToAdd = new LinkedList<>();
		if (listenersToRemove == null)
			listenersToRemove = new LinkedList<>();
	}

	/**
	 * Attaches a load listener.
	 *
	 * @param l
	 *            listener
	 */
	public static void addListener(ExeLoadListener l) {
		initListenerLists();
		listenersToAdd.add(l);
	}

	/**
	 * Detaches a load listener.
	 * 
	 * @param l
	 *            listener
	 */
	public static void removeListener(ExeLoadListener l) {
		initListenerLists();
		listenersToRemove.add(l);
	}

	/**
	 * Removes all listeners.
	 */
	public static void removeAllListeners() {
		listeners = null;
		listenersToAdd = null;
		listenersToRemove = null;
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
		if (listenersToAdd != null) {
			listeners.addAll(listenersToAdd);
			listenersToAdd.clear();
		}
		if (listenersToRemove != null) {
			listeners.removeAll(listenersToRemove);
			listenersToRemove.clear();
		}
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
	 * The ".rdata" segment. Used to read {@linkplain #exeStrings the executable
	 * strings}.
	 */
	private static PEFile.Section rdataSection;

	// ---------------
	// .rdata Pointers
	// ---------------
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
	private static final int[] STRING_POINTERS = new int[] {
			ARMSITEM_PTR,
			IMG_EXT_PTR,
			CREDIT_PTR,
			NPC_TBL_PTR,
			PIXEL_PTR,
			MYCHAR_PTR,
			TITLE_PTR,
			ARMSIMAGE_PTR,
			ARMS_PTR,
			ITEMIMAGE_PTR,
			DATA_FOLDER_PTR,
			STAGEIMAGE_PTR,
			NPCSYM_PTR,
			NPCREGU_PTR,
			TEXTBOX_PTR,
			CARET_PTR,
			BULLET_PTR,
			FACE_PTR,
			FADE_PTR,
			LOADING_PTR,
			PXM_TAG_PTR,
			PROFILE_NAME_PTR,
			PROFILE_HEADER_PTR,
			PROFILE_FLAGH_PTR,
			STAGESELECT_PTR,
			STAGE_FOLDER_PTR,
			PRT_PREFIX_PTR,
			PXA_EXT_PTR,
			PXM_EXT_PTR,
			PXE_EXT_PTR,
			TSC_EXT_PTR,
			NPC_FOLDER_PTR,
			NPC_PREFIX_PTR,
			HEAD_PTR };

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
	 * Mod types.
	 * 
	 * @author Leo
	 *
	 */
	public enum ModType {
		/**
		 * Standard Cave Story executable.
		 */
		STANDARD,
		/**
		 * Cave Story+ stage.tbl file.
		 */
		PLUS;
	}

	/**
	 * Type of currently loaded mod.
	 */
	private static ModType type = ModType.STANDARD;

	/**
	 * Gets the type of the currently loaded mod.
	 * 
	 * @return mod type
	 */
	public static ModType getType() {
		return type;
	}

	/**
	 * Checks if the current "executable" is a stage.tbl file.
	 *
	 * @return <code>true</code> if in CS+ mode, <code>false</code> otherwise
	 */
	public static boolean isPlusMode() {
		return type == ModType.PLUS;
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
	 * If <code>true</code>, TSC files will be loaded, otherwise they will be
	 * ignored.
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
	 * PE data.
	 *
	 * @see PEFile
	 */
	private static PEFile peData;
	/**
	 * The contents of the ".rsrc" section.
	 * 
	 * @see RsrcHandler
	 */
	private static RsrcHandler rsrcData;
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
	 * Stores information about the game's starting point.
	 * 
	 * @author Leo
	 *
	 */
	public static class StartPoint {
		/**
		 * Starting maximum health.
		 */
		public short maxHealth;
		/**
		 * Starting current health.
		 */
		public short curHealth;
		/**
		 * Starting map.
		 */
		public int map;
		/**
		 * Starting X position.
		 */
		public short positionX;
		/**
		 * Starting Y position.
		 */
		public short positionY;
		/**
		 * Starting direction.
		 */
		public int direction;
	}

	/**
	 * The currently loaded game/mod's starting point.
	 */
	private static StartPoint startPoint;
	/**
	 * Alternate source for {@linkplain #startPoint the starting point}.<br>
	 * Will only be used if <code>startPoint == null</code> (if there is no
	 * game/mod loaded).
	 */
	private static Supplier<StartPoint> startPointSup;
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
	 * CS+ EXCLUSIVE - "UI" graphics file.
	 */
	private static File ui;

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
			BackendLogger.error("EXE loading failed.", e);
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
		String baseExt = base.getName();
		int dp = baseExt.lastIndexOf('.');
		if (dp == -1)
			throw new IOException("Can't load file with no extension as an executable!");
		baseExt = baseExt.substring(dp + 1);
		if (baseExt.equals("tbl"))
			// assume stage.tbl
			loadPlus();
		else if (baseExt.equals("exe"))
			loadVanilla();
		else
			throw new IOException("Can't load file with extension \"" + baseExt + "\" as an executable!");
	}

	/**
	 * Loads an executable as a mod.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadVanilla() throws IOException {
		type = ModType.STANDARD;
		try {
			notifyListeners(false, EVENT_PRELOAD, null, -1, -1);
			locateSections();
			loadExeStrings();
			ProfileManager.setHeader(getExeString(STRING_PROFILE_HEADER));
			ProfileManager.setFlagHeader(getExeString(STRING_PROFILE_FLAGH));
			dataDir = new File(base.getParent() + getExeString(STRING_DATA_FOLDER));
			entityList = new Vector<>();
			mapdata = new Vector<>();
			mapInfo = new Vector<>();
			imageMap = new HashMap<>();
			pxaMap = new HashMap<>();
			loadNpcTbl();
			fillMapdata();
			notifyListeners(false, EVENT_LOAD, null, -1, -1);
			loadGraphics();
			try {
				loadRsrc();
			} catch (Exception e) {
				// this can be ignored
				BackendLogger.error("Failed to laod embedded bitmaps! Luckily, they're not essential", e);
			}
			loadStartPoint();
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
	 * Loads a stage.tbl file as a mod.
	 *
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadPlus() throws IOException {
		type = ModType.PLUS;
		try {
			notifyListeners(false, EVENT_PRELOAD, null, -1, -1);
			initExeStringsPlus();
			ProfileManager.setHeader(getExeString(STRING_PROFILE_HEADER));
			ProfileManager.setFlagHeader(getExeString(STRING_PROFILE_FLAGH));
			dataDir = base.getParentFile();
			entityList = new Vector<>();
			mapdata = new Vector<>();
			mapInfo = new Vector<>();
			imageMap = new HashMap<>();
			pxaMap = new HashMap<>();
			loadNpcTbl();
			fillMapdataPlus();
			notifyListeners(false, EVENT_LOAD, null, -1, -1);
			loadGraphics();
			loadGraphicsPlus();
			initStartPointPlus();
			loadMapInfo();
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_SUCCESS, -1, -1);
		} catch (Exception e) {
			loaded = false;
			notifyListeners(false, EVENT_POSTLOAD, LOADNAME_POSTLOAD_FAILURE, -1, -1);
			throw e;
		}
		loaded = true;
	}

	public static File correctFile(File src) {
		if (!isPlusMode())
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
		startPoint = null;
		title = null;
		pixel = null;
		myChar = null;
		armsImage = null;
		itemImage = null;
		stageImage = null;
		npcSym = null;
		npcRegu = null;
		textBox = null;
		caret = null;
		bullet = null;
		face = null;
		fade = null;
		loading = null;
		ui = null;
		System.gc();
		ProfileManager.setHeader(NormalProfile.DEFAULT_HEADER);
		ProfileManager.setFlagHeader(NormalProfile.DEFAULT_FLAGH);
		type = ModType.STANDARD;
		notifyListeners(false, EVENT_UNLOAD, null, -1, -1);
	}

	/**
	 * Locate the executable sections.
	 *
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	private static void locateSections() throws IOException {
		// setup I/O stuff
		FileInputStream inStream = new FileInputStream(base);
		FileChannel chan = inStream.getChannel();
		long l = chan.size();
		if (l > 0x7FFFFFFF) {
			inStream.close();
			throw new IOException("Too big!");
		}
		ByteBuffer bb = ByteBuffer.allocate((int) l);
		if (chan.read(bb) != l) {
			inStream.close();
			throw new IOException("Didn't read whole file.");
		}
		inStream.close();
		peData = new PEFile(bb, 0x1000);
		// get sections
		int rdataSecId = peData.getSectionIndexByTag(".rdata");
		if (rdataSecId == -1)
			throw new IOException("Could not find .rdata segment!");
		rdataSection = peData.sections.get(rdataSecId);
		int rsrcSecId = peData.getResourcesIndex();
		if (rsrcSecId == -1)
			throw new IOException("Could not find .rsrc segment!");
		rsrcData = new RsrcHandler(peData.sections.get(rsrcSecId));
	}

	/**
	 * Sets an EXE string and notifies listeners.
	 * 
	 * @param i
	 *            index
	 * @param s
	 *            value
	 */
	private static void setExeString(int i, String s) {
		exeStrings[i] = s;
		notifyListeners(false, EVENT_EXE_STRING, null, i, exeStrings.length - 1);
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
		ByteBuffer uBuf = ByteBuffer.allocate(2);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		// read the text
		exeStrings = new String[STRING_POINTERS.length];
		byte[] buffer = new byte[0x10];
		uBuf = ByteBuffer.wrap(buffer);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < STRING_POINTERS.length; i++) {
			uBuf.put(rdataSection.rawData, STRING_POINTERS[i], buffer.length);
			uBuf.flip();
			String str = StrTools.CString(buffer, encoding);
			uBuf.clear();
			// backslashes are Windows-only, so replace them with forward slashes
			str = str.replaceAll("\\\\", "/");
			setExeString(i, str);
		}
	}

	/**
	 * Initializes executable strings for CS+.
	 */
	private static void initExeStringsPlus() {
		exeStrings = new String[STRING_POINTERS.length];
		setExeString(STRING_ARMSITEM, "ArmsItem.tsc");
		setExeString(STRING_IMG_EXT, "%s/%s.bmp");
		setExeString(STRING_CREDIT, "Credit.tsc");
		setExeString(STRING_NPC_TBL, "npc.tbl");
		setExeString(STRING_PIXEL, ""); // not needed
		setExeString(STRING_MYCHAR, "MyChar");
		setExeString(STRING_TITLE, "Title");
		setExeString(STRING_ARMSIMAGE, "ArmsImage");
		setExeString(STRING_ARMS, "Arms");
		setExeString(STRING_ITEMIMAGE, "ItemImage");
		setExeString(STRING_STAGEIMAGE, "StageImage");
		setExeString(STRING_NPCSYM, "Npc/NpcSym");
		setExeString(STRING_NPCREGU, "Npc/NpcRegu");
		setExeString(STRING_TEXTBOX, "TextBox");
		setExeString(STRING_CARET, "Caret");
		setExeString(STRING_BULLET, "Bullet");
		setExeString(STRING_FACE, "Face");
		setExeString(STRING_FADE, "Fade");
		setExeString(STRING_DATA_FOLDER, ""); // not needed
		setExeString(STRING_LOADING, "Loading");
		setExeString(STRING_PXM_TAG, "PXM");
		setExeString(STRING_PROFILE_NAME, "Profile.dat");
		setExeString(STRING_PROFILE_HEADER, NormalProfile.DEFAULT_HEADER);
		setExeString(STRING_PROFILE_FLAGH, NormalProfile.DEFAULT_FLAGH);
		setExeString(STRING_STAGESELECT, "StageSelect.tsc");
		setExeString(STRING_STAGE_FOLDER, "Stage");
		setExeString(STRING_PRT_PREFIX, "%s/Prt%s");
		setExeString(STRING_PXA_EXT, "%s/%s.pxa");
		setExeString(STRING_PXM_EXT, "%s/%s.pxm");
		setExeString(STRING_PXE_EXT, "%s/%s.pxe");
		setExeString(STRING_TSC_EXT, "%s/%s.tsc");
		setExeString(STRING_NPC_FOLDER, "Npc");
		setExeString(STRING_NPC_PREFIX, "%s/Npc%s");
		setExeString(STRING_HEAD, "Head.tsc");
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

		inStream = new FileInputStream(tblFile);
		calculated_npcs = (int) (tblFile.length() / 24);
		inChan = inStream.getChannel();

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
			if (i != 0 && i % 4 == 0)
				notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_HITBOX, npcId++, calculated_npcs - 1);
		}

		npcId = 0;
		// read display box section
		dBuf.clear();
		inChan.read(dBuf);
		dBuf.flip();
		displayDat = new byte[4 * calculated_npcs];
		for (int i = 0; i < displayDat.length; i++) {
			displayDat[i] = dBuf.get();
			if (i != 0 && i % 4 == 0)
				notifyListeners(false, EVENT_NPC_TBL, LOADNAME_NPC_TBL_DISPLAYBOX, npcId++, calculated_npcs - 1);
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
		ByteBuffer uBuf = ByteBuffer.allocate(2);
		uBuf.order(ByteOrder.LITTLE_ENDIAN);
		// find the .csmap or .swdata segment
		PEFile.Section mapSec = null;
		String mapSecTag = null;
		for (int i = 0; i < peData.sections.size(); i++) {
			PEFile.Section s = peData.sections.get(i);
			String st = s.decodeTag();
			if (st.equals(".csmap") || st.equals(".swdata")) {
				mapSec = s;
				mapSecTag = st;
				break;
			}
		}

		if (mapSec == null) // virgin executable
		{
			// setup I/O stuff
			FileInputStream inStream = new FileInputStream(base);
			FileChannel inChan = inStream.getChannel();
			int numMaps = 95;
			inChan.position(0x937B0); // seek to start of mapdatas
			for (int i = 0; i < numMaps; i++) {
				// for each map
				uBuf = ByteBuffer.allocate(200);
				uBuf.order(ByteOrder.LITTLE_ENDIAN);
				inChan.read(uBuf);
				uBuf.flip();
				mapdata.add(new Mapdata(i, uBuf, type, encoding));
				notifyListeners(false, EVENT_MAP_DATA, null, i, numMaps - 1);
			}
			inStream.close();
		} else { // exe has been edited probably
			ByteBuffer buf = ByteBuffer.wrap(mapSec.rawData);
			int numMaps = mapSec.rawData.length / 200;
			if (mapSecTag.contains(".csmap")) {
				// cave editor/booster's lab
				for (int i = 0; i < numMaps; i++) {
					// for each map
					mapdata.add(new Mapdata(i, buf, type, encoding));
					notifyListeners(false, EVENT_MAP_DATA, null, i, numMaps - 1);
				}
			} else {
				// sue's workshop
				int nMaps = 0;
				while (true) {
					// for each map
					mapdata.add(new Mapdata(nMaps, buf, type, encoding));
					notifyListeners(false, EVENT_MAP_DATA, null, nMaps++, numMaps - 1);
				}
			}
		}
	}

	/**
	 * Loads map data from a "stage.tbl" file.
	 *
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	private static void fillMapdataPlus() throws IOException {
		FileChannel inChan;
		FileInputStream inStream;
		inStream = new FileInputStream(base);
		inChan = inStream.getChannel();
		int numMaps = (int) (base.length() / 229);
		ByteBuffer dBuf = ByteBuffer.allocate(numMaps * 229);
		dBuf.order(ByteOrder.LITTLE_ENDIAN);
		inChan.read(dBuf);
		dBuf.flip();
		for (int i = 0; i < numMaps; i++) { // for each map
			mapdata.add(new Mapdata(i, dBuf, type, encoding));
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
	 * @throws Exception
	 *             if an error occurs.
	 */
	private static BufferedImage loadImage(File srcFile, boolean trans) throws Exception {
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
			BackendLogger.error("Failed to load image " + srcFile, e);
			throw e;
		}
	}

	/**
	 * Loads an image.
	 *
	 * @param srcFile
	 *            source image
	 * @return filtered image
	 * @throws IOException
	 *             if an error occurs.
	 */
	private static BufferedImage loadImage(File srcFile) throws Exception {
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

	/**
	 * Loads CS+ exclusive graphics files.
	 *
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void loadGraphicsPlus() throws IOException {
		ui = loadGraphic("UI");
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
		String pixelName = getExeString(STRING_PIXEL);
		byte[] bmp = rsrcData.getBitmapData(pixelName, 1041);
		if (bmp == null)
			throw new IOException("Embedded bitmap \"" + pixelName + "\" was not found!");
		try {
			bmp = transformBitmap(bmp);
		} catch (IOException e) {
			throw new IOException("transformBitmap threw exception: " + e.getMessage(), e);
		}
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(bmp));
		imageMap.put((pixel = new File(pixelName)), bi);
		notifyListeners(false, EVENT_GRAPHICS_RSRC, null, -1, -1);
	}

	/**
	 * Converts a .rsrc bitmap to something readable using
	 * {@link ImageIO#read(java.io.InputStream)}.
	 * 
	 * @param bytes
	 *            .rsrc bitmap data
	 * @return readable bitmap data (to wrap in a {@link ByteArrayInputStream})
	 * @throws IOException
	 *             if the bitmap data cannot be converted.
	 * @author 20kdc
	 */
	private static byte[] transformBitmap(byte[] bytes) throws IOException {
		byte[] bt = new byte[bytes.length + 14];
		// Input buffer, used to get some details
		ByteBuffer bb1 = ByteBuffer.wrap(bytes);
		bb1.order(ByteOrder.LITTLE_ENDIAN);
		int hdrSize = bb1.getInt(0);
		if (hdrSize < 40)
			throw new IOException("Expected BITMAPINFOHEADER, got BITMAPCOREHEADER");
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

	// ".rsrc" segment code ends here

	public static final int RVA_STARTING_HP_MAX = 0x14BD8;
	public static final int RVA_STARTING_HP_CUR = 0x14BCF;
	public static final int RVA_STARTING_MAP = 0x1D599;
	public static final int RVA_STARTING_POS_X = 0x1D592;
	public static final int RVA_STARTING_POS_Y = 0x1D590;
	public static final int RVA_STARTING_DIR = 0x14B74;

	/**
	 * Reads the start point.
	 */
	private static void loadStartPoint() {
		startPoint = new StartPoint();
		startPoint.maxHealth = peData.setupRVAPoint(RVA_STARTING_HP_MAX).getShort();
		startPoint.curHealth = peData.setupRVAPoint(RVA_STARTING_HP_CUR).getShort();
		startPoint.map = Byte.toUnsignedInt(peData.setupRVAPoint(RVA_STARTING_MAP).get());
		startPoint.positionX = (short) peData.setupRVAPoint(RVA_STARTING_POS_X).get();
		startPoint.positionY = (short) peData.setupRVAPoint(RVA_STARTING_POS_Y).get();
		startPoint.direction = peData.setupRVAPoint(RVA_STARTING_DIR).getInt();
	}

	/**
	 * Initializes the start point for CS+.
	 */
	private static void initStartPointPlus() {
		startPoint = new StartPoint();
		startPoint.maxHealth = 3;
		startPoint.curHealth = startPoint.maxHealth;
		startPoint.map = 13; // Start Point
		startPoint.positionX = 10;
		startPoint.positionY = 8;
		startPoint.direction = 2; // Right
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
		notifyListeners(true, SUBEVENT_IMAGE, srcFile.getAbsolutePath(), -1, -1);
		try {
			if (imageMap.containsKey(srcFile))
				return;
			imageMap.put(srcFile, loadImage(srcFile));
		} catch (Exception e) {
			BackendLogger.error("Failed to add image " + srcFile, e);
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
		BackendLogger.error("Key not found for getImageGraphics: " + key);
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
		BackendLogger.error("Key not found for getImage: " + key);
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
		BackendLogger.error("Key not found for getImageHeight: " + key);
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
		BackendLogger.error("Key not found for getImageWidth: " + key);
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
		FileInputStream inStream = null;
		if (pxaMap.containsKey(srcFile))
			return pxaMap.get(srcFile);
		byte[] pxaArray = null;
		boolean succ = false;
		notifyListeners(true, SUBEVENT_PXA, srcFile.getAbsolutePath(), -1, -1);
		try {
			inStream = new FileInputStream(srcFile);
			FileChannel inChan = inStream.getChannel();
			ByteBuffer pxaBuf = ByteBuffer.allocate(256);// this is the max size. Indeed, the only size..
			inChan.read(pxaBuf);
			inStream.close();
			pxaBuf.flip();
			pxaArray = pxaBuf.array();
			pxaMap.put(srcFile, pxaArray);
			succ = true;
		} catch (Exception e) {
			BackendLogger.error("Failed to load PXA:\n" + srcFile, e);
		} finally {
			if (inStream != null)
				try {
					inStream.close();
				} catch (IOException e) {
					BackendLogger.warn("failed to close PXA input stream");
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
	 * Gets the executable's PE data.
	 *
	 * @return PE data
	 */
	public static PEFile getPEData() {
		return peData;
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
	 * Sets {@linkplain #startPointSup the starting point supplier}.
	 * 
	 * @param startPointSup
	 *            start point supplier
	 */
	public static void setStartPointSup(Supplier<StartPoint> startPointSup) {
		ExeData.startPointSup = startPointSup;
	}

	/**
	 * Gets the game's starting point.
	 * 
	 * @return start point
	 */
	public static StartPoint getStartPoint() {
		if (startPoint != null)
			return startPoint;
		else if (startPointSup != null)
			return startPointSup.get();
		return null;
	}

	/**
	 * Gets the "Title" graphics file.
	 *
	 * @return Title file
	 */
	public static File getTitle() {
		return title;
	}

	/**
	 * Gets the "PIXEL" resource file.
	 * 
	 * @return Pixel file
	 */
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

	/**
	 * Gets the "UI" graphics file.
	 * 
	 * @return UI file
	 */
	public static File getUI() {
		return ui;
	}

}

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.leo.cse.backend.ResUtils;
import com.leo.cse.backend.StrTools;
import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.PlusProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.MCI;

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

	/**
	 * Pre-load event.
	 * 
	 * @see ExeLoadListener#preLoad(boolean)
	 * @see #notifyListeners(int)
	 */
	private static final int NOTIFY_PRELOAD = 0;
	/**
	 * Load event.
	 * 
	 * @see ExeLoadListener#load(boolean)
	 * @see #notifyListeners(int)
	 */
	private static final int NOTIFY_LOAD = 1;
	/**
	 * Post-load event.
	 * 
	 * @see ExeLoadListener#postLoad(boolean)
	 * @see #notifyListeners(int)
	 */
	private static final int NOTIFY_POSTLOAD = 2;
	/**
	 * Unload event.
	 * 
	 * @see ExeLoadListener#unload()
	 * @see #notifyListeners(int)
	 */
	private static final int NOTIFY_UNLOAD = 3;

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
	private static void notifyListeners(int notifyType) {
		if (listeners == null)
			return;
		for (ExeLoadListener l : listeners)
			switch (notifyType) {
			case NOTIFY_PRELOAD:
				l.preLoad(plusMode);
				break;
			case NOTIFY_LOAD:
				l.load(plusMode);
				break;
			case NOTIFY_POSTLOAD:
				l.postLoad(plusMode);
				break;
			case NOTIFY_UNLOAD:
				l.unload();
				break;
			}
	}

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
	 * Base pointer for the ".rdata" segment in the executable.
	 */
	private static int rdataPtr;

	// --------
	// Pointers
	// --------
	// ...well, technically not actual pointers, but file positions
	// Relative to rdataPtr!
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
			MYCHAR_PTR, TITLE_PTR, ARMSIMAGE_PTR, ARMS_PTR, ITEMIMAGE_PTR, DATA_FOLDER_PTR, STAGEIMAGE_PTR, NPCSYM_PTR,
			NPCREGU_PTR, TEXTBOX_PTR, CARET_PTR, BULLET_PTR, FACE_PTR, FADE_PTR, LOADING_PTR, PXM_TAG_PTR,
			PROFILE_HEADER_PTR, PROFILE_FLAGH_PTR, STAGESELECT_PTR, STAGE_FOLDER_PTR, PRT_PREFIX_PTR, PXA_EXT_PTR,
			PXM_EXT_PTR, PXE_EXT_PTR, TSC_EXT_PTR, NPC_FOLDER_PTR, NPC_PREFIX_PTR, HEAD_PTR };

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
		STRING_HEAD = Arrays.binarySearch(STRING_POINTERS, HEAD_PTR);
	}

	// TODO CS+ support
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
			base = new File(
					ProfileManager.getFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
		if (!base.exists())
			return;
		load0(base);
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
		/*
		if (base.getName().endsWith(".tbl")) {
			// assume stage.tbl
			plusMode = true;
			loadPlus();
			return;
		} else
		*/
			plusMode = false;
		try {
			notifyListeners(NOTIFY_PRELOAD);
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
			notifyListeners(NOTIFY_LOAD);
			loadMapInfo();
			loadGraphics();
			notifyListeners(NOTIFY_POSTLOAD);
		} catch (IOException e) {
			loaded = false;
			throw e;
		}
		loaded = true;
	}

	/**
	 * Loads mapdata from a stage.tbl file.
	 * 
	 * @throws IOException
	 *             probably all the time because this code is designed for
	 *             executables.
	 */
	// TODO CS+ support
	@SuppressWarnings("unused")
	private static void loadPlus() throws IOException {
		System.out.println("Attempting to load CS+ stuff, errors may occur!");
		try {
			notifyListeners(NOTIFY_PRELOAD);
			initExeStringsPlus();
			ProfileManager.setHeader(getExeString(STRING_PROFILE_HEADER));
			ProfileManager.setFlagHeader(getExeString(STRING_PROFILE_FLAGH));
			dataDir = ResUtils.getBaseFolder(base);
			entityList = new Vector<EntityData>();
			mapdata = new Vector<Mapdata>();
			mapInfo = new Vector<MapInfo>();
			imageMap = new HashMap<File, BufferedImage>();
			pxaMap = new HashMap<File, byte[]>();
			loadNpcTbl();
			fillMapdataPlus();
			notifyListeners(NOTIFY_LOAD);
			loadMapInfo();
			loadGraphics();
			notifyListeners(NOTIFY_POSTLOAD);
		} catch (IOException e) {
			loaded = false;
			throw e;
		}
		loaded = true;
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
		ProfileManager.setHeader(NormalProfile.DEFAULT_HEADER);
		ProfileManager.setFlagHeader(NormalProfile.DEFAULT_FLAGH);
		System.gc();
		notifyListeners(NOTIFY_UNLOAD);
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

		public void setTag(String t) {
			tag = t;
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

		public ByteBuffer toBuf() {
			ByteBuffer retVal = ByteBuffer.allocate(0x28);
			retVal.order(ByteOrder.nativeOrder());

			byte[] tagDat = java.util.Arrays.copyOf(tag.getBytes(), 8);
			retVal.put(tagDat);
			retVal.putInt(vSize);
			retVal.putInt(vAddr);
			retVal.putInt(rSize);
			retVal.putInt(rAddr);
			retVal.putInt(pReloc);
			retVal.putInt(pLine);
			retVal.putShort(numReloc);
			retVal.putShort(numLine);
			retVal.putInt(attrib);
			retVal.flip();
			return retVal;
		}
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
		// locate .rdata segment
		FileInputStream inStream;
		FileChannel inChan;
		inStream = new FileInputStream(base);
		inChan = inStream.getChannel();
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
		// find the .rdata segment
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
		ExeSec[] headers = new ExeSec[sections.size()];
		for (int i = 0; i < sections.size(); i++) {
			headers[i] = new ExeSec(sections.get(i), inChan);
		}
		if (rdataSec == -1) {
			inStream.close();
			throw new IOException("Could not find .rdata segment!");
		}
		rdataPtr = headers[rdataSec].getPos();
		System.out.println("rdataPtr=0x" + Integer.toHexString(rdataPtr).toUpperCase());
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
		}
		inStream.close();
	}

	/**
	 * Initializes executable strings for CS+.
	 */
	private static void initExeStringsPlus() {
		exeStrings = new String[STRING_POINTERS.length];
		exeStrings[STRING_ARMSITEM] = "ArmsItem.tsc";
		exeStrings[STRING_IMG_EXT] = "%s/%s.png"; // CS+ uses PNGs, not BMPs
		exeStrings[STRING_CREDIT] = "Credit.tsc";
		exeStrings[STRING_NPC_TBL] = "npc.tbl";
		exeStrings[STRING_MYCHAR] = "MyChar";
		exeStrings[STRING_TITLE] = "Title";
		exeStrings[STRING_ARMSIMAGE] = "ArmsImage";
		exeStrings[STRING_ARMS] = "Arms";
		exeStrings[STRING_ITEMIMAGE] = "ItemImage";
		exeStrings[STRING_STAGEIMAGE] = "StageImage";
		exeStrings[STRING_NPCSYM] = "Npc/NpcSym";
		exeStrings[STRING_NPCREGU] = "Npc/NpcRegu";
		exeStrings[STRING_TEXTBOX] = "Textbox";
		exeStrings[STRING_CARET] = "Caret";
		exeStrings[STRING_BULLET] = "Bullet";
		exeStrings[STRING_FACE] = "Face";
		exeStrings[STRING_FADE] = "Fade";
		exeStrings[STRING_DATA_FOLDER] = ""; // not needed
		exeStrings[STRING_LOADING] = "Loading";
		exeStrings[STRING_PXM_TAG] = "PXM";
		exeStrings[STRING_PROFILE_HEADER] = PlusProfile.DEFAULT_HEADER;
		exeStrings[STRING_PROFILE_FLAGH] = PlusProfile.DEFAULT_FLAGH;
		exeStrings[STRING_STAGESELECT] = "StageSelect.tsc";
		exeStrings[STRING_STAGE_FOLDER] = "Stage";
		exeStrings[STRING_PRT_PREFIX] = "%s/Prt%s";
		exeStrings[STRING_PXA_EXT] = "%s/%s.pxa";
		exeStrings[STRING_PXM_EXT] = "%s/%s.pxm";
		exeStrings[STRING_PXE_EXT] = "%s/%s.pxe";
		exeStrings[STRING_TSC_EXT] = "%s/%s.tsc";
		exeStrings[STRING_NPC_FOLDER] = "Npc";
		exeStrings[STRING_NPC_PREFIX] = "%s/Npc%s";
		exeStrings[STRING_HEAD] = "Head.tsc";
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
	 * Loads map data from a "stage.tbl" file.
	 * 
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	// TODO CS+ support
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
			dBuf.get(buf32);
			// newMap.setJpName(buf32); // not needed
			dBuf.get(buf32);
			newMap.setMapName(StrTools.CString(buf32, encoding));
			mapdata.add(newMap);
		}
		inChan.close();
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

	/**
	 * Gets the "Title" graphics file.
	 * 
	 * @return Title file
	 */
	public static File getTitle() {
		return title;
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

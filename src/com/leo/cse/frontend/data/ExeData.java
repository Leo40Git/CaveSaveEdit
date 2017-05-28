package com.leo.cse.frontend.data;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.carrotlord.string.StrTools;
import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.SaveEditorPanel;

// credit to Noxid for making Booster's Lab open source so I could steal code from it
public class ExeData {

	private ExeData() {
	}

	private static final int ARMSITEM_PTR = 0x8C270;
	private static final int IMG_EXT_PTR = 0x8C280;
	private static final int NPC_TBL_PTR = 0x8C3AB;
	private static final int MYCHAR_PTR = 0x8C4F0;
	private static final int ARMSIMAGE_PTR = 0x8C500;
	private static final int ITEMIMAGE_PTR = 0x8C514;
	private static final int DATA_FOLDER_PTR = 0x8C5BC;
	private static final int STAGEIMAGE_PTR = 0x8C520;
	private static final int NPCSYM_PTR = 0x8C52C;
	private static final int NPCREGU_PTR = 0x8C538;
	private static final int PXM_TAG_PTR = 0x8C67C;
	private static final int STAGESELECT_PTR = 0x8C770;
	private static final int STAGE_FOLDER_PTR = 0x8C7D4;
	private static final int PRT_PREFIX_PTR = 0x8C7DC;
	private static final int PXA_EXT_PTR = 0x8C7E8;
	private static final int PXM_EXT_PTR = 0x8C7F4;
	private static final int PXE_EXT_PTR = 0x8C800;
	private static final int TSC_EXT_PTR = 0x8C80C;
	private static final int NPC_FOLDER_PTR = 0x8C81C;
	private static final int NPC_PREFIX_PTR = 0x8C820;

	private static final int[] STRING_POINTERS = new int[] { ARMSITEM_PTR, IMG_EXT_PTR, NPC_TBL_PTR, MYCHAR_PTR,
			ARMSIMAGE_PTR, ITEMIMAGE_PTR, DATA_FOLDER_PTR, STAGEIMAGE_PTR, NPCSYM_PTR, NPCREGU_PTR, PXM_TAG_PTR,
			STAGESELECT_PTR, STAGE_FOLDER_PTR, PRT_PREFIX_PTR, PXA_EXT_PTR, PXM_EXT_PTR, PXE_EXT_PTR, TSC_EXT_PTR,
			NPC_FOLDER_PTR, NPC_PREFIX_PTR };

	public static final int STRING_ARMSITEM = 0;
	public static final int STRING_IMG_EXT = 1;
	public static final int STRING_NPC_TBL = 2;
	public static final int STRING_MYCHAR = 3;
	public static final int STRING_ARMSIMAGE = 4;
	public static final int STRING_ITEMIMAGE = 5;
	public static final int STRING_DATA_FOLDER = 6;
	public static final int STRING_STAGEIMAGE = 7;
	public static final int STRING_NPCSYM = 8;
	public static final int STRING_NPCREGU = 9;
	public static final int STRING_PXM_TAG = 10;
	public static final int STRING_STAGESELECT = 11;
	public static final int STRING_STAGE_FOLDER = 12;
	public static final int STRING_PRT_PREFIX = 13;
	public static final int STRING_PXA_EXT = 14;
	public static final int STRING_PXM_EXT = 15;
	public static final int STRING_PXE_EXT = 16;
	public static final int STRING_TSC_EXT = 17;
	public static final int STRING_NPC_FOLDER = 18;
	public static final int STRING_NPC_PREFIX = 19;

	private static String[] exeStrings;
	private static boolean loaded = false;
	private static File base;
	private static File dataDir;
	private static Vector<EntityData> entityList;
	private static Vector<Mapdata> mapdata;
	private static Vector<MapInfo> mapInfo;
	private static Map<File, BufferedImage> imgMap;
	private static Map<File, byte[]> pxaMap;
	private static File myChar;
	private static File armsImage;
	private static File itemImage;
	private static File stageImage;
	private static File npcSym;
	private static File npcRegu;

	public static void load(File file) throws IOException {
		if (!Profile.isLoaded() || Profile.getFile() == null)
			return;
		File base = file;
		if (base == null)
			base = new File(Profile.getFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
		while (!base.exists()) {
			int returnVal = SaveEditorPanel.openFileChooser("Open mod executable",
					new FileNameExtensionFilter("Applications", "exe"), base, false);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				base = SaveEditorPanel.getSelectedFile();
			else
				return;
			if (!base.exists())
				JOptionPane.showMessageDialog(Main.window, "Mod executable \"" + base.getName() + "\" does not exist!",
						"EXE does not exist", JOptionPane.ERROR_MESSAGE);
		}
		load0(base, true);
	}

	public static void load() throws IOException {
		load(null);
	}

	public static void reload() throws IOException {
		if (!loaded || base == null)
			return;
		load0(base, false);
	}

	private static void load0(File base, boolean loadGraphics) throws IOException {
		String encoding = Main.encoding;
		ExeData.base = base;
		// read exe strings
		exeStrings = new String[20];
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
			exeStrings[i] = StrTools.CString(buffer, encoding);
			uBuf.clear();
		}
		inStream.close();
		dataDir = new File(base.getParent() + exeStrings[STRING_DATA_FOLDER]);
		entityList = new Vector<EntityData>();
		mapdata = new Vector<Mapdata>();
		mapInfo = new Vector<MapInfo>();
		imgMap = new HashMap<File, BufferedImage>();
		pxaMap = new HashMap<File, byte[]>();
		loadNpcTbl();
		fillMapdata();
		loadMapInfo();
		if (loadGraphics)
			loadGraphics();
		loaded = true;
	}

	public static void unload() {
		exeStrings = null;
		loaded = false;
		base = null;
		dataDir = null;
		entityList = null;
		mapdata = null;
		mapInfo = null;
		imgMap = null;
		pxaMap = null;
		myChar = null;
		armsImage = null;
		itemImage = null;
		stageImage = null;
		npcRegu = null;
		npcSym = null;
		System.gc();
	}

	private static void loadNpcTbl() throws IOException {
		File tblFile = new File(base.getParent() + exeStrings[STRING_DATA_FOLDER] + "/" + exeStrings[STRING_NPC_TBL]);
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

		// read hitbox section
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

		// TODO Find a way to auto-detect framerects
	}

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
				newMap.setFileName(StrTools.CString(buffer, encoding));
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

	private static void loadMapInfo() {
		for (int i = 0; i < mapdata.size(); i++)
			mapInfo.add(new MapInfo(mapdata.get(i)));
	}

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

	private static BufferedImage loadImage(File srcFile) throws IOException {
		return loadImage(srcFile, true);
	}

	private static void loadGraphics() throws IOException {
		myChar = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_MYCHAR]);
		addImage(myChar);
		armsImage = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_ARMSIMAGE]);
		addImage(armsImage);
		itemImage = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_ITEMIMAGE]);
		addImage(itemImage);
		stageImage = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_STAGEIMAGE]);
		addImage(stageImage);
		npcSym = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_NPCSYM]);
		addImage(npcSym);
		npcRegu = ResUtils.getGraphicsFile(dataDir.toString(), exeStrings[STRING_NPCREGU]);
		addImage(npcRegu);
	}

	/**
	 * This method is a proxy to the file version of this method
	 * 
	 * @param srcFile
	 *            location of the file to load
	 * @param filterType
	 *            Specifies a specific operation to perform when loading
	 */
	public static void addImage(String srcFile) {
		addImage(new File(srcFile));
	}

	/**
	 * Attempts to add an image to the store. Valid filter type values: 0 - No
	 * filter 1 - Convert black pixels to transparent
	 * 
	 * @param srcFile
	 *            image to load
	 * @param filterType
	 *            filtering method
	 */
	public static void addImage(File srcFile) {
		try {
			if (imgMap.containsKey(srcFile))
				return;
			imgMap.put(srcFile, loadImage(srcFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is a proxy to the file version of this method
	 * 
	 * @param src
	 *            location of the file to load
	 * @param filterType
	 *            Specifies a specific operation to perform when loading
	 */
	public static void reloadImage(String src) {
		reloadImage(new File(src));
	}

	/**
	 * If the file exists in the repository, replace. If not, load it anyway
	 * 
	 * @param srcFile
	 *            image to load
	 * @param filterType
	 *            filtering method
	 */
	public static void reloadImage(File srcFile) {
		if (imgMap.containsKey(srcFile)) {
			imgMap.get(srcFile).flush();
			imgMap.remove(srcFile);
		}
		addImage(srcFile);
	}

	public static java.awt.Graphics getImgGraphics(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getGraphics();
		System.err.println("Key not found for getImgGraphics");
		System.err.println(key);
		return null;
	}

	public static BufferedImage getImg(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key);
		System.err.println("Key not found for getImg");
		System.err.println(key);
		return null;
	}

	public static BufferedImage getImg(String key) {
		return getImg(new File(key));
	}

	public static int getImgH(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getHeight();
		System.err.println("Key not found for getImgH");
		System.err.println(key);
		return -1;
	}

	public static int getImgW(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getWidth();
		System.err.println("Key not found for getImgW");
		System.err.println(key);
		return -1;
	}

	public static byte[] addPxa(File srcFile, int size) {
		FileChannel inChan = null;
		if (pxaMap.containsKey(srcFile))
			return pxaMap.get(srcFile);
		byte[] pxaArray = null;
		boolean succ = false;
		try {
			FileInputStream inStream = new FileInputStream(srcFile);
			inChan = inStream.getChannel();
			ByteBuffer pxaBuf = ByteBuffer.allocate(size);// this is the max size. Indeed, the only size..
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
				byte[] dummyArray = new byte[size];
				pxaMap.put(srcFile, dummyArray);
			}
		}
		return pxaArray;
	}

	public static byte[] addPxa(String src, int size) {
		return addPxa(new File(src), size);
	}

	public static byte[] getPxa(File srcFile) {
		return pxaMap.get(srcFile);
	}

	public static byte[] getPxa(String src) {
		return getPxa(new File(src));
	}

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

	public static MapInfo getMapInfo(int num) {
		if (num < 0)
			throw new IndexOutOfBoundsException("Requested map number (" + num + ") is negative!");
		if (mapInfo.size() < num)
			throw new IndexOutOfBoundsException(
					"Requested map number is " + num + ", but maximum map number is " + mapdata.size() + "!");
		return mapInfo.get(num);
	}

	public static int getMapInfoCount() {
		return mapInfo.size();
	}

	public static EntityData getEntityInfo(short entityType) {
		return entityList.get(entityType);
	}

	public static File getMyChar() {
		return myChar;
	}

	public static File getArmsImage() {
		return armsImage;
	}

	public static File getItemImage() {
		return itemImage;
	}

	public static File getStageImage() {
		return stageImage;
	}

	public static File getNpcRegu() {
		return npcRegu;
	}

	public static File getNpcSym() {
		return npcSym;
	}

}

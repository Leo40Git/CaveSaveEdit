package com.leo.cse.frontend.data;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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
public class CSData {

	private CSData() {
	}

	private static boolean loaded = false;
	private static File base;
	private static File dataDir;
	private static Vector<Mapdata> mapdata;
	private static Vector<MapInfo> mapInfo;
	private static Map<File, BufferedImage> imgMap;
	private static Map<File, byte[]> pxaMap;
	private static BufferedImage myChar;
	private static BufferedImage armsImage;
	private static BufferedImage itemImage;
	private static BufferedImage stageImage;
	private static File npcFolder;
	private static BufferedImage npcSym;

	public static void load() throws IOException {
		if (!Profile.isLoaded() || Profile.getFile() == null)
			return;
		File base = new File(Profile.getFile().getAbsoluteFile().getParent() + "/" + MCI.get("Game.ExeName") + ".exe");
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

	public static void reload() throws IOException {
		if (!loaded || base == null)
			return;
		load0(base, false);
	}

	private static void load0(File base, boolean loadGraphics) throws IOException {
		CSData.base = base;
		dataDir = new File(base.getParent() + "/data");
		mapdata = new Vector<Mapdata>();
		mapInfo = new Vector<MapInfo>();
		imgMap = new HashMap<File, BufferedImage>();
		pxaMap = new HashMap<File, byte[]>();
		fillMapdata();
		loadMapInfo();
		if (loadGraphics)
			loadGraphics();
		loaded = true;
	}

	public static void unload() {
		loaded = false;
		base = null;
		dataDir = null;
		mapdata = null;
		mapInfo = null;
		imgMap = null;
		pxaMap = null;
		myChar = null;
		armsImage = null;
		itemImage = null;
		stageImage = null;
		npcFolder = null;
		npcSym = null;
		System.gc();
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
				// newMap.setNpcSheet1(StrTools.CString(buffer, encoding));
				uBuf.get(buffer, 0, 0x20);
				// newMap.setNpcSheet2(StrTools.CString(buffer, encoding));
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
					// newMap.setNpcSheet1(StrTools.CString(buffer, encoding));
					uBuf.get(buffer, 0, 0x20);
					// newMap.setNpcSheet2(StrTools.CString(buffer, encoding));
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
					// newMap.setNpcSheet1(StrTools.CString(buffer));
					uBuf.get(buffer, 0, 0x20);
					// newMap.setNpcSheet2(StrTools.CString(buffer));
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
			int res = MCI.getInteger("Special.Resolution", 1);
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
		String myCharName = MCI.getNullable("Game.MyChar");
		if (myCharName == null)
			myCharName = "MyChar";
		File myCharFile = ResUtils.getGraphicsFile(dataDir.toString(), myCharName);
		myChar = loadImage(myCharFile);
		String armsImageName = MCI.getNullable("Game.ArmsImage");
		if (armsImageName == null)
			armsImageName = "ArmsImage";
		File armsImageFile = ResUtils.getGraphicsFile(dataDir.toString(), armsImageName);
		armsImage = loadImage(armsImageFile);
		String itemImageName = MCI.getNullable("Game.ItemImage");
		if (itemImageName == null)
			itemImageName = "ItemImage";
		File itemImageFile = ResUtils.getGraphicsFile(dataDir.toString(), itemImageName);
		itemImage = loadImage(itemImageFile);
		String stageImageName = MCI.getNullable("Game.StageImage");
		if (stageImageName == null)
			stageImageName = "StageImage";
		File stageImageFile = ResUtils.getGraphicsFile(dataDir.toString(), stageImageName);
		stageImage = loadImage(stageImageFile);
		String npcFolderName = MCI.getNullable("Game.NpcFolder");
		if (npcFolderName == null)
			npcFolderName = "Npc";
		npcFolder = new File(dataDir.toString() + "/" + npcFolderName);
		String npcSymName = MCI.getNullable("Game.NpcSym");
		if (npcSymName == null)
			npcSymName = "NpcSym";
		File npcSymFile = ResUtils.getGraphicsFile(npcFolder.toString(), npcSymName);
		npcSym = loadImage(npcSymFile);
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

	public static BufferedImage getMyChar() {
		return myChar;
	}

	public static BufferedImage getArmsImage() {
		return armsImage;
	}

	public static BufferedImage getItemImage() {
		return itemImage;
	}

	public static BufferedImage getStageImage() {
		return stageImage;
	}

	public static BufferedImage getNpcSym() {
		return npcSym;
	}

}

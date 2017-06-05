package com.leo.cse.backend;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
/**
 * Stores information for loaded maps.
 * 
 * @author Leo
 *
 */
public class MapInfo {

	/**
	 * Width of the map, in tiles.
	 */
	private int mapX;
	/**
	 * Height of the map, in tiles.
	 */
	private int mapY;
	/**
	 * The map's ID.
	 */
	private int mapNumber;
	/**
	 * The map tiles. <br \> The first index is the layer: 0 for background, 1 for
	 * foreground. <br \> The second and third indexes are X and Y positions,
	 * respectively.
	 */
	protected int[][][] map;
	/**
	 * The map's tileset file.
	 * 
	 * @see ExeData#getImage(File)
	 */
	private File tileset;
	/**
	 * The map's background image file.
	 * 
	 * @see ExeData#getImage(File)
	 */
	private File bgImage;
	/**
	 * The map's file name.
	 */
	private String fileName;
	/**
	 * The map's scroll type.
	 */
	private int scrollType;
	/**
	 * The map's 1st NPC sheet file.
	 * 
	 * @see ExeData#getImage(File)
	 */
	private File npcSheet1;
	/**
	 * The map's 2nd NPC sheet file.
	 * 
	 * @see ExeData#getImage(File)
	 */
	private File npcSheet2;
	/**
	 * The map's name.
	 */
	private String mapName;
	/**
	 * The map's PXA file.
	 * 
	 * @see ExeData#getPxa(File)
	 */
	private File pxaFile;
	/**
	 * List of the map's entities.
	 * 
	 * @see PxeEntry
	 */
	private LinkedList<PxeEntry> pxeList;

	/**
	 * Loads a map and it's resources.
	 * 
	 * @param d
	 *            source map data
	 */
	public MapInfo(Mapdata d) {
		fileName = d.getFileName();
		scrollType = d.getScrollType();
		mapName = d.getMapName();
		File directory = ExeData.getDataDir();
		loadImageResource(d, directory);
		String stage = ExeData.getExeString(ExeData.STRING_STAGE_FOLDER);
		String pxa = ExeData.getExeString(ExeData.STRING_PXA_EXT);
		pxaFile = new File(String.format(pxa, directory + "/" + stage, d.getTileset()));
		ExeData.addPxa(pxaFile);
		loadMap(d);
		getEntities(d);
	}

	/**
	 * Loads image resources for a map.
	 * 
	 * @param d
	 *            source map data
	 * @param directory
	 *            data directory
	 */
	private void loadImageResource(Mapdata d, File directory) {
		// load each image resource
		String stage = ExeData.getExeString(ExeData.STRING_STAGE_FOLDER);
		String npc = ExeData.getExeString(ExeData.STRING_NPC_FOLDER);
		String prt = ExeData.getExeString(ExeData.STRING_PRT_PREFIX);
		String npcP = ExeData.getExeString(ExeData.STRING_NPC_PREFIX);
		tileset = ResUtils.getGraphicsFile(directory.toString(), String.format(prt, stage, d.getTileset()));
		ExeData.addImage(tileset);
		bgImage = ResUtils.getGraphicsFile(directory.toString(), d.getBgName());
		ExeData.addImage(bgImage);
		npcSheet1 = ResUtils.getGraphicsFile(directory.toString(), String.format(npcP, npc, d.getNpcSheet1()));
		ExeData.addImage(npcSheet1);
		npcSheet2 = ResUtils.getGraphicsFile(directory.toString(), String.format(npcP, npc, d.getNpcSheet2()));
		ExeData.addImage(npcSheet2);
	}

	/**
	 * Loads a map.
	 * 
	 * @param d
	 *            source map data
	 */
	protected void loadMap(Mapdata d) {
		// load the map data
		ByteBuffer mapBuf;
		File directory = ExeData.getDataDir();
		String currentFileName = String.format(ExeData.getExeString(ExeData.STRING_PXM_EXT),
				directory + "/" + ExeData.getExeString(ExeData.STRING_STAGE_FOLDER), d.getFileName());
		try {
			File currentFile = ResUtils.newFile(currentFileName);

			if (!currentFile.exists())
				throw new IOException("File \"" + currentFile + "\" does not exist!");

			FileInputStream inStream = new FileInputStream(currentFile);
			FileChannel inChan = inStream.getChannel();
			ByteBuffer hBuf = ByteBuffer.allocate(8);
			hBuf.order(ByteOrder.LITTLE_ENDIAN);
			inChan.read(hBuf);
			// read the filetag
			hBuf.flip();
			byte tagArray[] = new byte[3];
			hBuf.get(tagArray, 0, 3);
			if (!(new String(tagArray).equals(ExeData.getExeString(ExeData.STRING_PXM_TAG)))) {
				inChan.close();
				inStream.close();
				throw new IOException("Bad file tag");
			}
			hBuf.get();
			mapX = hBuf.getShort();
			mapY = hBuf.getShort();
			mapBuf = ByteBuffer.allocate(mapY * mapX);
			mapBuf.order(ByteOrder.LITTLE_ENDIAN);
			inChan.read(mapBuf);
			inChan.close();
			inStream.close();
			mapBuf.flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load PXM:\n" + currentFileName);
			mapX = 21;
			mapY = 16;
			mapBuf = ByteBuffer.allocate(mapY * mapX);
		}
		map = new int[2][mapY][mapX];
		for (int y = 0; y < mapY; y++)
			for (int x = 0; x < mapX; x++) {
				int tile = 0xFF & mapBuf.get();
				if (calcPxa(tile) > 0x20)
					map[1][y][x] = tile;
				else
					map[0][y][x] = tile;
			}
	}

	/**
	 * Calculates a tile's type.
	 * 
	 * @param tileNum
	 *            tile ID
	 * @return tile type
	 */
	public int calcPxa(int tileNum) {
		byte[] pxaData = ExeData.getPxa(pxaFile);
		int rval = 0;
		try {
			rval = pxaData[tileNum];
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("pxa len: " + pxaData.length);
			System.err.println("tile " + tileNum);
		}
		return rval & 0xFF;
	}

	/**
	 * Loads the entities of a map.
	 * 
	 * @param d
	 *            source map data
	 */
	private void getEntities(Mapdata d) {
		pxeList = new LinkedList<>();
		File directory = ExeData.getDataDir();
		String currentFileName = String.format(ExeData.getExeString(ExeData.STRING_PXE_EXT),
				directory + "/" + ExeData.getExeString(ExeData.STRING_STAGE_FOLDER), d.getFileName());
		try {
			File currentFile = ResUtils.newFile(currentFileName);
			if (!currentFile.exists())
				throw new IOException("File \"" + currentFile + "\" does not exist!");
			
			FileInputStream inStream = new FileInputStream(currentFile);
			FileChannel inChan = inStream.getChannel();
			ByteBuffer hBuf = ByteBuffer.allocate(6);
			hBuf.order(ByteOrder.LITTLE_ENDIAN);

			inChan.read(hBuf);
			hBuf.flip();
			int nEnt;
			ByteBuffer eBuf;
			nEnt = hBuf.getShort(4);
			eBuf = ByteBuffer.allocate(nEnt * 12 + 2);
			eBuf.order(ByteOrder.LITTLE_ENDIAN);
			inChan.read(eBuf);
			eBuf.flip();
			eBuf.getShort(); // discard this value
			for (int i = 0; i < nEnt; i++) {
				int pxeX = eBuf.getShort();
				int pxeY = eBuf.getShort();
				int pxeFlagID = eBuf.getShort();
				int pxeEvent = eBuf.getShort();
				int pxeType = eBuf.getShort();
				int pxeFlags = eBuf.getShort() & 0xFFFF;
				PxeEntry p = new PxeEntry(pxeX, pxeY, pxeFlagID, pxeEvent, pxeType, pxeFlags);
				pxeList.add(p);
			}
			inChan.close();
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load PXE:\n" + currentFileName);
			pxeList = null;
		}
	}

	/**
	 * Stores information about an entity.
	 * 
	 * @author Leo
	 *
	 */
	public class PxeEntry {
		/**
		 * The entity's X position, in tiles.
		 */
		private short xTile;

		public int getX() {
			return xTile;
		}

		/**
		 * The entity's Y position, in tiles.
		 */
		private short yTile;

		public int getY() {
			return yTile;
		}

		/**
		 * The entity's flag ID.
		 */
		private short flagID;

		public int getFlagID() {
			return flagID;
		}

		/**
		 * The entity's event number.
		 */
		private short eventNum;

		public int getEvent() {
			return eventNum;
		}

		/**
		 * The entity's type.
		 */
		private short entityType;

		public int getType() {
			return entityType;
		}

		/**
		 * The entity's flags.
		 */
		private short flags;

		public int getFlags() {
			return flags;
		}

		/**
		 * The entity's npc.tbl entry.
		 */
		private EntityData inf;

		public EntityData getInfo() {
			return inf;
		}

		/**
		 * Creates a new entity.
		 * 
		 * @param pxeX
		 *            x position
		 * @param pxeY
		 *            y position
		 * @param pxeFlagID
		 *            flag ID
		 * @param pxeEvent
		 *            event number
		 * @param pxeType
		 *            entity type
		 * @param pxeFlags
		 *            entity flags
		 */
		PxeEntry(int pxeX, int pxeY, int pxeFlagID, int pxeEvent, int pxeType, int pxeFlags) {
			xTile = (short) pxeX;
			yTile = (short) pxeY;
			flagID = (short) pxeFlagID;
			eventNum = (short) pxeEvent;
			entityType = (short) pxeType;
			flags = (short) pxeFlags;

			inf = ExeData.getEntityInfo(entityType);
			if (inf == null)
				throw new NullPointerException("Entity type " + entityType + " is undefined!");
		}

		/**
		 * Draws the entity to the specified graphics instance.
		 * 
		 * @param g
		 *            graphics to draw to
		 */
		public void draw(Graphics2D g) {
			if ((flags & 0x0800) != 0) {
				// Appear once flagID set
				if (!Profile.getFlag(flagID))
					return;
			}
			if ((flags & 0x4000) != 0) {
				// No Appear if flagID set
				if (Profile.getFlag(flagID))
					return;
			}

			Rectangle frameRect = inf.getFramerect();
			BufferedImage srcImg;
			int tilesetNum = inf.getTileset();
			if (tilesetNum == 0x15)
				srcImg = ExeData.getImage(npcSheet1);
			else if (tilesetNum == 0x16)
				srcImg = ExeData.getImage(npcSheet2);
			else if (tilesetNum == 0x14) // npc sym
				srcImg = ExeData.getImage(ExeData.getNpcSym());
			else if (tilesetNum == 0x17) // npc regu
				srcImg = ExeData.getImage(ExeData.getNpcRegu());
			else if (tilesetNum == 0x2) // map tileset
				srcImg = ExeData.getImage(tileset);
			else if (tilesetNum == 0x10) // npc myChar
				srcImg = ExeData.getImage(ExeData.getMyChar());
			else
				srcImg = null;

			if (srcImg != null) {
				int srcX = frameRect.x;
				int srcY = frameRect.y;
				int srcX2 = frameRect.width;
				int srcY2 = frameRect.height;
				Rectangle dest = getDrawArea();
				g.drawImage(srcImg, dest.x, dest.y, dest.x + dest.width, dest.y + dest.height, srcX, srcY, srcX2, srcY2,
						null);
			}
		}

		/**
		 * Gets the entity's draw area.
		 * 
		 * @return draw area
		 */
		public Rectangle getDrawArea() {
			// Rectangle frameRect = inf.getFramerect();
			Rectangle offset;
			if (inf != null) {
				offset = inf.getDisplay();
			} else {
				offset = new Rectangle(16, 16, 16, 16);
			}
			// int width = frameRect.width - frameRect.x, height = frameRect.height -
			// frameRect.y;
			int offL = offset.x;
			int offU = offset.y;
			int offR = offset.width;
			int offD = offset.height;
			int destW = offR + offL;
			destW *= 2;
			// destW = Math.max(destW, width);
			int destH = offD + offU;
			destH *= 2;
			// destH = Math.max(destH, height);
			int destX = xTile * 32 - offL * 2;
			int destY = yTile * 32 - offU * 2;
			Rectangle area = new Rectangle(destX, destY, destW, destH);
			return area;
		}
	}

	public int getMapX() {
		return mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public int getMapNumber() {
		return mapNumber;
	}

	/**
	 * Gets the map tiles. For indexes, see {@link #map}.
	 * 
	 * @return map tiles
	 * @see #map
	 */
	public int[][][] getMap() {
		return map.clone();
	}

	public File getTileset() {
		return tileset;
	}

	public File getBgImage() {
		return bgImage;
	}

	public String getFileName() {
		return fileName;
	}

	public int getScrollType() {
		return scrollType;
	}

	public File getNpcSheet1() {
		return npcSheet1;
	}

	public File getNpcSheet2() {
		return npcSheet2;
	}

	public String getMapName() {
		return mapName;
	}

	public File getPxaFile() {
		return pxaFile;
	}

	/**
	 * Gets an iterator over the map's entities.
	 * 
	 * @return iterator over elements of entity list
	 * @see #pxeList
	 */
	public Iterator<PxeEntry> getPxeIterator() {
		return pxeList.iterator();
	}

	/**
	 * Checks if there are missing assets.
	 * 
	 * @return <code>true</code> if there are missing assets, <code>false</code>
	 *         otherwise.
	 */
	public boolean hasMissingAssets() {
		return ExeData.getImage(tileset) == null || ExeData.getImage(bgImage) == null
				|| ExeData.getImage(npcSheet1) == null || ExeData.getImage(npcSheet2) == null || pxeList == null;
	}

	/**
	 * Returns a readable list of missing assets. Useful for reporting errors.
	 * 
	 * @return list of missing assets, or an empty string if there are no missing
	 *         assets
	 */
	public String getMissingAssets() {
		if (!hasMissingAssets())
			return "";
		final String[] assetName = new String[] { "tileset", "background image", "NPC sheet 1", "NPC sheet 2", "PXE file" };
		final boolean[] assetStat = new boolean[] { ExeData.getImage(tileset) == null,
				ExeData.getImage(bgImage) == null, ExeData.getImage(npcSheet1) == null,
				ExeData.getImage(npcSheet2) == null, pxeList == null };
		assert (assetName.length == assetStat.length);
		String ret = "";
		for (int i = 0; i < assetStat.length; i++)
			if (assetStat[i])
				ret += assetName[i] + ", ";
		if ("".equals(ret))
			return ret;
		ret = ret.substring(0, 1).toUpperCase() + ret.substring(1, ret.length());
		return ret.substring(0, ret.length() - 2);
	}

}

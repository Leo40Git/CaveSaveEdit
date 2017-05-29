package com.leo.cse.frontend.data;

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

import com.leo.cse.backend.Profile;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
public class MapInfo {

	private int mapX;
	private int mapY;
	private int mapNumber;
	protected int[][][] map;
	private File tileset;
	private File bgImage;
	private String fileName;
	private int scrollType;
	private File npcSheet1;
	private File npcSheet2;
	private String mapName;
	private File pxaFile;
	private LinkedList<PxeEntry> pxeList;

	public Iterator<PxeEntry> getPxeIterator() {
		return pxeList.iterator();
	}

	public MapInfo(Mapdata d) {
		fileName = d.getFileName();
		scrollType = d.getScrollType();
		mapName = d.getMapName();
		File directory = ExeData.getDataDir();
		loadImageResource(d, directory);
		String stage = ExeData.getExeString(ExeData.STRING_STAGE_FOLDER);
		String pxa = ExeData.getExeString(ExeData.STRING_PXA_EXT);
		pxaFile = new File(String.format(pxa, directory + "/" + stage, d.getTileset()));
		ExeData.addPxa(pxaFile, 256);
		loadMap(d);
		getEntities(d);
	}

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

	protected void loadMap(Mapdata d) {
		// load the map data
		ByteBuffer mapBuf;
		File directory = ExeData.getDataDir();
		String currentFileName = String.format(ExeData.getExeString(ExeData.STRING_PXM_EXT),
				directory + "/" + ExeData.getExeString(ExeData.STRING_STAGE_FOLDER), d.getFileName());
		try {
			File currentFile = new File(currentFileName);

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

	private void getEntities(Mapdata d) {
		pxeList = new LinkedList<>();
		File directory = ExeData.getDataDir();
		String currentFileName = String.format(ExeData.getExeString(ExeData.STRING_PXE_EXT),
				directory + "/" + ExeData.getExeString(ExeData.STRING_STAGE_FOLDER), d.getFileName());
		try {
			File currentFile = new File(currentFileName);
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
				PxeEntry p = new PxeEntry(pxeX, pxeY, pxeFlagID, pxeEvent, pxeType, pxeFlags, 1);
				pxeList.add(p);
			}
			inChan.close();
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load PXE:\n" + currentFileName);
		}
	}

	public class PxeEntry {
		private short xTile;

		public int getX() {
			return xTile;
		}

		private short yTile;

		public int getY() {
			return yTile;
		}

		private short flagID;

		public int getFlagID() {
			return flagID;
		}

		private short eventNum;

		public int getEvent() {
			return eventNum;
		}

		private short entityType;

		public int getType() {
			return entityType;
		}

		// set method below
		private short flags;

		public int getFlags() {
			return flags;
		}

		private EntityData inf;

		public EntityData getInfo() {
			return inf;
		}

		PxeEntry(int pxeX, int pxeY, int pxeFlagID, int pxeEvent, int pxeType, int pxeFlags, int pxeLayer) {
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

		public void draw(Graphics2D g2d) {
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
				srcImg = ExeData.getImg(npcSheet1);
			else if (tilesetNum == 0x16)
				srcImg = ExeData.getImg(npcSheet2);
			else if (tilesetNum == 0x14) // npc sym
				srcImg = ExeData.getImg(ExeData.getNpcSym());
			else if (tilesetNum == 0x17) // npc regu
				srcImg = ExeData.getImg(ExeData.getNpcRegu());
			else if (tilesetNum == 0x2) // map tileset
				srcImg = ExeData.getImg(tileset);
			else if (tilesetNum == 0x10) // npc myChar
				srcImg = ExeData.getImg(ExeData.getMyChar());
			else
				srcImg = null;

			if (srcImg != null) {
				int srcX = frameRect.x;
				int srcY = frameRect.y;
				int srcX2 = frameRect.width;
				int srcY2 = frameRect.height;
				Rectangle dest = getDrawArea(frameRect);
				g2d.drawImage(srcImg, dest.x, dest.y, dest.x + dest.width, dest.y + dest.height, srcX, srcY, srcX2,
						srcY2, null);
			}
		}

		public Rectangle getDrawArea(Rectangle frameRect) {
			Rectangle offset;
			if (inf != null) {
				offset = inf.getDisplay();
			} else {
				offset = new Rectangle(16, 16, 16, 16);
			}
			int offL = offset.x;
			int offU = offset.y;
			int offR = offset.width;
			int offD = offset.height;
			int destW = offR + offL;
			destW *= 2;
			destW = Math.max(destW, frameRect.width - frameRect.x);
			int destH = offD + offU;
			destH *= 2;
			destH = Math.max(destH, frameRect.height - frameRect.y);
			int destX = xTile * 16 - offL;
			destX *= 2;
			int destY = yTile * 16 - offU;
			destY *= 2;

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

}

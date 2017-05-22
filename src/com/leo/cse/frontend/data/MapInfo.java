package com.leo.cse.frontend.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class MapInfo {

	private int mapX;
	private int mapY;
	private int mapNumber;
	protected int[][][] map;
	private File tileset;
	private File bgImage;
	private int scrollType;
	private File pxaFile;

	public MapInfo(Mapdata d) {
		scrollType = d.getScrollType();
		File directory = CSData.getDataDir();
		loadImageResource(d, directory);
		pxaFile = new File(directory + "/Stage/" + d.getTileset() + ".pxa");
		CSData.addPxa(pxaFile, 256);
		loadMap(d);
	}

	private void loadImageResource(Mapdata d, File directory) {
		// load each image resource
		tileset = ResUtils.getGraphicsFile(directory + "/Stage", "Prt" + d.getTileset());
		CSData.addImage(tileset);
		bgImage = ResUtils.getGraphicsFile(directory.toString(), d.getBgName());
		CSData.addImage(bgImage);
	}

	protected void loadMap(Mapdata d) {
		// load the map data
		ByteBuffer mapBuf;
		File directory = CSData.getDataDir();
		try {
			File currentFile;
			currentFile = new File(directory + "/Stage/" + d.getFileName() + ".pxm"); //$NON-NLS-1$ //$NON-NLS-2$

			if (!currentFile.exists())
				writeDummyPxm(currentFile);

			FileInputStream inStream = new FileInputStream(currentFile);
			FileChannel inChan = inStream.getChannel();
			ByteBuffer hBuf = ByteBuffer.allocate(8);
			hBuf.order(ByteOrder.LITTLE_ENDIAN);
			inChan.read(hBuf);
			// read the filetag
			hBuf.flip();
			byte tagArray[] = new byte[3];
			hBuf.get(tagArray, 0, 3);
			if (!(new String(tagArray).equals("PXM"))) {
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
			System.err.println("Failed to load PXM:\n" + directory + "/Stage/" + d.getFileName() + ".pxm");
			mapX = 21;
			mapY = 16;
			mapBuf = ByteBuffer.allocate(mapY * mapX);
		}
		map = new int[4][mapY][mapX];
		for (int y = 0; y < mapY; y++)
			for (int x = 0; x < mapX; x++) {
				int next = 0xFF & mapBuf.get();
				if (calcPxa(next) < 0x20)
					map[1][y][x] = next;
				else
					map[2][y][x] = next;
			}
	}

	private void writeDummyPxm(File currentFile) throws IOException {
		FileOutputStream out = new FileOutputStream(currentFile);
		FileChannel chan = out.getChannel();
		byte[] pxmTag = { 'P', 'X', 'M', 0x10 };
		ByteBuffer mapBuf;
		mapBuf = ByteBuffer.allocate(21 * 16 + 4);
		mapBuf.order(ByteOrder.LITTLE_ENDIAN);
		mapBuf.putShort(0, (short) 21);
		mapBuf.putShort(2, (short) 16);
		ByteBuffer tagBuf = ByteBuffer.wrap(pxmTag);
		chan.write(tagBuf);
		chan.write(mapBuf);
		out.close();
		currentFile.deleteOnExit();
	}

	public int calcPxa(int tileNum) {
		byte[] pxaData = CSData.getPxa(pxaFile);
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

	public int getScrollType() {
		return scrollType;
	}

	public File getPxaFile() {
		return pxaFile;
	}

}

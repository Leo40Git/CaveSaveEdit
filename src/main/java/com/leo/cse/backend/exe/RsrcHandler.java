package com.leo.cse.backend.exe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import com.leo.cse.frontend.Main;

public class RsrcHandler {

	public RsrcEntry root;

	public static class RsrcEntry {
		public RsrcEntry parent; // if this is null, this entry is the root directory
		public String name; // each char is short-sized
		public int id; // if name is null, use this instead
		public byte[] data; // if null, this is a directory...
		public LinkedList<RsrcEntry> entries; // ...which means this contains its entries
		// for data entries:
		public int dataCodepage;
		public int dataReserved;
		// for directories:
		public int dirCharacteristics;
		public int dirTimestamp;
		public short dirVerMajor, dirVerMinor;

		public boolean isDirectory() {
			return data == null;
		}

		public RsrcEntry getSubEntry(String name) {
			if (!isDirectory())
				return null;
			for (RsrcEntry entry : entries) {
				if (entry.name == null)
					continue;
				if (entry.name.equals(name))
					return entry;
			}
			return null;
		}

		public boolean hasSubEntry(String name) {
			return getSubEntry(name) != null;
		}

		public RsrcEntry getSubEntry(int id) {
			if (!isDirectory())
				return null;
			for (RsrcEntry entry : entries) {
				if (entry.name != null)
					continue;
				if (entry.id == id)
					return entry;
			}
			return null;
		}

		public boolean hasSubEntry(int id) {
			return getSubEntry(id) != null;
		}

	}

	public RsrcHandler(PEFile.Section rsrcSec) {
		rsrcSec.shiftResourceContents(-rsrcSec.virtualAddrRelative);
		root = new RsrcEntry();
		root.entries = new LinkedList<>();
		ByteBuffer data = ByteBuffer.wrap(rsrcSec.rawData);
		data.order(ByteOrder.LITTLE_ENDIAN);
		readDirectory(data, root);
		rsrcSec.shiftResourceContents(rsrcSec.virtualAddrRelative);
	}

	private void readDirectory(ByteBuffer data, RsrcEntry root) {
		int posStorage = 0;
		root.dirCharacteristics = data.getInt();
		root.dirTimestamp = data.getInt();
		root.dirVerMajor = data.getShort();
		root.dirVerMinor = data.getShort();
		int entries = data.getShort();
		entries += data.getShort();
		Main.LOGGER.trace("reading " + entries + " entries");
		for (int i = 0; i < entries; i++) {
			RsrcEntry entry = new RsrcEntry();
			entry.parent = root;
			int nameOffset = data.getInt();
			if ((nameOffset & 0x80000000) == 0) {
				// id
				entry.id = nameOffset;
			} else {
				// name
				posStorage = data.position();
				nameOffset &= 0x7FFFFFFF;
				Main.LOGGER.trace("nameOffset=0x" + Integer.toHexString(nameOffset).toUpperCase());
				Main.LOGGER.trace("data.limit()=0x" + Integer.toHexString(data.limit()).toUpperCase());
				data.position(nameOffset);
				int nameLen = data.getShort();
				char[] nameBuf = new char[nameLen];
				for (int j = 0; j < nameLen; j++) {
					nameBuf[j] = (char) data.getShort();
				}
				entry.name = new String(nameBuf, 0, nameLen);
				Main.LOGGER.trace("entry.name=" + entry.name);
				data.position(posStorage);
			}
			handleEntryData(data, entry);
			root.entries.add(entry);
		}
	}

	private void handleEntryData(ByteBuffer data, RsrcEntry entry) {
		int dataOffset = data.getInt();
		int posStorage = data.position();
		if ((dataOffset & 0x80000000) == 0) {
			// data
			Main.LOGGER.trace("DATA,dataOffset=0x" + Integer.toHexString(dataOffset).toUpperCase());
			data.position(dataOffset);
			int dataPos = data.getInt();
			int dataSize = data.getInt();
			entry.dataCodepage = data.getInt();
			entry.dataReserved = data.getInt();
			// start reading data
			data.position(dataPos);
			byte[] entryData = new byte[dataSize];
			data.get(entryData);
			entry.data = entryData;
		} else {
			// subdirectory
			dataOffset &= 0x7FFFFFFF;
			Main.LOGGER.trace("SUBDIR,dataOffset=0x" + Integer.toHexString(dataOffset).toUpperCase());
			entry.entries = new LinkedList<>();
			data.position(dataOffset);
			readDirectory(data, entry);
		}
		data.position(posStorage);
	}

	public byte[] getBitmapData(String name, int preferredLangId) {
		// get bitmap directory (ID 2)
		RsrcEntry bmpDir = root.getSubEntry(2);
		if (bmpDir == null)
			// exe does not have embedded bitmaps
			return null;
		// get bitmap entry
		RsrcEntry bmpEnt = bmpDir.getSubEntry(name);
		if (bmpEnt == null)
			// bitmap does not exist
			return null;
		LinkedList<RsrcEntry> bmpEntSub = bmpEnt.entries;
		if (bmpEntSub.size() == 0)
			// stub?
			return null;
		else if (bmpEntSub.size() == 1 || preferredLangId < 0)
			// only one entry OR no preferred language ID
			return bmpEnt.entries.get(0).data;
		else {
			// more than one entry
			// use preferredLangId to grab the correct entry
			RsrcEntry target = bmpEnt.getSubEntry(preferredLangId);
			if (target == null)
				// preferred lang ID not found
				return null;
			return target.data;
		}
	}

}

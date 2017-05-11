package com.leo.cse.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Profile {

	private Profile() {
	}

	public static final int FILE_LENGTH = 1540;
	public static final String HEADER = "Do041220";
	public static final String FLAG = "FLAG";

	public static class Weapon {

		public static final int BASE_POINTER = 0x38;

		private int id;
		private int level;
		private int exp;
		private int maxAmmo;
		private int curAmmo;

		public static int getPointer(int slot) {
			return BASE_POINTER + slot * (5 * ByteUtils.INT_SIZE);
		}

		public Weapon(byte[] data, int slot) {
			final int ptr = getPointer(slot);
			id = ByteUtils.readInt(data, ptr);
			level = ByteUtils.readInt(data, ptr + ByteUtils.INT_SIZE);
			exp = ByteUtils.readInt(data, ptr + ByteUtils.INT_SIZE * 2);
			maxAmmo = ByteUtils.readInt(data, ptr + ByteUtils.INT_SIZE * 3);
			curAmmo = ByteUtils.readInt(data, ptr + ByteUtils.INT_SIZE * 4);
		}

		public void save(byte[] data, int slot) {
			final int ptr = getPointer(slot);
			ByteUtils.writeInt(data, ptr, id);
			ByteUtils.writeInt(data, ptr + ByteUtils.INT_SIZE, level);
			ByteUtils.writeInt(data, ptr + ByteUtils.INT_SIZE * 2, exp);
			ByteUtils.writeInt(data, ptr + ByteUtils.INT_SIZE * 3, maxAmmo);
			ByteUtils.writeInt(data, ptr + ByteUtils.INT_SIZE * 4, curAmmo);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getExp() {
			return exp;
		}

		public void setExp(int exp) {
			this.exp = exp;
		}

		public int getMaxAmmo() {
			return maxAmmo;
		}

		public void setMaxAmmo(int maxAmmo) {
			this.maxAmmo = maxAmmo;
		}

		public int getCurrentAmmo() {
			return curAmmo;
		}

		public void setCurrentAmmo(int curAmmo) {
			this.curAmmo = curAmmo;
		}

		public String toString(int indent) {
			String in = "";
			for (int i = 0; i < indent; i++) {
				in += " ";
			}
			String ret = "";
			ret += in + "ID: " + id;
			ret += "\n" + in + "Level: " + level;
			ret += "\n" + in + "Extra EXP: " + exp;
			ret += "\n" + in + "Ammo: " + curAmmo + "/" + maxAmmo;
			return ret;
		}

		@Override
		public String toString() {
			return toString(0);
		}

	}

	public static class Teleporter {

		public static final int BASE_POINTER = 0x158;

		private int id;
		private int location;

		public static int getPointer(int slot) {
			return BASE_POINTER + slot * (2 * ByteUtils.INT_SIZE);
		}

		public Teleporter(byte[] data, int slot) {
			final int ptr = getPointer(slot);
			id = ByteUtils.readInt(data, ptr);
			location = ByteUtils.readInt(data, ptr + ByteUtils.INT_SIZE);
		}

		public void save(byte[] data, int slot) {
			final int ptr = getPointer(slot);
			ByteUtils.writeInt(data, ptr, id);
			ByteUtils.writeInt(data, ptr + ByteUtils.INT_SIZE, location);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getLocation() {
			return location;
		}

		public void setLocation(int location) {
			this.location = location;
		}

		public String toString(int indent) {
			String in = "";
			for (int i = 0; i < indent; i++) {
				in += " ";
			}
			String ret = "";
			ret += in + "ID: " + id;
			ret += "\n" + in + "Location: " + location;
			return ret;
		}

		@Override
		public String toString() {
			return toString(0);
		}

	}
	
	private static File file = null;
	private static boolean loaded = false;
	// reference for pointers: http://www.cavestory.org/guides/profile.txt
	private static int map = 0;
	private static int song = 0;
	private static short x = 0, y = 0;
	private static int direction = 0;
	private static short maxHealth = 0;
	private static short starCount = 0;
	private static short curHealth = 0;
	private static int curWeapon = 0;
	private static boolean[] equips = null;
	private static int time = 0;
	private static Weapon[] weapons = null;
	private static int[] items = null;
	private static Teleporter[] tele = null;
	private static boolean[] flags = null;

	public static void read(File file) throws IOException {
		Profile.file = file;
		// read data
		byte[] data = new byte[FILE_LENGTH];
		try (FileInputStream fis = new FileInputStream(file)) {
			if(fis.read(data) != data.length)
				throw new IOException("file is too small");
		}
		// check header
		String header = ByteUtils.readString(data, 0x00, HEADER.length());
		if (!HEADER.equals(header))
			throw new IOException("invalid file header");
		map = ByteUtils.readInt(data, 0x08);
		song = ByteUtils.readInt(data, 0x0C);
		x = ByteUtils.readShort(data, 0x11);
		y = ByteUtils.readShort(data, 0x15);
		direction = ByteUtils.readInt(data, 0x18);
		maxHealth = ByteUtils.readShort(data, 0x1C);
		starCount = ByteUtils.readShort(data, 0x1E);
		curHealth = ByteUtils.readShort(data, 0x20);
		curWeapon = ByteUtils.readInt(data, 0x24);
		equips = new boolean[ByteUtils.SHORT_SIZE * 8];
		ByteUtils.readFlags(data, 0x2C, equips);
		time = ByteUtils.readInt(data, 0x34);
		weapons = new Weapon[5];
		for (int i = 0; i < weapons.length; i++) {
			weapons[i] = new Weapon(data, i);
		}
		items = new int[30];
		ByteUtils.readInts(data, 0xD8, items);
		tele = new Teleporter[7];
		for (int i = 0; i < tele.length; i++) {
			tele[i] = new Teleporter(data, i);
		}
		String flag = ByteUtils.readString(data, 0x218, FLAG.length());
		if (!FLAG.equals(flag)) {
			throw new IOException("Flag header is missing!");
		}
		flags = new boolean[8000];
		ByteUtils.readFlags(data, 0x21C, flags);
		loaded = true;
	}

	public static void read(String path) throws IOException {
		read(new File(path));
	}

	public static void write() throws IOException {
		if (!loaded)
			return;
		// back up file just in case
		File backup = new File(file.getAbsolutePath() + ".bkp");
		if (backup.exists()) {
			backup.delete();
		}
		backup.createNewFile();
		try (FileOutputStream fos = new FileOutputStream(backup); FileInputStream fis = new FileInputStream(file)) {
			byte[] data = new byte[FILE_LENGTH];
			fis.read(data);
			fos.write(data);
		}
		// start writing
		try (FileOutputStream fos = new FileOutputStream(file)) {
			byte[] data = new byte[FILE_LENGTH];
			ByteUtils.writeString(data, 0x00, HEADER);
			ByteUtils.writeInt(data, 0x08, map);
			ByteUtils.writeInt(data, 0x0C, song);
			ByteUtils.writeInt(data, 0x11, x);
			ByteUtils.writeInt(data, 0x15, y);
			ByteUtils.writeInt(data, 0x18, direction);
			ByteUtils.writeShort(data, 0x1C, maxHealth);
			ByteUtils.writeShort(data, 0x1E, starCount);
			ByteUtils.writeShort(data, 0x20, curHealth);
			ByteUtils.writeInt(data, 0x24, curWeapon);
			ByteUtils.writeFlags(data, 0x2C, equips);
			ByteUtils.writeInt(data, 0x34, time);
			for (int i = 0; i < weapons.length; i++) {
				weapons[i].save(data, i);
			}
			ByteUtils.writeInts(data, 0xD8, items);
			for (int i = 0; i < tele.length; i++) {
				tele[i].save(data, i);
			}
			ByteUtils.writeString(data, 0x218, FLAG);
			ByteUtils.writeFlags(data, 0x21C, flags);
			fos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while saving profile! Recovering backup.");
			try (FileOutputStream fos = new FileOutputStream(file); FileInputStream fis = new FileInputStream(backup)) {
				byte[] data = new byte[FILE_LENGTH];
				fis.read(data);
				fos.write(data);
			}
		}
	}
	
	public static boolean isLoaded() {
		return loaded;
	}

	public static int getMap() {
		return map;
	}

	public static void setMap(int map) {
		Profile.map = map;
	}

	public static int getSong() {
		return song;
	}

	public static void setSong(int song) {
		Profile.song = song;
	}

	public static short getX() {
		return x;
	}

	public static void setX(short x) {
		Profile.x = x;
	}

	public static short getY() {
		return y;
	}

	public static void setY(short y) {
		Profile.y = y;
	}

	public static int getDirection() {
		return direction;
	}

	public static void setDirection(int direction) {
		Profile.direction = direction;
	}

	public static short getMaxHealth() {
		return maxHealth;
	}

	public static void setMaxHealth(short maxHealth) {
		Profile.maxHealth = maxHealth;
	}

	public static short getStarCount() {
		return starCount;
	}

	public static void setStarCount(short starCount) {
		Profile.starCount = starCount;
	}

	public static short getCurHealth() {
		return curHealth;
	}

	public static void setCurHealth(short curHealth) {
		Profile.curHealth = curHealth;
	}

	public static int getCurWeapon() {
		return curWeapon;
	}

	public static void setCurWeapon(int curWeapon) {
		Profile.curWeapon = curWeapon;
	}

	public static boolean[] getEquips() {
		return equips;
	}

	public static boolean getEquip(int id) {
		return equips[id];
	}

	public static void setEquip(int id, boolean equipped) {
		equips[id] = equipped;
	}

	public static int getTime() {
		return time;
	}

	public static void setTime(int time) {
		Profile.time = time;
	}

	public static Weapon getWeapon(int id) {
		return weapons[id];
	}

	public static int[] getItems() {
		return items;
	}

	public static int getItem(int id) {
		return items[id];
	}

	public static void setItem(int id, int value) {
		items[id] = value;
	}

	public static Teleporter getTeleporter(int id) {
		return tele[id];
	}

	public static boolean[] getFlags() {
		return flags;
	}

	public static boolean getFlag(int id) {
		if (flags == null)
			return false;
		return flags[id];
	}

	public static void setFlag(int id, boolean set) {
		if (flags == null)
			return;
		flags[id] = set;
	}

	public static String dumpData() {
		String ret = "";
		ret += "Map: " + map;
		ret += "\nSong: " + song;
		ret += "\nPosition: (" + x + ", " + y + ")";
		ret += "\nFacing Direction: " + direction;
		ret += "\nHealth: " + curHealth + "/" + maxHealth;
		ret += "\nWhimsical Star Count: " + starCount;
		ret += "\nSelected Weapon Slot: " + curWeapon;
		ret += "\nTime Played: " + time;
		ret += "\nEquipment: [\n  ";
		for (int i = 0; i < equips.length; i++) {
			ret += i + ": ";
			ret += (equips[i] ? "" : "Not ") + "Equipped";
			if (i != equips.length - 1) {
				ret += ",\n  ";
			}
		}
		ret += "\n]";
		ret += "\nWeapons: [";
		for (int i = 0; i < weapons.length; i++) {
			ret += "\n  " + i + ": {\n" + weapons[i].toString(4) + "\n  }";
			if (i != weapons.length - 1) {
				ret += ",";
			}
		}
		ret += "\n]";
		ret += "\nItems: [\n  ";
		int ic = 0;
		for (int i = 0; i < items.length; i++) {
			ret += items[i];
			if (i != items.length - 1) {
				ret += ", ";
			}
			ic++;
			if (ic > 7) {
				ret += "\n  ";
				ic = 0;
			}
		}
		ret += "\n]";
		ret += "\nTeleporter Slots: [";
		for (int i = 0; i < tele.length; i++) {
			ret += "\n  " + i + ": {\n" + tele[i].toString(4) + "\n  }";
			if (i != tele.length - 1) {
				ret += ",";
			}
		}
		ret += "\n]";
		ret += "\nFlags: [\n  ";
		int fc = 0;
		for (int i = 0; i < flags.length; i++) {
			ret += (flags[i] ? "T" : "F") + ",";
			fc++;
			if (fc > 50) {
				ret += "\n  ";
				fc = 0;
			}
		}
		ret += "\n]";
		return ret;
	}

}

package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.backend.ByteUtils;

/**
 * The interface class between a Java application and a Cave Story profile.
 * 
 * @author Leo
 *
 */
public class Profile {

	/**
	 * Used to notify {@link ProfileChangeListener}s of the current profile being
	 * saved.
	 */
	public static final String EVENT_SAVE = "save";
	/**
	 * Used to notify {@link ProfileChangeListener}s of a new profile being loaded.
	 */
	public static final String EVENT_LOAD = "load";
	/**
	 * Map field.
	 * 
	 * @see #map
	 */
	public static final String FIELD_MAP = "map";
	/**
	 * Song field.
	 * 
	 * @see #song
	 */
	public static final String FIELD_SONG = "song";
	/**
	 * Position field.
	 * 
	 * @see #x
	 * @see #y
	 */
	public static final String FIELD_POSITION = "position";
	/**
	 * X position field.
	 * 
	 * @see #x
	 */
	public static final String FIELD_X_POSITION = "position_x";
	/**
	 * Y position field.
	 * 
	 * @see #y
	 */
	public static final String FIELD_Y_POSITION = "position_y";
	/**
	 * Direction field.
	 * 
	 * @see #direction
	 */
	public static final String FIELD_DIRECTION = "direction";
	/**
	 * Maximum health field.
	 * 
	 * @see #maxHealth
	 */
	public static final String FIELD_MAXIMUM_HEALTH = "maxHealth";
	/**
	 * Whimsical Star count field.
	 * 
	 * @see #starCount
	 */
	public static final String FIELD_STAR_COUNT = "starCount";
	/**
	 * Current health field.
	 * 
	 * @see #curHealth
	 */
	public static final String FIELD_CURRENT_HEALTH = "curHealth";
	/**
	 * Current weapon slot field.
	 * 
	 * @see #curWeapon
	 */
	public static final String FIELD_CURRENT_WEAPON = "curWeapon";
	/**
	 * Equipment flags field.
	 * 
	 * @see #equips
	 */
	public static final String FIELD_EQUIPS = "equips[%d]";
	/**
	 * Time played field.
	 * 
	 * @see #time
	 */
	public static final String FIELD_TIME_PLAYED = "time";
	/**
	 * Weapon ID field.
	 * 
	 * @see Weapon#id
	 */
	public static final String FIELD_WEAPON_ID = "weapons[%d].id";
	/**
	 * Weapon level field.
	 * 
	 * @see Weapon#level
	 */
	public static final String FIELD_WEAPON_LEVEL = "weapons[%d].level";
	/**
	 * Weapon EXP field.
	 * 
	 * @see Weapon#exp
	 */
	public static final String FIELD_WEAPON_EXP = "weapons[%d].exp";
	/**
	 * Weapon maximum ammo field.
	 * 
	 * @see Weapon#maxAmmo
	 */
	public static final String FIELD_WEAPON_MAXIMUM_AMMO = "weapons[%d].maxAmmo";
	/**
	 * Weapon current ammo field.
	 * 
	 * @see Weapon#curAmmo
	 */
	public static final String FIELD_WEAPON_CURRENT_AMMO = "weapons[%d].curAmmo";
	/**
	 * Items field.
	 * 
	 * @see #items
	 */
	public static final String FIELD_ITEMS = "items[%d]";
	/**
	 * Warp ID field.
	 * 
	 * @see Warp#id
	 */
	public static final String FIELD_WARP_ID = "warps[%d].id";
	/**
	 * Warp location field.
	 * 
	 * @see Warp#location
	 */
	public static final String FIELD_WARP_LOCATION = "warps[%d].location";
	/**
	 * Flags field.
	 * 
	 * @see #flags
	 */
	public static final String FIELD_FLAGS = "flags[%d]";
	/**
	 * <MIM costume "field". A field for this doesn't actually exist.
	 * 
	 * @see #getMimCostume()
	 */
	public static final String FIELD_MIM_COSTUME = "mimCostume";
	/**
	 * Variables "field". A field for this doesn't actually exist.
	 * 
	 * @see #getVariable(int)
	 */
	public static final String FIELD_VARIABLES = "variables[%d]";
	/**
	 * Physics variables "field". A field for this doesn't actually exist.
	 * 
	 * @see #getPhysVariable(int)
	 */
	public static final String FIELD_PHYSICS_VARIABLES = "physVars[%d]";
	/**
	 * Amount of cash "field". A field for this doesn't actually exist.
	 * 
	 * @see #getCash()
	 */
	public static final String FIELD_CASH = "cash";

	/**
	 * Make sure an instance of this class cannot be created.
	 */
	private Profile() {
	}

	/**
	 * The expected file length.
	 */
	public static final int FILE_LENGTH = 0x604;
	/**
	 * The default profile header string.
	 */
	public static final String DEFAULT_HEADER = "Do041220";
	/**
	 * The default flag section header string.
	 */
	public static final String DEFAULT_FLAGH = "FLAG";
	/**
	 * The profile header string.
	 */
	public static String header = DEFAULT_HEADER;
	/**
	 * The flag section header string.
	 */
	public static String flagH = DEFAULT_FLAGH;

	/**
	 * Structural class for weapons.
	 * 
	 * @author Leo
	 *
	 */
	public static class Weapon {

		/**
		 * Starting position for weapon data.
		 */
		public static final int BASE_POINTER = 0x038;

		/**
		 * The weapon's slot.
		 */
		private int slot;
		/**
		 * The weapon's type.
		 */
		private int id;
		/**
		 * The weapon's level.
		 */
		private int level;
		/**
		 * The weapon's extra EXP.
		 */
		private int exp;
		/**
		 * The weapon's maximum ammo capacity.
		 */
		private int maxAmmo;
		/**
		 * The weapon's current ammo amount.
		 */
		private int curAmmo;

		/**
		 * Gets the starting position for a weapon slot.
		 * 
		 * @param slot
		 *            weapon slot
		 * @return starting position
		 */
		public static int getPointer(int slot) {
			return BASE_POINTER + slot * (5 * Integer.BYTES);
		}

		/**
		 * Creates a new weapon based on data from a byte array.
		 * 
		 * @param data
		 *            byte array
		 * @param slot
		 *            weapon slot
		 */
		public Weapon(byte[] data, int slot) {
			this.slot = slot;
			final int ptr = getPointer(slot);
			setId(ByteUtils.readInt(data, ptr));
			setLevel(ByteUtils.readInt(data, ptr + Integer.BYTES));
			setExp(ByteUtils.readInt(data, ptr + Integer.BYTES * 2));
			setMaxAmmo(ByteUtils.readInt(data, ptr + Integer.BYTES * 3));
			setCurAmmo(ByteUtils.readInt(data, ptr + Integer.BYTES * 4));
		}

		/**
		 * Writes the weapon data to a byte array.
		 * 
		 * @param data
		 *            byte array
		 * @param slot
		 *            weapon slot
		 */
		public void write(byte[] data) {
			final int ptr = getPointer(slot);
			ByteUtils.writeInt(data, ptr, id);
			ByteUtils.writeInt(data, ptr + Integer.BYTES, level);
			ByteUtils.writeInt(data, ptr + Integer.BYTES * 2, exp);
			ByteUtils.writeInt(data, ptr + Integer.BYTES * 3, maxAmmo);
			ByteUtils.writeInt(data, ptr + Integer.BYTES * 4, curAmmo);
		}

		/**
		 * Gets the weapon's ID.
		 * 
		 * @return weapon id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the weapon's ID.
		 * 
		 * @param id
		 *            new weapon id
		 */
		public void setId(int id) {
			if (this.id != id)
				notifyListeners(FIELD_WEAPON_ID, slot, this.id, id);
			this.id = id;
		}

		/**
		 * Gets the weapon's level.
		 * 
		 * @return weapon level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Sets the weapon's level.
		 * 
		 * @param level
		 *            new weapon level
		 */
		public void setLevel(int level) {
			if (this.level != level)
				notifyListeners(FIELD_WEAPON_LEVEL, slot, this.level, level);
			this.level = level;
		}

		/**
		 * Gets the weapon's extra EXP.
		 * 
		 * @return weapon EXP
		 */
		public int getExp() {
			return exp;
		}

		/**
		 * Sets the weapon's extra EXP.
		 * 
		 * @param id
		 *            new weapon EXP
		 */
		public void setExp(int exp) {
			if (this.exp != exp)
				notifyListeners(FIELD_WEAPON_EXP, slot, this.exp, exp);
			this.exp = exp;
		}

		/**
		 * Gets the weapon's max ammo capacity.
		 * 
		 * @return weapon max ammo
		 */
		public int getMaxAmmo() {
			return maxAmmo;
		}

		/**
		 * Sets the weapon's max ammo capacity.
		 * 
		 * @param maxAmmo
		 *            new weapon max ammo
		 */
		public void setMaxAmmo(int maxAmmo) {
			if (this.maxAmmo != maxAmmo)
				notifyListeners(FIELD_WEAPON_MAXIMUM_AMMO, slot, this.maxAmmo, maxAmmo);
			this.maxAmmo = maxAmmo;
		}

		/**
		 * Gets the weapon's current ammo capacity.
		 * 
		 * @return weapon current ammo
		 */
		public int getCurAmmo() {
			return curAmmo;
		}

		/**
		 * Sets the weapon's max ammo capacity.
		 * 
		 * @param curAmmo
		 *            new weapon current ammo
		 */
		public void setCurAmmo(int curAmmo) {
			if (this.curAmmo != curAmmo)
				notifyListeners(FIELD_WEAPON_CURRENT_AMMO, slot, this.curAmmo, curAmmo);
			this.curAmmo = curAmmo;
		}

		/**
		 * Returns a string representation of the object.
		 * 
		 * @param indent
		 *            amount of spaces to indent with
		 * @return a string representation of the object
		 */
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

	/**
	 * Structural class for warps.
	 * 
	 * @author Leo
	 *
	 */
	public static class Warp {

		/**
		 * Starting position for warp data.
		 */
		public static final int BASE_POINTER = 0x158;

		/**
		 * The warp's slot.
		 */
		private int slot;
		/**
		 * The warp's slot graphic.
		 */
		private int id;
		/**
		 * The warp's location event.
		 */
		private int location;

		/**
		 * Gets the starting position for a warp slot.
		 * 
		 * @param slot
		 *            warp slot
		 * @return starting position
		 */
		public static int getPointer(int slot) {
			return BASE_POINTER + slot * (2 * Integer.BYTES);
		}

		/**
		 * Creates a new warp based on data from a byte array.
		 * 
		 * @param data
		 *            byte array
		 * @param slot
		 *            warp slot
		 */
		public Warp(byte[] data, int slot) {
			this.slot = slot;
			final int ptr = getPointer(slot);
			setId(ByteUtils.readInt(data, ptr));
			setLocation(ByteUtils.readInt(data, ptr + Integer.BYTES));
		}

		/**
		 * Saves the warp data to a byte array.
		 * 
		 * @param data
		 *            byte array
		 * @param slot
		 *            warp slot
		 */
		public void write(byte[] data) {
			final int ptr = getPointer(slot);
			ByteUtils.writeInt(data, ptr, id);
			ByteUtils.writeInt(data, ptr + Integer.BYTES, location);
		}

		/**
		 * Gets the warp's ID.
		 * 
		 * @return warp ID
		 */
		public int getId() {
			return id;
		}

		/**
		 * Sets the warp's ID.
		 * 
		 * @param id
		 *            new warp ID
		 */
		public void setId(int id) {
			if (this.id != id)
				notifyListeners(FIELD_WARP_ID, slot, this.id, id);
			this.id = id;
		}

		/**
		 * Gets the warp's location.
		 * 
		 * @return warp location
		 */
		public int getLocation() {
			return location;
		}

		/**
		 * Sets the warp's location.
		 * 
		 * @param location
		 *            new warp location
		 */
		public void setLocation(int location) {
			if (this.location != location)
				notifyListeners(FIELD_WARP_LOCATION, slot, this.location, location);
			this.location = location;
		}

		/**
		 * Returns a string representation of the object.
		 * 
		 * @param indent
		 *            amount of spaces to indent with
		 * @return a string representation of the object
		 */
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

	/**
	 * Loaded flag. If <code>true</code>, a profile has been loaded.
	 */
	private static boolean loaded = false;
	/**
	 * The profile file.
	 */
	private static File file = null;
	/**
	 * The byte array the file is loaded into. Contains raw data.
	 * <p>
	 * <i>This is NOT updated when standard field commands are used and this does
	 * NOT update other fields when modified. Invoke {@link #pushToData()} and
	 * {@link #pullFromData()}, respectively, to do those.</i>
	 */
	private static byte[] data = null;
	/**
	 * Modified flag. If <code>true</code>, the currently loaded profile has been
	 * modified.
	 */
	private static boolean modified = false;
	/**
	 * Disables the undo/redo manager.
	 */
	private static boolean noUndo = true;
	/**
	 * Undo/redo manager.
	 */
	private static UndoManager undoMan;

	/**
	 * Represents an edit to a field in the profile.
	 * 
	 * @author Leo
	 *
	 */
	public static class ProfileEdit implements UndoableEdit {

		/**
		 * The name of the field that was modified.
		 */
		private String field;
		/**
		 * The index of the field that was modified.
		 */
		private int index;
		/**
		 * The field's old value.
		 */
		private Object oldVal;
		/**
		 * The field's new value.
		 */
		private Object newVal;
		/**
		 * If <code>true</code>, this edit has been undone and thus can be redone.
		 */
		private boolean hasBeenUndone;

		/**
		 * Constructs a new <code>ProfileEdit</code>.
		 * 
		 * @param field
		 *            the name of the field that was modified
		 * @param index
		 *            the index of the field that was modified
		 * @param oldVal
		 *            the field's old value
		 * @param newVal
		 *            the field's new value
		 */
		public ProfileEdit(String field, int index, Object oldVal, Object newVal) {
			this.field = field;
			this.index = index;
			this.oldVal = oldVal;
			this.newVal = newVal;
		}

		@Override
		public void undo() throws CannotUndoException {
			System.out.println("Attempting to undo: " + getUndoPresentationName());
			setField(field, index, oldVal);
			hasBeenUndone = true;
		}

		@Override
		public boolean canUndo() {
			return !hasBeenUndone;
		}

		@Override
		public void redo() throws CannotRedoException {
			System.out.println("Attempting to redo: " + getRedoPresentationName());
			setField(field, index, newVal);
			hasBeenUndone = false;
		}

		@Override
		public boolean canRedo() {
			return hasBeenUndone;
		}

		@Override
		public void die() {
			oldVal = null;
			newVal = null;
		}

		@Override
		public boolean addEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean isSignificant() {
			return true;
		}

		@Override
		public String getPresentationName() {
			String fn = field;
			if (index > -1)
				fn += "[" + fn + "]";
			return fn;
		}

		@Override
		public String getUndoPresentationName() {
			String pn = getPresentationName();
			return "undo " + pn + " from " + newVal + " to " + oldVal;
		}

		@Override
		public String getRedoPresentationName() {
			String pn = getPresentationName();
			return "redo " + pn + " from " + oldVal + " to " + newVal;
		}

	}

	/**
	 * Undoes an edit.
	 */
	public static void undo() {
		if (!loaded || undoMan == null || !undoMan.canUndo())
			return;
		undoMan.undo();
	}

	/**
	 * Redoes an edit.
	 */
	public static void redo() {
		if (!loaded || undoMan == null || !undoMan.canRedo())
			return;
		undoMan.redo();
	}

	// reference for pointers: http://www.cavestory.org/guides/profile.txt
	/**
	 * The current map ID.
	 */
	private static int map = 0;
	/**
	 * The currently playing song ID.
	 */
	private static int song = 0;
	/**
	 * The player's X position.
	 */
	private static short x = 0;
	/**
	 * The player's Y position.
	 */
	private static short y = 0;
	/**
	 * The direction the player is facing in.
	 */
	private static int direction = 0;
	/**
	 * Maximum health.
	 */
	private static short maxHealth = 0;
	/**
	 * Number of Whimsical Stars.
	 * <p>
	 * The maximum number of Stars that can be displayed in-game is 3, but values
	 * above 3 will still work: Only 3 Stars are displayed, but the real value gets
	 * reduced, so getting hit with "4" Stars, for example, will cause the player to
	 * remain with 3 Stars.
	 */
	private static short starCount = 0;
	/**
	 * Current health.
	 */
	private static short curHealth = 0;
	/**
	 * Currently selected weapon slot.
	 */
	private static int curWeapon = 0;
	/**
	 * Equipment flags (<code>&lt;EQ+</code>/<code>&lt;EQ-</code>).
	 */
	private static boolean[] equips = null;
	/**
	 * Time played.
	 */
	private static int time = 0;
	/**
	 * Weapons.
	 * 
	 * @see Weapon
	 */
	private static Weapon[] weapons = null;
	/**
	 * Items.
	 */
	private static int[] items = null;
	/**
	 * Warps.
	 * 
	 * @see Warp
	 */
	private static Warp[] warps = null;
	/**
	 * Flags.
	 */
	private static boolean[] flags = null;

	/**
	 * A list of {@link ProfileChangeListener}s.
	 */
	private static List<ProfileChangeListener> listeners;

	/**
	 * Attaches a change listener to this profile.
	 * 
	 * @param l
	 *            listener
	 */
	public static void addListener(ProfileChangeListener l) {
		if (listeners == null)
			listeners = new LinkedList<>();
		listeners.add(l);
	}

	/**
	 * Notifies all change listeners attached to this profile of a change to a
	 * field.
	 * 
	 * @param field
	 *            name of the field that was modified
	 * @param index
	 *            index of the field that was modified, or <code>-1</code> if not
	 *            applicable
	 * @param oldValue
	 *            old value of the field
	 * @param newValue
	 *            new value of the field
	 * @param modified
	 *            modified flag state
	 */
	private static void notifyListeners(String field, int id, Object oldValue, Object newValue, boolean modified) {
		Profile.modified = modified;
		if (!noUndo && undoMan != null && !EVENT_SAVE.equals(field) && !EVENT_LOAD.equals(field))
			undoMan.addEdit(new ProfileEdit(field, id, oldValue, newValue));
		if (listeners == null)
			return;
		for (ProfileChangeListener l : listeners)
			l.onChange(field, id, oldValue, newValue);
	}

	/**
	 * Sets the modified flag and notifies all change listeners attached to this
	 * profile of a change to a field.
	 * 
	 * @param field
	 *            name of the field that was modified
	 * @param index
	 *            index of the field that was modified, or <code>-1</code> if not
	 *            applicable
	 * @param oldValue
	 *            old value of the field
	 * @param newValue
	 *            new value of the field
	 */
	private static void notifyListeners(String field, int id, Object oldValue, Object newValue) {
		notifyListeners(field, id, oldValue, newValue, true);
	}

	/**
	 * Pulls changes from the byte array. This should be called after modifying the
	 * array's contents.
	 */
	public static void pullFromData() {
		if (data == null)
			return;
		setMap(ByteUtils.readInt(data, 0x008));
		setSong(ByteUtils.readInt(data, 0x00C));
		setX(ByteUtils.readShort(data, 0x011));
		setY(ByteUtils.readShort(data, 0x015));
		setDirection(ByteUtils.readInt(data, 0x018));
		setMaxHealth(ByteUtils.readShort(data, 0x01C));
		setStarCount(ByteUtils.readShort(data, 0x01E));
		setCurHealth(ByteUtils.readShort(data, 0x020));
		setCurWeapon(ByteUtils.readInt(data, 0x024));
		if (equips == null)
			equips = new boolean[16];
		boolean[] equips = new boolean[16];
		ByteUtils.readFlags(data, 0x02C, equips);
		setEquips(equips);
		setTime(ByteUtils.readInt(data, 0x034));
		weapons = new Weapon[7];
		for (int i = 0; i < weapons.length; i++) {
			weapons[i] = new Weapon(data, i);
		}
		if (items == null)
			items = new int[30];
		int[] items = new int[30];
		ByteUtils.readInts(data, 0x0D8, items);
		setItems(items);
		warps = new Warp[7];
		for (int i = 0; i < warps.length; i++) {
			warps[i] = new Warp(data, i);
		}
		if (flags == null)
			flags = new boolean[8000];
		boolean[] flags = new boolean[8000];
		ByteUtils.readFlags(data, 0x21C, flags);
		setFlags(flags);
	}

	/**
	 * Pushes changes to the byte array. This should be called before modifying or
	 * reading the array's contents.
	 */
	public static void pushToData() {
		if (data == null)
			data = new byte[FILE_LENGTH];
		ByteUtils.writeString(data, 0, header);
		ByteUtils.writeInt(data, 0x008, map);
		ByteUtils.writeInt(data, 0x00C, song);
		ByteUtils.writeInt(data, 0x011, x);
		ByteUtils.writeInt(data, 0x015, y);
		ByteUtils.writeInt(data, 0x018, direction);
		ByteUtils.writeShort(data, 0x01C, maxHealth);
		ByteUtils.writeShort(data, 0x01E, starCount);
		ByteUtils.writeShort(data, 0x020, curHealth);
		ByteUtils.writeInt(data, 0x024, curWeapon);
		ByteUtils.writeFlags(data, 0x02C, equips);
		ByteUtils.writeInt(data, 0x034, time);
		for (Weapon w : weapons) {
			w.write(data);
		}
		ByteUtils.writeInts(data, 0x0D8, items);
		for (Warp w : warps) {
			w.write(data);
		}
		ByteUtils.writeString(data, 0x218, flagH);
		ByteUtils.writeFlags(data, 0x21C, flags);
	}

	/**
	 * Reads a profile file.
	 * 
	 * @param file
	 *            file to read
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void read(File file) throws IOException {
		Profile.file = file;
		// read data
		data = new byte[FILE_LENGTH];
		try (FileInputStream fis = new FileInputStream(file)) {
			if (fis.read(data) != data.length)
				throw new IOException("file is too small");
		}
		// check header
		String profHeader = ByteUtils.readString(data, 0, header.length());
		if (!header.equals(profHeader))
			throw new IOException("Invalid file header!");
		// check flag header
		String profFlagH = ByteUtils.readString(data, 0x218, flagH.length());
		if (!flagH.equals(profFlagH))
			throw new IOException("Flag header is missing!");
		// pull values from data
		pullFromData();
		// set loaded flag
		loaded = true;
		// new undo manager
		if (noUndo)
			undoMan = null;
		else
			undoMan = new UndoManager();
		// notify listeners with "field" EVENT_LOAD
		notifyListeners(EVENT_LOAD, -1, null, null, false);
	}

	/**
	 * Reads a profile file.
	 * 
	 * @param path
	 *            path to file to read
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void read(String path) throws IOException {
		read(new File(path));
	}

	/**
	 * Writes the profile file.
	 * 
	 * @param dest
	 *            path to file to write
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void write(File dest) throws IOException {
		if (!loaded)
			return;
		// back up file just in case
		File backup = null;
		if (dest.exists()) {
			backup = new File(dest.getAbsolutePath() + ".bkp");
			if (backup.exists()) {
				backup.delete();
			}
			backup.createNewFile();
			try (FileOutputStream fos = new FileOutputStream(backup); FileInputStream fis = new FileInputStream(dest)) {
				byte[] data = new byte[FILE_LENGTH];
				fis.read(data);
				fos.write(data);
			}
		} else {
			dest.createNewFile();
		}
		// start writing
		pushToData();
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			if (backup != null) {
				System.err.println("Error while saving profile! Recovering backup.");
				try (FileOutputStream fos = new FileOutputStream(file);
						FileInputStream fis = new FileInputStream(backup)) {
					byte[] data = new byte[FILE_LENGTH];
					fis.read(data);
					fos.write(data);
				}
			}
		}
		// set new file
		file = dest;
		// notify listeners with "field" EVENT_SAVE
		notifyListeners(EVENT_SAVE, -1, null, null, false);
	}

	/**
	 * Writes the profile file.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void write() throws IOException {
		write(file);
	}

	/**
	 * Checks if a profile is loaded.
	 * 
	 * @return <code>true</code> if a profile is loaded, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isLoaded() {
		return loaded;
	}

	/**
	 * Gets the loaded profile.
	 * 
	 * @return loaded profile
	 */
	public static File getFile() {
		return file;
	}

	/**
	 * Gets the raw data array.
	 * 
	 * @return data array
	 */
	public static byte[] getData() {
		return data;
	}

	/**
	 * Checks if the profile was modified since the last time it was saved.
	 * 
	 * @return <code>true</code> if modified, <code>false</code> otherwise.
	 */
	public static boolean isModified() {
		return modified;
	}

	/**
	 * Checks if the undo/redo manager is disabled.
	 * 
	 * @return <code>true</code> if disabled, <code>false</code> otherwise.
	 */
	public static boolean isNoUndo() {
		return noUndo;
	}

	/**
	 * Disables or enables the undo/redo manager.
	 * 
	 * @param noUndo
	 *            <code>true</code> to disable, <code>false</code> to enable
	 */
	public static void setNoUndo(boolean noUndo) {
		Profile.noUndo = noUndo;
		if (noUndo)
			undoMan = null;
		else
			undoMan = new UndoManager();
	}

	/**
	 * Gets the current map ID.
	 * 
	 * @return map ID
	 */
	public static int getMap() {
		return map;
	}

	/**
	 * Sets the current map ID.
	 * 
	 * @param map
	 *            new map ID
	 */
	public static void setMap(int map) {
		if (Profile.map != map)
			notifyListeners(FIELD_MAP, -1, Profile.map, map);
		Profile.map = map;
	}

	/**
	 * Gets the currently playing song ID.
	 * 
	 * @return song ID
	 */
	public static int getSong() {
		return song;
	}

	/**
	 * Sets the currently playing song ID.
	 * 
	 * @param song
	 *            new song ID
	 */
	public static void setSong(int song) {
		if (Profile.song != song)
			notifyListeners(FIELD_SONG, -1, Profile.song, song);
		Profile.song = song;
	}

	/**
	 * Gets the player's X position.
	 * 
	 * @return X position
	 */
	public static short getX() {
		return x;
	}

	public static void setPosition(Short[] value) {
		if (Profile.x != value[0] && Profile.y != value[1])
			notifyListeners(FIELD_POSITION, -1, new Short[] { Profile.x, Profile.y }, value);
		Profile.x = value[0];
		Profile.y = value[1];
	}

	public static void setPosition(short x, short y) {
		setPosition(new Short[] { x, y });
	}

	/**
	 * Sets the player's X position.
	 * 
	 * @param x
	 *            new X position
	 */
	public static void setX(short x) {
		if (Profile.x != x)
			notifyListeners(FIELD_X_POSITION, -1, Profile.x, x);
		Profile.x = x;
	}

	/**
	 * Gets the player's Y position.
	 * 
	 * @return Y position
	 */
	public static short getY() {
		return y;
	}

	/**
	 * Gets the player's Y position.
	 * 
	 * @param y
	 *            new Y position
	 */
	public static void setY(short y) {
		if (Profile.y != y)
			notifyListeners(FIELD_Y_POSITION, -1, Profile.y, y);
		Profile.y = y;
	}

	/**
	 * Gets the direction the player is facing in.
	 * 
	 * @return direction
	 */
	public static int getDirection() {
		return direction;
	}

	/**
	 * Sets the direction the player is facing in.
	 * 
	 * @param direction
	 *            new direction
	 */
	public static void setDirection(int direction) {
		if (Profile.direction != direction)
			notifyListeners(FIELD_DIRECTION, -1, Profile.direction, direction);
		Profile.direction = direction;
	}

	/**
	 * Gets the maximum health.
	 * 
	 * @return maximum health
	 */
	public static short getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Sets the maximum health.
	 * 
	 * @param maxHealth
	 *            new maximum health
	 */
	public static void setMaxHealth(short maxHealth) {
		if (Profile.maxHealth != maxHealth)
			notifyListeners(FIELD_MAXIMUM_HEALTH, -1, Profile.maxHealth, maxHealth);
		Profile.maxHealth = maxHealth;
	}

	/**
	 * Gets the number of Whimsical Stars.
	 * 
	 * @return star count
	 */
	public static short getStarCount() {
		return starCount;
	}

	/**
	 * Sets the number of Whimsical Stars. See {@linkplain #starCount here} for
	 * details.
	 * 
	 * @param starCount
	 *            new star count
	 */
	public static void setStarCount(short starCount) {
		if (Profile.starCount != starCount)
			notifyListeners(FIELD_STAR_COUNT, -1, Profile.starCount, starCount);
		Profile.starCount = starCount;
	}

	/**
	 * Gets the current health.
	 * 
	 * @return current health
	 */
	public static short getCurHealth() {
		return curHealth;
	}

	/**
	 * Sets the current health.
	 * 
	 * @param curHealth
	 *            new current health
	 */
	public static void setCurHealth(short curHealth) {
		if (Profile.curHealth != curHealth)
			notifyListeners(FIELD_CURRENT_HEALTH, -1, Profile.curHealth, curHealth);
		Profile.curHealth = curHealth;
	}

	/**
	 * Gets the currently selected weapon slot.
	 * 
	 * @return current weapon
	 */
	public static int getCurWeapon() {
		return curWeapon;
	}

	/**
	 * Sets the currently selected weapon slot.
	 * 
	 * @param curWeapon
	 *            new current weapon
	 */
	public static void setCurWeapon(int curWeapon) {
		if (Profile.curWeapon != curWeapon)
			notifyListeners(FIELD_CURRENT_WEAPON, -1, Profile.curWeapon, curWeapon);
		Profile.curWeapon = curWeapon;
	}

	/**
	 * Gets the state of all equipments.
	 * 
	 * @return all equips
	 */
	public static boolean[] getEquips() {
		return equips.clone();
	}

	/**
	 * Gets the state of a specific equipment.
	 * 
	 * @param id
	 *            equipment id
	 * @return <code>true</code> if equipment is equipped, <code>false</code>
	 *         otherwise
	 */
	public static boolean getEquip(int id) {
		return equips[id];
	}

	/**
	 * Sets the state of multiple equipment flags.
	 * 
	 * @param startId
	 *            starting flag ID
	 * @param values
	 *            flag states
	 */
	public static void setEquips(int startId, boolean[] values) {
		int aid = startId;
		for (int i = 0; i < values.length; i++) {
			if (aid >= equips.length)
				return;
			setEquip(aid, values[i]);
			aid++;
		}
	}

	/**
	 * Sets the state of multiple equipment flags.
	 * 
	 * @param values
	 *            flag states
	 */
	public static void setEquips(boolean[] values) {
		setEquips(0, values);
	}

	/**
	 * Sets the state of a specific equipment flag.
	 * 
	 * @param id
	 *            equipment id
	 * @param equipped
	 *            <code>true</code> if equipment is equipped, <code>false</code>
	 *            otherwise
	 */
	public static void setEquip(int id, boolean equipped) {
		if (id >= equips.length)
			return;
		if (equips[id] != equipped)
			notifyListeners(FIELD_EQUIPS, id, equips[id], equipped);
		equips[id] = equipped;
	}

	/**
	 * Gets the time played.
	 * 
	 * @return time played
	 */
	public static int getTime() {
		return time;
	}

	/**
	 * Sets the time played.
	 * 
	 * @param time
	 *            new time played
	 */
	public static void setTime(int time) {
		if (Profile.time != time)
			notifyListeners(FIELD_TIME_PLAYED, -1, Profile.time, time);
		Profile.time = time;
	}

	/**
	 * Gets weapon data from all slots.
	 * 
	 * @return all weapons
	 */
	public static Weapon[] getWeapons() {
		return weapons.clone();
	}

	/**
	 * Gets weapon data from a specific slot.
	 * 
	 * @param id
	 *            weapon slot
	 * @return weapon data in slot
	 */
	public static Weapon getWeapon(int id) {
		return weapons[id];
	}

	/**
	 * Gets the items in all slots.
	 * 
	 * @return all items
	 */
	public static int[] getItems() {
		return items.clone();
	}

	/**
	 * Gets the item in a specific slot.
	 * 
	 * @param id
	 *            item slot
	 * @return item ID
	 */
	public static int getItem(int id) {
		return items[id];
	}

	/**
	 * Sets the items of multiple slots.
	 * 
	 * @param startId
	 *            starting item slot
	 * @param values
	 *            item IDs
	 */
	public static void setItems(int startId, int[] values) {
		int as = startId;
		for (int i = 0; i < values.length; i++) {
			if (as >= items.length)
				return;
			setItem(as, values[i]);
			as++;
		}
	}

	/**
	 * Sets the items of multiple slots.
	 * 
	 * @param values
	 *            item IDs
	 */
	public static void setItems(int[] values) {
		setItems(0, values);
	}

	/**
	 * Sets the item in a specific slot.
	 * 
	 * @param id
	 *            item slot
	 * @param value
	 *            new item ID
	 */
	public static void setItem(int id, int value) {
		if (id >= items.length)
			return;
		if (items[id] != value)
			notifyListeners(FIELD_ITEMS, id, items[id], value);
		items[id] = value;
	}

	/**
	 * Gets warp data from all slots.
	 * 
	 * @return all warps
	 */
	public static Warp[] getWarps() {
		return warps.clone();
	}

	/**
	 * Gets warp data in a specific slot.
	 * 
	 * @param id
	 *            warp slot
	 * @return warp data in slot
	 */
	public static Warp getWarp(int id) {
		if (id >= warps.length)
			return null;
		return warps[id];
	}

	/**
	 * Gets the state of all flags.
	 * 
	 * @return all flags
	 */
	public static boolean[] getFlags() {
		return flags;
	}

	/**
	 * Gets the state of a specific flag.
	 * 
	 * @param id
	 *            flag ID
	 * @return <code>true</code> if flag is set, <code>false</code> otherwise
	 */
	public static boolean getFlag(int id) {
		if (flags == null)
			return false;
		return flags[id];
	}

	/**
	 * Sets the states of multiple flags.
	 * 
	 * @param startId
	 *            starting flag ID
	 * @param values
	 *            flag states
	 */
	public static void setFlags(int startId, boolean[] values) {
		int aid = startId;
		for (int i = 0; i < values.length; i++) {
			if (aid >= flags.length)
				return;
			setFlag(aid, values[i]);
			aid++;
		}
	}

	/**
	 * Sets the states of multiple flags.
	 * 
	 * @param values
	 *            flag states
	 */
	public static void setFlags(boolean[] values) {
		setFlags(0, values);
	}

	/**
	 * Sets the state of a specific flag.
	 * 
	 * @param id
	 *            flag ID
	 * @param set
	 *            <code>true</code> if flag is set, <code>false</code> otherwise
	 */
	public static void setFlag(int id, boolean set) {
		if (id >= flags.length)
			return;
		if (flags[id] != set)
			notifyListeners(FIELD_FLAGS, id, flags[id], set);
		flags[id] = set;
	}

	/// ------------------------
	/// Special ASM hack support
	/// ------------------------

	/**
	 * Gets the current &lt;MIM costume.
	 * 
	 * @return current costume
	 */
	public static long getMimCostume() {
		long ret = 0;
		for (int i = 7968; i < 7995; i++)
			if (getFlag(i))
				ret |= (long) Math.pow(2, i - 7968);
		return ret;
	}

	/**
	 * Sets the current &lt;MIM costume.
	 * 
	 * @param costume
	 *            new costume
	 */
	public static void setMimCostume(long costume) {
		long curCostume = getMimCostume();
		if (curCostume != costume)
			notifyListeners(FIELD_MIM_COSTUME, -1, curCostume, costume);
		for (int i = 7968; i < 7995; i++)
			setFlag(i, (costume & (long) Math.pow(2, i - 7968)) != 0);
	}

	/**
	 * Gets the value of a &lt;VAR variable.
	 * 
	 * @param id
	 *            variable id
	 * @return value
	 */
	public static short getVariable(int id) {
		pushToData();
		return ByteUtils.readShort(data, 0x50A + id * Short.BYTES);
	}

	/**
	 * Sets a &lt;VAR variable to a value.
	 * 
	 * @param id
	 *            variable id
	 * @param value
	 *            new value
	 */
	public static void setVariable(int id, short value) {
		short oldValue = getVariable(id);
		if (oldValue != value)
			notifyListeners(FIELD_VARIABLES, id, oldValue, value);
		pushToData();
		ByteUtils.writeShort(data, 0x50A + id * Short.BYTES, value);
		pullFromData();
	}

	/**
	 * Gets the value of a &lt;PHY variable.
	 * 
	 * @param id
	 *            variable id
	 * @return value
	 */
	public static short getPhysVariable(int id) {
		pushToData();
		return ByteUtils.readShort(data, 0x4DC + id * Short.BYTES);
	}

	/**
	 * Sets a &lt;PHY variable to a value.
	 * 
	 * @param id
	 *            variable id
	 * @param value
	 *            new value
	 */
	public static void setPhysVariable(int id, short value) {
		short oldValue = getPhysVariable(id);
		if (oldValue != value)
			notifyListeners(FIELD_PHYSICS_VARIABLES, id, oldValue, value);
		pushToData();
		ByteUtils.writeShort(data, 0x4DC + id * Short.BYTES, value);
		pullFromData();
	}

	/**
	 * Gets the amount of cash.
	 * 
	 * @return amount of cash
	 */
	public static long getCash() {
		long ret = 0;
		for (int i = 7968; i < 7999; i++)
			if (getFlag(i))
				ret |= (long) Math.pow(2, i - 7968);
		return ret;
	}

	/**
	 * Sets the amount of cash.
	 * 
	 * @param cash
	 *            new amount of cash
	 */
	public static void setCash(long cash) {
		long oldCash = getCash();
		if (oldCash != cash)
			notifyListeners(FIELD_CASH, -1, oldCash, cash);
		for (int i = 7968; i < 7999; i++)
			setFlag(i, (cash & (long) Math.pow(2, i - 7968)) != 0);
	}

	/**
	 * Sets a field.
	 * 
	 * @param field
	 *            field to set
	 * @param index
	 *            index to set
	 * @param value
	 *            value to set
	 */
	public static void setField(String field, int index, Object value) {
		boolean oldUndo = noUndo;
		noUndo = true;
		Weapon w;
		Warp t;
		switch (field) {
		case FIELD_MAP:
			if (!(value instanceof Integer))
				return;
			setMap((Integer) value);
			break;
		case FIELD_SONG:
			if (!(value instanceof Integer))
				return;
			setSong((Integer) value);
			break;
		case FIELD_POSITION:
			if (!(value instanceof Short[]))
				return;
			setPosition((Short[]) value);
			break;
		case FIELD_X_POSITION:
			if (!(value instanceof Short))
				return;
			setX((Short) value);
			break;
		case FIELD_Y_POSITION:
			if (!(value instanceof Short))
				return;
			setY((Short) value);
			break;
		case FIELD_DIRECTION:
			if (!(value instanceof Integer))
				return;
			setDirection((Integer) value);
			break;
		case FIELD_MAXIMUM_HEALTH:
			if (!(value instanceof Short))
				return;
			setMaxHealth((Short) value);
			break;
		case FIELD_STAR_COUNT:
			if (!(value instanceof Short))
				return;
			setStarCount((Short) value);
			break;
		case FIELD_CURRENT_HEALTH:
			if (!(value instanceof Short))
				return;
			setCurHealth((Short) value);
			break;
		case FIELD_CURRENT_WEAPON:
			if (!(value instanceof Integer))
				return;
			setCurWeapon((Integer) value);
			break;
		case FIELD_EQUIPS:
			if (index < 0)
				return;
			if (!(value instanceof Boolean))
				return;
			setEquip(index, (Boolean) value);
			break;
		case FIELD_TIME_PLAYED:
			if (!(value instanceof Integer))
				return;
			setTime((Integer) value);
			break;
		case FIELD_WEAPON_ID:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			w = getWeapon(index);
			if (w == null)
				return;
			w.setId((Integer) value);
			break;
		case FIELD_WEAPON_LEVEL:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			w = getWeapon(index);
			if (w == null)
				return;
			w.setLevel((Integer) value);
			break;
		case FIELD_WEAPON_EXP:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			w = getWeapon(index);
			if (w == null)
				return;
			w.setExp((Integer) value);
			break;
		case FIELD_WEAPON_CURRENT_AMMO:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			w = getWeapon(index);
			if (w == null)
				return;
			w.setCurAmmo((Integer) value);
			break;
		case FIELD_WEAPON_MAXIMUM_AMMO:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			w = getWeapon(index);
			if (w == null)
				return;
			w.setMaxAmmo((Integer) value);
			break;
		case FIELD_ITEMS:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			setItem(index, (Integer) value);
			break;
		case FIELD_WARP_ID:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			t = getWarp(index);
			if (t == null)
				return;
			t.setId((Integer) value);
		case FIELD_WARP_LOCATION:
			if (index < 0)
				return;
			if (!(value instanceof Integer))
				return;
			t = getWarp(index);
			if (t == null)
				return;
			t.setLocation((Integer) value);
		case FIELD_FLAGS:
			if (index < 0)
				return;
			if (!(value instanceof Boolean))
				return;
			setFlag(index, (Boolean) value);
			break;
		case FIELD_MIM_COSTUME:
			if (!(value instanceof Long))
				return;
			setMimCostume((Long) value);
			break;
		case FIELD_VARIABLES:
			if (index < 0)
				return;
			if (!(value instanceof Short))
				return;
			setVariable(index, (Short) value);
			break;
		case FIELD_PHYSICS_VARIABLES:
			if (index < 0)
				return;
			if (!(value instanceof Short))
				return;
			setPhysVariable(index, (Short) value);
			break;
		case FIELD_CASH:
			if (!(value instanceof Long))
				return;
			setCash((Long) value);
			break;
		case EVENT_SAVE:
		case EVENT_LOAD:
		default:
			break;
		}
		noUndo = oldUndo;
	}

	/**
	 * Creates a text-based dump of the information stored in the profile.
	 * 
	 * @return information dump
	 */
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
		for (int i = 0; i < warps.length; i++) {
			ret += "\n  " + i + ": {\n" + warps[i].toString(4) + "\n  }";
			if (i != warps.length - 1) {
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

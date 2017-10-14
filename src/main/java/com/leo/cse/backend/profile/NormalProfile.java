package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.leo.cse.backend.ByteUtils;

public class NormalProfile extends CommonProfile {

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
	 * Position "field". A field for this doesn't actually exist.
	 * 
	 * @see #getPosition()
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
	public static final String FIELD_EQUIPS = "equips";
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
	public static final String FIELD_WEAPON_ID = "weapons.id";
	/**
	 * Weapon level field.
	 * 
	 * @see Weapon#level
	 */
	public static final String FIELD_WEAPON_LEVEL = "weapons.level";
	/**
	 * Weapon EXP field.
	 * 
	 * @see Weapon#exp
	 */
	public static final String FIELD_WEAPON_EXP = "weapons.exp";
	/**
	 * Weapon maximum ammo field.
	 * 
	 * @see Weapon#maxAmmo
	 */
	public static final String FIELD_WEAPON_MAXIMUM_AMMO = "weapons.maxAmmo";
	/**
	 * Weapon current ammo field.
	 * 
	 * @see Weapon#curAmmo
	 */
	public static final String FIELD_WEAPON_CURRENT_AMMO = "weapons.curAmmo";
	/**
	 * Items field.
	 * 
	 * @see #items
	 */
	public static final String FIELD_ITEMS = "items";
	/**
	 * Warp ID field.
	 * 
	 * @see Warp#id
	 */
	public static final String FIELD_WARP_ID = "warps.id";
	/**
	 * Warp location field.
	 * 
	 * @see Warp#location
	 */
	public static final String FIELD_WARP_LOCATION = "warps.location";
	/**
	 * Flags field.
	 * 
	 * @see #flags
	 */
	public static final String FIELD_FLAGS = "flags";
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
	public static final String FIELD_VARIABLES = "variables";
	/**
	 * Physics variables "field". A field for this doesn't actually exist.
	 * 
	 * @see #getPhysVariable(int)
	 */
	public static final String FIELD_PHYSICS_VARIABLES = "physVars";
	/**
	 * Amount of cash "field". A field for this doesn't actually exist.
	 * 
	 * @see #getCash()
	 */
	public static final String FIELD_CASH = "cash";

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

	public NormalProfile(boolean extFields) {
		super();
		header = DEFAULT_HEADER;
		flagH = DEFAULT_FLAGH;
		setupFields();
		if (extFields)
			setupFieldsExt();
	}

	public NormalProfile() {
		this(true);
	}

	protected byte[] data;

	@Override
	public void read(File file, int section) throws IOException {
		// read data
		data = new byte[FILE_LENGTH];
		try (FileInputStream fis = new FileInputStream(file)) {
			if (fis.read(data) < data.length)
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
		// set loaded file
		loadedFile = file;
	}

	@Override
	public void write(File file, int section) throws IOException {
		if (data == null)
			return;
		// back up file just in case
		File backup = null;
		if (file.exists()) {
			backup = new File(file.getAbsolutePath() + ".bkp");
			if (backup.exists()) {
				backup.delete();
			}
			backup.createNewFile();
			try (FileOutputStream fos = new FileOutputStream(backup); FileInputStream fis = new FileInputStream(file)) {
				byte[] data = new byte[FILE_LENGTH];
				fis.read(data);
				fos.write(data);
			}
		} else {
			file.createNewFile();
		}
		// start writing
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
		// set loaded file
		loadedFile = file;
	}

	protected void setupFields() {
		makeFieldInt(FIELD_MAP, 0x008);
		makeFieldInt(FIELD_SONG, 0x00C);
		makeFieldShort(FIELD_X_POSITION, 0x011);
		makeFieldShort(FIELD_Y_POSITION, 0x015);
		makeFieldPosition(0x011, 0x015);
		makeFieldInt(FIELD_DIRECTION, 0x018);
		makeFieldShort(FIELD_MAXIMUM_HEALTH, 0x01C);
		makeFieldShort(FIELD_STAR_COUNT, 0x01E);
		makeFieldShort(FIELD_CURRENT_HEALTH, 0x020);
		makeFieldInt(FIELD_CURRENT_WEAPON, 0x024);
		makeFieldFlags(FIELD_EQUIPS, 16, 0x02C);
		makeFieldInt(FIELD_TIME_PLAYED, 0x034);
		makeFieldInts(FIELD_WEAPON_ID, 7, Integer.BYTES * 5, 0x038);
		makeFieldInts(FIELD_WEAPON_LEVEL, 7, Integer.BYTES * 5, 0x03C);
		makeFieldInts(FIELD_WEAPON_EXP, 7, Integer.BYTES * 5, 0x040);
		makeFieldInts(FIELD_WEAPON_MAXIMUM_AMMO, 7, Integer.BYTES * 5, 0x044);
		makeFieldInts(FIELD_WEAPON_CURRENT_AMMO, 7, Integer.BYTES * 5, 0x048);
		makeFieldInts(FIELD_ITEMS, 30, 0, 0x0D8);
		makeFieldInts(FIELD_WARP_ID, 7, Integer.BYTES * 2, 0x158);
		makeFieldInts(FIELD_WARP_LOCATION, 7, Integer.BYTES * 2, 0x15C);
		makeFieldFlags(FIELD_FLAGS, 8000, 0x21C);
	}

	protected void setupFieldsExt() {
		try {
			addField(FIELD_MIM_COSTUME, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Long.TYPE;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Long;
				}

				@Override
				public Object getValue(int index) {
					long ret = 0;
					for (int i = 7968; i < 7995; i++)
						try {
							if ((boolean) getField(FIELD_FLAGS, i))
								ret |= (long) Math.pow(2, i - 7968);
						} catch (ProfileFieldException e) {
							e.printStackTrace();
						}
					return ret;
				}

				@Override
				public void setValue(int index, Object value) {
					long v = (Long) value;
					for (int i = 7968; i < 7995; i++)
						try {
							setField(FIELD_FLAGS, i, (v & (long) Math.pow(2, i - 7968)) != 0);
						} catch (ProfileFieldException e) {
							e.printStackTrace();
						}
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
		makeFieldShorts(FIELD_VARIABLES, 123, 0, 0x50A);
		makeFieldShorts(FIELD_PHYSICS_VARIABLES, 16, 0, 0x4DC);
		makeFieldLong(FIELD_CASH, 0x600);
	}

	protected void makeFieldShort(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Short;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readShort(data, ptr);
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeShort(data, ptr, (Short) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldShorts(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Short;
				}

				@Override
				public Object getValue(int index) {
					short[] ret = new short[length];
					ByteUtils.readShorts(data, ptr, off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					short[] vals = new short[length];
					ByteUtils.readShorts(data, ptr, off, vals);
					vals[index] = (Short) value;
					ByteUtils.writeShorts(data, ptr, off, vals);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldInt(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Integer;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readInt(data, ptr);
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeInt(data, ptr, (Integer) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldInts(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Integer;
				}

				@Override
				public Object getValue(int index) {
					int[] ret = new int[length];
					ByteUtils.readInts(data, ptr, off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int[] vals = new int[length];
					ByteUtils.readInts(data, ptr, off, vals);
					vals[index] = (Integer) value;
					ByteUtils.writeInts(data, ptr, off, vals);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldLong(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Long.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Long;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readLong(data, ptr);
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeLong(data, ptr, (Long) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldFlags(String name, int length, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Boolean.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Boolean;
				}

				@Override
				public Object getValue(int index) {
					boolean[] vals = new boolean[length];
					ByteUtils.readFlags(data, ptr, vals);
					return vals[index];
				}

				@Override
				public void setValue(int index, Object value) {
					boolean[] vals = new boolean[length];
					ByteUtils.readFlags(data, ptr, vals);
					vals[index] = (Boolean) value;
					ByteUtils.writeFlags(data, ptr, vals);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldPosition(int xPtr, int yPtr) {
		try {
			addField(FIELD_POSITION, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short[].class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Short[];
				}

				@Override
				public Object getValue(int index) {
					Short[] ret = new Short[2];
					ret[0] = ByteUtils.readShort(data, xPtr);
					ret[1] = ByteUtils.readShort(data, yPtr);
					return ret;
				}

				@Override
				public void setValue(int index, Object value) {
					Short[] vals = (Short[]) value;
					ByteUtils.writeShort(data, xPtr, vals[0]);
					ByteUtils.writeShort(data, yPtr, vals[1]);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

}

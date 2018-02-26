package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.leo.cse.backend.ByteUtils;

public class NormalProfile extends Profile {

	public static final String EVENT_DATA_MODIFIED = "data.modified";
	/**
	 * Map field.
	 */
	public static final String FIELD_MAP = "map";
	/**
	 * Song field.
	 */
	public static final String FIELD_SONG = "song";
	/**
	 * Position "field". A field for this doesn't actually exist.
	 */
	public static final String FIELD_POSITION = "position";
	/**
	 * X position field.
	 */
	public static final String FIELD_X_POSITION = "position_x";
	/**
	 * Y position field.
	 */
	public static final String FIELD_Y_POSITION = "position_y";
	/**
	 * Direction field.
	 */
	public static final String FIELD_DIRECTION = "direction";
	/**
	 * Maximum health field.
	 */
	public static final String FIELD_MAXIMUM_HEALTH = "max_health";
	/**
	 * Whimsical Star count field.
	 */
	public static final String FIELD_STAR_COUNT = "star_count";
	/**
	 * Current health field.
	 */
	public static final String FIELD_CURRENT_HEALTH = "cur_health";
	/**
	 * Current weapon slot field.
	 */
	public static final String FIELD_CURRENT_WEAPON = "cur_weapon";
	/**
	 * Equipment flags field.
	 */
	public static final String FIELD_EQUIPS = "equips";
	/**
	 * Time played field.
	 */
	public static final String FIELD_TIME_PLAYED = "time";
	/**
	 * Weapon ID field.
	 */
	public static final String FIELD_WEAPON_ID = "weapons.id";
	/**
	 * Weapon level field.
	 */
	public static final String FIELD_WEAPON_LEVEL = "weapons.level";
	/**
	 * Weapon EXP field.
	 */
	public static final String FIELD_WEAPON_EXP = "weapons.exp";
	/**
	 * Weapon maximum ammo field.
	 */
	public static final String FIELD_WEAPON_MAXIMUM_AMMO = "weapons.max_ammo";
	/**
	 * Weapon current ammo field.
	 */
	public static final String FIELD_WEAPON_CURRENT_AMMO = "weapons.cur_ammo";
	/**
	 * Items field.
	 */
	public static final String FIELD_ITEMS = "items";
	/**
	 * Warp ID field.
	 */
	public static final String FIELD_WARP_ID = "warps.id";
	/**
	 * Warp location field.
	 */
	public static final String FIELD_WARP_LOCATION = "warps.location";
	/**
	 * Map flags field.
	 */
	public static final String FIELD_MAP_FLAGS = "map_flags";
	/**
	 * Flags field.
	 */
	public static final String FIELD_FLAGS = "flags";
	/**
	 * <MIM costume "field". A field for this doesn't actually exist.
	 */
	public static final String FIELD_MIM_COSTUME = "mim_costume";
	/**
	 * Variables "field". A field for this doesn't actually exist.
	 */
	public static final String FIELD_VARIABLES = "variables";
	/**
	 * Physics variables "field". A field for this doesn't actually exist.
	 */
	public static final String FIELD_PHYSICS_VARIABLES = "phys_vars";
	/**
	 * Amount of cash "field". A field for this doesn't actually exist.
	 */
	public static final String FIELD_CASH = "cash";

	public static final String FIELD_EQP_VARIABLES = "eqp.variables";

	public static final String FIELD_EQP_MODS_TRUE = "eqp.mods.true";

	public static final String FIELD_EQP_MODS_FALSE = "eqp.mods.false";

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
	public void create() {
		// create data
		data = new byte[FILE_LENGTH];
		// insert header & flag header
		ByteUtils.writeString(data, 0, header);
		ByteUtils.writeString(data, 0x218, flagH);
		// set loaded file to null
		loadedFile = null;
	}

	@Override
	public void load(File file) throws IOException {
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
	public void save(File file) throws IOException {
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
		makeFieldBools(FIELD_MAP_FLAGS, 128, 0, 0x196);
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
		makeFieldBytes(FIELD_EQP_VARIABLES, 64, 0, 0x196);
		makeFieldFlags(FIELD_EQP_MODS_TRUE, 3, 0x1D6);
		makeFieldFlags(FIELD_EQP_MODS_FALSE, 3, 0x1D8);
	}

	protected int correctPointer(int ptr) {
		return ptr;
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
					return ByteUtils.readShort(data, correctPointer(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeShort(data, correctPointer(ptr), (Short) value);
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
					ByteUtils.readShorts(data, correctPointer(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = correctPointer(ptr);
					short[] vals = new short[length];
					ByteUtils.readShorts(data, cptr, off, vals);
					vals[index] = (Short) value;
					ByteUtils.writeShorts(data, cptr, off, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinumumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length;
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
					return ByteUtils.readInt(data, correctPointer(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeInt(data, correctPointer(ptr), (Integer) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}
	
	protected void makeFieldBool(String name, int ptr) {
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
					byte flag = data[correctPointer(ptr)];
					return (flag == 0 ? false : true);
				}

				@Override
				public void setValue(int index, Object value) {
					byte flag = (byte) ((Boolean) value ? 1 : 0);
					data[correctPointer(ptr)] = flag;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldBools(String name, int length, int off, int ptr) {
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
					byte[] ret = new byte[length];
					ByteUtils.readBytes(data, correctPointer(ptr), off, ret);
					return (ret[index] == 0 ? false : true);
				}

				@Override
				public void setValue(int index, Object value) {
					byte actualVal = 0;
					if ((Boolean) value)
						actualVal = 1;
					int cptr = correctPointer(ptr);
					byte[] vals = new byte[length];
					ByteUtils.readBytes(data, cptr, off, vals);
					vals[index] = actualVal;
					ByteUtils.writeBytes(data, cptr, off, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinumumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void makeFieldBytes(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Byte.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Byte;
				}

				@Override
				public Object getValue(int index) {
					byte[] ret = new byte[length];
					ByteUtils.readBytes(data, correctPointer(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = correctPointer(ptr);
					byte[] vals = new byte[length];
					ByteUtils.readBytes(data, cptr, off, vals);
					vals[index] = (Byte) value;
					ByteUtils.writeBytes(data, cptr, off, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinumumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length;
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
					ByteUtils.readInts(data, correctPointer(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = correctPointer(ptr);
					int[] vals = new int[length];
					ByteUtils.readInts(data, cptr, off, vals);
					vals[index] = (Integer) value;
					ByteUtils.writeInts(data, cptr, off, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinumumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length;
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
					return ByteUtils.readLong(data, correctPointer(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeLong(data, correctPointer(ptr), (Long) value);
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
					ByteUtils.readFlags(data, correctPointer(ptr), vals);
					return vals[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = correctPointer(ptr);
					boolean[] vals = new boolean[length];
					ByteUtils.readFlags(data, cptr, vals);
					vals[index] = (Boolean) value;
					ByteUtils.writeFlags(data, cptr, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinumumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length;
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
					ret[0] = ByteUtils.readShort(data, correctPointer(xPtr));
					ret[1] = ByteUtils.readShort(data, correctPointer(yPtr));
					return ret;
				}

				@Override
				public void setValue(int index, Object value) {
					Short[] vals = (Short[]) value;
					ByteUtils.writeShort(data, correctPointer(xPtr), vals[0]);
					ByteUtils.writeShort(data, correctPointer(yPtr), vals[1]);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

}

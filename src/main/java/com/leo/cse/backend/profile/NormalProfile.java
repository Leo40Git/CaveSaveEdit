package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;

import com.leo.cse.backend.ByteUtils;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.ExeData.StartPoint;
import com.leo.cse.backend.profile.ProfileManager.ProfileFieldException;

public class NormalProfile extends Profile {

	/**
	 * A combination of the {@linkplain #FIELD_MAP map field} and the
	 * {@linkplain #FIELD_POSITION position field}.
	 */
	public static final String FIELD_MAP_AND_POSITION = "map_and_pos";
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
	/**
	 * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i>
	 * EQ+ variables.
	 */
	public static final String FIELD_EQP_VARIABLES = "eqp.variables";
	/**
	 * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i>
	 * EQ+ "true" modifiers.
	 */
	public static final String FIELD_EQP_MODS_TRUE = "eqp.mods.true";
	/**
	 * <i>(EQ+ STUFF NOT IMPLEMENTED YET!)</i>
	 * EQ+ "false" modifiers.
	 */
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

	/**
	 * Initializes and registers fields.
	 *
	 * @param extFields
	 *            "extended fields" flag. if <code>true</code>, special support
	 *            fields will also be registered
	 */
	public NormalProfile(boolean extFields) {
		super();
		header = DEFAULT_HEADER;
		flagH = DEFAULT_FLAGH;
		setupFields();
		if (extFields)
			setupFieldsExt();
	}

	/**
	 * Initializes and registers fields, including special support fields.
	 */
	public NormalProfile() {
		this(true);
	}

	/**
	 * Profile data.
	 */
	protected byte[] data;

	@Override
	public void create() {
		// create data
		data = new byte[FILE_LENGTH];
		// insert header & flag header
		ByteUtils.writeString(data, 0, header);
		ByteUtils.writeString(data, 0x218, flagH);
		// set start point fields
		StartPoint sp = ExeData.getStartPoint();
		if (sp != null) {
			try {
				setField(FIELD_MAP, -1, sp.map);
				setField(FIELD_X_POSITION, -1, (short) (sp.positionX * 32));
				setField(FIELD_Y_POSITION, -1, (short) (sp.positionY * 32));
				setField(FIELD_DIRECTION, -1, sp.direction);
				setField(FIELD_MAXIMUM_HEALTH, -1, sp.maxHealth);
				setField(FIELD_CURRENT_HEALTH, -1, sp.curHealth);
			} catch (ProfileFieldException e) {
				e.printStackTrace();
			}
		}
		// set loaded flag
		loaded = true;
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
		// set loaded flag
		loaded = true;
		// set loaded file
		loadedFile = file;
	}

	@Override
	public void save(File file) throws IOException {
		if (data == null)
			return;
		File backup = null;
		if (file.exists()) {
			// back up file just in case
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
		} else
			// create file to write to
			file.createNewFile();
		// start writing
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			if (backup != null) {
				// attempt to recover
				System.err.println("Error while saving profile! Attempting to recover backup.");
				e.printStackTrace();
				try (FileOutputStream fos = new FileOutputStream(file);
						FileInputStream fis = new FileInputStream(backup)) {
					byte[] data = new byte[FILE_LENGTH];
					fis.read(data);
					fos.write(data);
				} catch (Exception e2) {
					System.err.println("Error while recovering backup!");
					e2.printStackTrace();
				}
			}
		}
		// set loaded file
		loadedFile = file;
	}

	@Override
	public void unload() {
		data = null;
		loaded = false;
		loadedFile = null;
	}

	/**
	 * Initialize and register regular fields.
	 */
	protected void setupFields() {
		makeFieldMapAndPosition(0x008, 0x011, 0x015);
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
		makeFieldInts(FIELD_WEAPON_ID, 7, Integer.BYTES * 4, 0x038);
		makeFieldInts(FIELD_WEAPON_LEVEL, 7, Integer.BYTES * 4, 0x03C);
		makeFieldInts(FIELD_WEAPON_EXP, 7, Integer.BYTES * 4, 0x040);
		makeFieldInts(FIELD_WEAPON_MAXIMUM_AMMO, 7, Integer.BYTES * 4, 0x044);
		makeFieldInts(FIELD_WEAPON_CURRENT_AMMO, 7, Integer.BYTES * 4, 0x048);
		makeFieldInts(FIELD_ITEMS, 30, 0, 0x0D8);
		makeFieldInts(FIELD_WARP_ID, 7, Integer.BYTES, 0x158);
		makeFieldInts(FIELD_WARP_LOCATION, 7, Integer.BYTES, 0x15C);
		makeFieldBools(FIELD_MAP_FLAGS, 128, 0, 0x196, false);
		makeFieldFlags(FIELD_FLAGS, 8000, 0x21C);
	}

	/**
	 * Initialize and register special support fields.
	 */
	protected void setupFieldsExt() {
		try {
			addField(FIELD_MIM_COSTUME, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Long.TYPE;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Long;
				}

				@Override
				public Object getValue(int index) {
					long ret = 0;
					for (int i = 7968; i < 7995; i++)
						try {
							if ((boolean) getField(FIELD_FLAGS, i))
								ret |= 1 << (i - 7968);
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
							setField(FIELD_FLAGS, i, (v & (1 << (i - 7968))) != 0);
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

	/**
	 * Function to correct pointers.
	 */
	protected Function<Integer, Integer> ptrCorrector = t -> t;

	/**
	 * Creates a <code>byte</code> field.
	 *
	 * @param name
	 *            name of field
	 * @param ptr
	 *            pointer to field in profile
	 */
	protected void makeFieldByte(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Byte.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Byte;
				}

				@Override
				public Object getValue(int index) {
					return data[ptrCorrector.apply(ptr)];
				}

				@Override
				public void setValue(int index, Object value) {
					data[ptrCorrector.apply(ptr)] = (Byte) value;
				}

				@Override
				public boolean hasIndexes() {
					return false;
				}

				@Override
				public int getMinimumIndex() {
					return -1;
				}

				@Override
				public int getMaximumIndex() {
					return -1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>byte</code> array field.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of elements in field
	 * @param off
	 *            offset between each element in bytes
	 * @param ptr
	 *            pointer to first element in profile
	 */
	protected void makeFieldBytes(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Byte.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Byte;
				}

				@Override
				public Object getValue(int index) {
					byte[] ret = new byte[length];
					ByteUtils.readBytes(data, ptrCorrector.apply(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = ptrCorrector.apply(ptr);
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
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>short</code> field.
	 *
	 * @param name
	 *            name of field
	 * @param ptr
	 *            pointer to field in profile
	 */
	protected void makeFieldShort(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Short;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readShort(data, ptrCorrector.apply(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeShort(data, ptrCorrector.apply(ptr), (Short) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>short</code> array field.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of elements in field
	 * @param off
	 *            offset between each element in bytes
	 * @param ptr
	 *            pointer to first element in profile
	 */
	protected void makeFieldShorts(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Short;
				}

				@Override
				public Object getValue(int index) {
					short[] ret = new short[length];
					ByteUtils.readShorts(data, ptrCorrector.apply(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = ptrCorrector.apply(ptr);
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
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>int</code> field.
	 *
	 * @param name
	 *            name of field
	 * @param ptr
	 *            pointer to field in profile
	 */
	protected void makeFieldInt(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Integer;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readInt(data, ptrCorrector.apply(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeInt(data, ptrCorrector.apply(ptr), (Integer) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>boolean</code> field, with a byte representing a boolean: 0
	 * being <code>false</code>, and anything else being <code>true</code>.
	 *
	 * @param name
	 *            name of field
	 * @param ptr
	 *            pointer to field in profile
	 * @param type
	 *            <code>true</code> for sign bit, <code>false</code> for 0/1
	 */
	protected void makeFieldBool(String name, int ptr, boolean type) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Boolean.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Boolean;
				}

				@Override
				public Object getValue(int index) {
					byte flag = data[ptrCorrector.apply(ptr)];
					if (type)
						return Byte.toUnsignedInt(flag) > 0x7F;
					return flag == 0;
				}

				@Override
				public void setValue(int index, Object value) {
					byte flag = 0;
					if (type)
						flag = (byte) ((Boolean) value ? 0xFF : 0x7F);
					else
						flag = (byte) ((Boolean) value ? 1 : 0);
					data[ptrCorrector.apply(ptr)] = flag;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>boolean</code> array field, with a byte representing each
	 * boolean: 0
	 * being <code>false</code>, and anything else being <code>true</code>.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of elements in field
	 * @param off
	 *            offset between each element in bytes
	 * @param ptr
	 *            pointer to first element in profile
	 * @param type
	 *            <code>true</code> for sign bit, <code>false</code> for 0/1
	 */
	protected void makeFieldBools(String name, int length, int off, int ptr, boolean type) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Boolean.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Boolean;
				}

				@Override
				public Object getValue(int index) {
					byte[] ret = new byte[length];
					ByteUtils.readBytes(data, ptrCorrector.apply(ptr), off, ret);
					byte flag = ret[index];
					if (type)
						return Byte.toUnsignedInt(flag) > 0x7F;
					return flag == 0;
				}

				@Override
				public void setValue(int index, Object value) {
					boolean valBool = (Boolean) value;
					byte actualVal = 0;
					if (type) {
						actualVal = 0x7F;
						if (valBool)
							actualVal = (byte) 0xFF;
					} else if (valBool)
						actualVal = 1;
					int cptr = ptrCorrector.apply(ptr);
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
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>int</code> array field.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of elements in field
	 * @param off
	 *            offset between each element in bytes
	 * @param ptr
	 *            pointer to first element in profile
	 */
	protected void makeFieldInts(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Integer;
				}

				@Override
				public Object getValue(int index) {
					int[] ret = new int[length];
					ByteUtils.readInts(data, ptrCorrector.apply(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = ptrCorrector.apply(ptr);
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
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>long</code> field.
	 *
	 * @param name
	 *            name of field
	 * @param ptr
	 *            pointer to field in profile
	 */
	protected void makeFieldLong(String name, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Long.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Long;
				}

				@Override
				public Object getValue(int index) {
					return ByteUtils.readLong(data, ptrCorrector.apply(ptr));
				}

				@Override
				public void setValue(int index, Object value) {
					ByteUtils.writeLong(data, ptrCorrector.apply(ptr), (Long) value);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a <code>long</code> array field.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of elements in field
	 * @param off
	 *            offset between each element in bytes
	 * @param ptr
	 *            pointer to first element in profile
	 */
	protected void makeFieldLongs(String name, int length, int off, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Long.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Long;
				}

				@Override
				public Object getValue(int index) {
					long[] ret = new long[length];
					ByteUtils.readLongs(data, ptrCorrector.apply(ptr), off, ret);
					return ret[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = ptrCorrector.apply(ptr);
					long[] vals = new long[length];
					ByteUtils.readLongs(data, cptr, off, vals);
					vals[index] = (Long) value;
					ByteUtils.writeLongs(data, cptr, off, vals);
				}

				@Override
				public boolean hasIndexes() {
					return true;
				}

				@Override
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a bitflag field. Every 8 bitflags take up one byte.
	 *
	 * @param name
	 *            name of field
	 * @param length
	 *            number of bitflags in field
	 * @param ptr
	 *            pointer to first byte of field
	 */
	protected void makeFieldFlags(String name, int length, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Boolean.class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					return value instanceof Boolean;
				}

				@Override
				public Object getValue(int index) {
					boolean[] vals = new boolean[length];
					ByteUtils.readFlags(data, ptrCorrector.apply(ptr), vals);
					return vals[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int cptr = ptrCorrector.apply(ptr);
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
				public int getMinimumIndex() {
					return 0;
				}

				@Override
				public int getMaximumIndex() {
					return length - 1;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates {@linkplain #FIELD_MAP_AND_POSITION the map & position "field"}.
	 *
	 * @param mapPtr
	 *            pointer to map field
	 * @param xPtr
	 *            pointer to X position field
	 * @param yPtr
	 *            pointer to Y position field
	 */
	protected void makeFieldMapAndPosition(int mapPtr, int xPtr, int yPtr) {
		try {
			addField(FIELD_MAP_AND_POSITION, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer[].class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					if (!(value instanceof Integer[]))
						return false;
					return ((Integer[]) value).length >= 3;
				}

				@Override
				public Object getValue(int index) {
					Integer[] ret = new Integer[3];
					ret[0] = ByteUtils.readInt(data, mapPtr);
					ret[1] = (int) ByteUtils.readShort(data, ptrCorrector.apply(xPtr));
					ret[2] = (int) ByteUtils.readShort(data, ptrCorrector.apply(yPtr));
					return ret;
				}

				private short int2Short(int i) {
					return (short) Math.min(Math.max(i, Short.MIN_VALUE), Short.MAX_VALUE);
				}

				@Override
				public void setValue(int index, Object value) {
					Integer[] vals = (Integer[]) value;
					ByteUtils.writeInt(data, ptrCorrector.apply(mapPtr), vals[0]);
					ByteUtils.writeShort(data, ptrCorrector.apply(xPtr), int2Short(vals[1]));
					ByteUtils.writeShort(data, ptrCorrector.apply(yPtr), int2Short(vals[2]));
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates {@linkplain #FIELD_POSITION the position "field"}.
	 *
	 * @param xPtr
	 *            pointer to X position field
	 * @param yPtr
	 *            pointer to Y position field
	 */
	protected void makeFieldPosition(int xPtr, int yPtr) {
		try {
			addField(FIELD_POSITION, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Short[].class;
				}

				@Override
				public boolean acceptsValue(int index, Object value) {
					if (!(value instanceof Short[]))
						return false;
					return ((Short[]) value).length >= 2;
				}

				@Override
				public Object getValue(int index) {
					Short[] ret = new Short[2];
					ret[0] = ByteUtils.readShort(data, ptrCorrector.apply(xPtr));
					ret[1] = ByteUtils.readShort(data, ptrCorrector.apply(yPtr));
					return ret;
				}

				@Override
				public void setValue(int index, Object value) {
					Short[] vals = (Short[]) value;
					ByteUtils.writeShort(data, ptrCorrector.apply(xPtr), vals[0]);
					ByteUtils.writeShort(data, ptrCorrector.apply(yPtr), vals[1]);
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

}

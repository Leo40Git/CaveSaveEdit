package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
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
		setupFields();
		if (extFields)
			setupFieldsExt();
	}

	public NormalProfile() {
		this(true);
	}

	protected byte[] data;

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
	}

	protected void setupFields() {
		makeFieldInt(FIELD_MAP, 0x008);
		makeFieldInt(FIELD_SONG, 0x00C);
		makeFieldShort(FIELD_X_POSITION, 0x011);
		makeFieldShort(FIELD_Y_POSITION, 0x015);
		makeFieldPosition();
		makeFieldInt(FIELD_DIRECTION, 0x018);
		makeFieldShort(FIELD_MAXIMUM_HEALTH, 0x01C);
		makeFieldShort(FIELD_STAR_COUNT, 0x01E);
		makeFieldShort(FIELD_CURRENT_HEALTH, 0x020);
		makeFieldInt(FIELD_CURRENT_WEAPON, 0x024);
		makeFieldFlags(FIELD_EQUIPS, 16, 0x02C);
		makeFieldInt(FIELD_TIME_PLAYED, 0x034);
		// TODO weapons
		makeFieldInts(FIELD_ITEMS, 30, 0x0D8);
		// TODO warps
		makeFieldFlags(FIELD_FLAGS, 8000, 0x21C);
	}

	protected void setupFieldsExt() {

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

	protected void makeFieldPosition() {
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
					try {
						Short x = (Short) getField(FIELD_X_POSITION);
						Short y = (Short) getField(FIELD_Y_POSITION);
						return new Short[] { x, y };
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public void setValue(int index, Object value) {
					try {
						Short[] pos = (Short[]) value;
						setField(FIELD_X_POSITION, pos[0]);
						setField(FIELD_Y_POSITION, pos[1]);
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
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

	protected void makeFieldInts(String name, int length, int ptr) {
		try {
			addField(name, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Integer[].class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Integer[];
				}

				@Override
				public Object getValue(int index) {
					int[] vals = new int[length];
					ByteUtils.readInts(data, ptr, vals);
					return vals[index];
				}

				@Override
				public void setValue(int index, Object value) {
					int[] vals = new int[length];
					ByteUtils.readInts(data, ptr, vals);
					vals[index] = (Integer) value;
					ByteUtils.writeInts(data, ptr, vals);
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

}

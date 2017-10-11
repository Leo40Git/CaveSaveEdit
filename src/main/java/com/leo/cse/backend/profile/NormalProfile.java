package com.leo.cse.backend.profile;

public class NormalProfile extends Profile {

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
	/**
	 * The profile header string.
	 */
	private String header = DEFAULT_HEADER;
	/**
	 * The flag section header string.
	 */
	private String flagH = DEFAULT_FLAGH;

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the flagH
	 */
	public String getFlagHeader() {
		return flagH;
	}

	/**
	 * @param flagH the flagH to set
	 */
	public void setFlagHeader(String flagH) {
		this.flagH = flagH;
	}

	@Override
	public boolean hasField(String field) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<?> getFieldType(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getField(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setField(String field, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasFunction(String func) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getFunctionArgNum(String func) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Class<?>[] getFunctionArgType(String func) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doFunction(String func, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getFunctionRetType(String func) {
		// TODO Auto-generated method stub
		return null;
	}

}

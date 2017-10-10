package com.leo.cse.backend.profile;

public class PlusProfile extends Profile {

	/**
	 * The expected CS+ file section length.
	 */
	public static final int SECTION_LENGTH = 0x620;
	/**
	 * The expected CS+ file length.
	 */
	public static final int FILE_LENGTH = 0x20020;
	
	/**
	 * The profile header string.
	 */
	private String header = NormalProfile.DEFAULT_HEADER;
	/**
	 * The flag section header string.
	 */
	private String flagH = NormalProfile.DEFAULT_FLAGH;
	
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

}

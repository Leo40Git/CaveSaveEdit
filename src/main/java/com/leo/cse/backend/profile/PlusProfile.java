package com.leo.cse.backend.profile;

public class PlusProfile extends NormalProfile {
	
	/**
	 * The expected CS+ file section length.
	 */
	public static final int SECTION_LENGTH = 0x620;
	/**
	 * The expected CS+ file length.
	 */
	public static final int FILE_LENGTH = 0x20020;
	
	public PlusProfile() {
		super(false);
	}

}

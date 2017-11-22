package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.leo.cse.backend.ByteUtils;

public class PlusProfile extends NormalProfile {

	/**
	 * The expected CS+ file section length.
	 */
	public static final int SECTION_LENGTH = 0x620;
	/**
	 * The expected CS+ file length.
	 */
	public static final int FILE_LENGTH = 0x20020;

	/**
	 * "Last modified" date in Unix time.
	 */
	public static final String FIELD_MODIFY_DATE = "modify_date";

	public static final String FIELD_DIFFICULTY = "difficulty";

	public static final String FIELD_BEAT_HELL = "beat_hell";

	/**
	 * Clones one file to another.<br/>
	 * 
	 * @param A0
	 *            {@link Integer}, slot to duplicate.
	 * @param A1
	 *            {@link Integer}, slot to insert duplicate into.
	 */
	public static final String METHOD_CLONE_FILE = "file.clone";

	/**
	 * Creates a new file.<br/>
	 * 
	 * @param A0
	 *            {@link Integer}, slot to initialize.
	 */
	public static final String METHOD_NEW_FILE = "file.new";

	/**
	 * Deletes a file.<br/>
	 * 
	 * @param A0
	 *            {@link Integer}, slot to clear.
	 */
	public static final String METHOD_DELETE_FILE = "file.delete";

	public PlusProfile() {
		super(false);
		setupFieldsPlus();
		setupMethodsPlus();
	}

	protected void setupFieldsPlus() {
		makeFieldLong(FIELD_MODIFY_DATE, 0x608);
		makeFieldShort(FIELD_DIFFICULTY, 0x610);
		try {
			addField(FIELD_BEAT_HELL, new ProfileField() {
				@Override
				public Class<?> getType() {
					return Boolean.class;
				}

				@Override
				public boolean acceptsValue(Object value) {
					return value instanceof Short;
				}

				@Override
				public Object getValue(int index) {
					byte flag = data[0x1F04D];
					return (flag == 0 ? false : true);
				}

				@Override
				public void setValue(int index, Object value) {
					byte flag = (byte) ((Boolean) value ? 1 : 0);
					data[0x1F04D] = flag;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void setupMethodsPlus() {
		try {
			final String[] dataMod = new String[] { EVENT_DATA_MODIFIED };
			addMethod(METHOD_CLONE_FILE, new ProfileMethod() {

				private final Class<?>[] argTypes = new Class<?>[] { Integer.class, Integer.class };

				@Override
				public Class<?>[] getArgTypes() {
					return argTypes;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public String[] getModifiedFields() {
					return dataMod;
				}

				@Override
				public Object call(Object... args) {
					int srcSec = (int) args[0];
					int dstSec = (int) args[1];
					System.arraycopy(data, srcSec * SECTION_LENGTH, data, dstSec * SECTION_LENGTH, SECTION_LENGTH);
					return null;
				}

			});
			final Class<?>[] oneInt = new Class<?>[] { Integer.class };
			addMethod(METHOD_NEW_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return oneInt;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public String[] getModifiedFields() {
					return dataMod;
				}

				@Override
				public Object call(Object... args) {
					int secToReplace = (int) args[0];
					byte[] newData = new byte[SECTION_LENGTH];
					ByteUtils.writeString(newData, 0, header);
					ByteUtils.writeString(newData, 0x218, flagH);
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					return null;
				}

			});
			addMethod(METHOD_DELETE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return oneInt;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public String[] getModifiedFields() {
					return dataMod;
				}

				@Override
				public Object call(Object... args) {
					int secToReplace = (int) args[0];
					byte[] newData = new byte[SECTION_LENGTH];
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					return null;
				}

			});
		} catch (ProfileMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean supportsSections() {
		return true;
	}

	@Override
	public void load(File file, int section) throws IOException {
		// read data
		data = new byte[FILE_LENGTH];
		try (FileInputStream fis = new FileInputStream(file)) {
			if (fis.read(data) < data.length)
				throw new IOException("file is too small");
		}
		int off = SECTION_LENGTH * section;
		// check header
		String profHeader = ByteUtils.readString(data, off, header.length());
		if (!header.equals(profHeader))
			throw new IOException("Invalid file header!");
		// check flag header
		String profFlagH = ByteUtils.readString(data, off + 0x218, flagH.length());
		if (!flagH.equals(profFlagH))
			throw new IOException("Flag header is missing!");
		// set loaded file & section
		loadedFile = file;
		loadedSection = section;
	}

	@Override
	public void loadSection(int section) throws IOException {
		if (loadedFile == null)
			return;
		load(loadedFile, section);
	}

	@Override
	public void save(File file, int section) throws IOException {
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

	@Override
	protected int correctPointer(int ptr) {
		return SECTION_LENGTH * loadedSection + ptr;
	}

}

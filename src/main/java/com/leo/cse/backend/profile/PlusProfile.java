package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.leo.cse.backend.ByteUtils;
import com.leo.cse.backend.profile.ProfileManager.FieldModChangeRecorder;

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

	public static final String METHOD_FILE_EXISTS = "file.exists";

	public static final String METHOD_GET_ACTIVE_FILE = "file.active.get";

	public static final String METHOD_SET_ACTIVE_FILE = "file.active.set";

	public static final String METHOD_PUSH_ACTIVE_FILE = "file.active.push";

	public static final String METHOD_POP_ACTIVE_FILE = "file.active.pop";

	private int curSection = -1;
	private List<Integer> secQueue;

	@Override
	protected int correctPointer(int ptr) {
		return curSection * SECTION_LENGTH + ptr;
	}

	public PlusProfile() {
		super(false);
		secQueue = new ArrayList<>();
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
					return value instanceof Boolean;
				}

				@Override
				public Object getValue(int index) {
					byte flag = data[0x1F04C];
					return (flag == 0 ? false : true);
				}

				@Override
				public void setValue(int index, Object value) {
					byte flag = (byte) ((Boolean) value ? 1 : 0);
					data[0x1F04C] = flag;
				}
			});
		} catch (ProfileFieldException e) {
			e.printStackTrace();
		}
	}

	protected void setupMethodsPlus() {
		try {
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
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					int srcSec = (int) args[0];
					int dstSec = (int) args[1];
					System.arraycopy(data, srcSec * SECTION_LENGTH, data, dstSec * SECTION_LENGTH, SECTION_LENGTH);
					fmcr.addChange(EVENT_DATA_MODIFIED, -1, null, null);
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
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					int secToReplace = (int) args[0];
					byte[] newData = new byte[SECTION_LENGTH];
					ByteUtils.writeString(newData, 0, header);
					ByteUtils.writeString(newData, 0x218, flagH);
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					fmcr.addChange(EVENT_DATA_MODIFIED, -1, null, null);
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
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					int secToReplace = (int) args[0];
					byte[] newData = new byte[SECTION_LENGTH];
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					fmcr.addChange(EVENT_DATA_MODIFIED, -1, null, null);
					return null;
				}

			});
			addMethod(METHOD_FILE_EXISTS, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return oneInt;
				}

				@Override
				public Class<?> getRetType() {
					return Boolean.class;
				}

				@Override
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					int secToChk = (int) args[0];
					int ptr = secToChk * SECTION_LENGTH;
					// check header
					String profHeader = ByteUtils.readString(data, ptr, header.length());
					if (!header.equals(profHeader))
						return false;
					// check flag header
					String profFlagH = ByteUtils.readString(data, ptr + 0x218, flagH.length());
					if (!flagH.equals(profFlagH))
						return false;
					return true;
				}

			});
			addMethod(METHOD_GET_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return null;
				}

				@Override
				public Class<?> getRetType() {
					return Integer.class;
				}

				@Override
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					return curSection;
				}

			});
			addMethod(METHOD_SET_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return oneInt;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					curSection = (int) args[0];
					return null;
				}

			});
			addMethod(METHOD_PUSH_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return oneInt;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					int newSec = (int) args[0];
					secQueue.add(curSection);
					curSection = newSec;
					return null;
				}

			});
			addMethod(METHOD_POP_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return null;
				}

				@Override
				public Class<?> getRetType() {
					return null;
				}

				@Override
				public Object call(FieldModChangeRecorder fmcr, Object... args) {
					if (secQueue.isEmpty())
						return null;
					curSection = secQueue.remove(0);
					return null;
				}

			});
		} catch (ProfileMethodException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void create() {
		// create data
		data = new byte[FILE_LENGTH];
		// set loaded file to null & set section
		loadedFile = null;
		curSection = 0;
	}

	@Override
	public void load(File file) throws IOException {
		// read data
		data = new byte[FILE_LENGTH];
		try (FileInputStream fis = new FileInputStream(file)) {
			if (fis.read(data) < data.length)
				throw new IOException("file is too small");
		}
		// set loaded file & section
		loadedFile = file;
		curSection = 0;
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

}

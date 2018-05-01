package com.leo.cse.backend.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.leo.cse.backend.ByteUtils;
import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.exe.ExeData.StartPoint;
import com.leo.cse.backend.profile.ProfileManager.FieldChangeRecorder;
import com.leo.cse.backend.profile.ProfileManager.ProfileFieldException;
import com.leo.cse.backend.profile.ProfileManager.ProfileMethodException;

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

	/**
	 * Difficulty: 0-1 for Original, 2-3 for Easy, 4-5 for Hard, wraps around (6 =
	 * 0, 7 = 1, etc.)
	 */
	public static final String FIELD_DIFFICULTY = "difficulty";

	/**
	 * Used normal slots.
	 */
	public static final String FIELD_USED_SLOTS_NORMAL = "used_slots.normal";

	/**
	 * Used Curly Story slots.
	 */
	public static final String FIELD_USED_SLOTS_CURLY = "used_slots.curly";

	/**
	 * Music volume. Goes from 1-10 (0 is the same as 1 and 10+ is earrape
	 * territory).
	 */
	public static final String FIELD_MUSIC_VOLUME = "music_volume";

	/**
	 * Sound volume. Goes from 1-10 (0 is the same as 1 and 10+ is earrape
	 * territory).
	 */
	public static final String FIELD_SOUND_VOLUME = "sound_volume";

	/**
	 * Soundtrack type. 0/1 for Remastered, 2 for Original, 3 for New.
	 */
	public static final String FIELD_SOUNDTRACK_TYPE = "soundtrack_typee";

	/**
	 * Graphics style. 0 for New, 1 for Original.
	 */
	public static final String FIELD_GRAPHICS_STYLE = "graphics_style";

	/**
	 * Language. 0 for English, 1 for Japanese.
	 */
	public static final String FIELD_LANGUAGE = "language";

	/**
	 * "Beat Bloodstained Sanctuary" flag. Unlocks Sanctuary Time Attack.
	 */
	public static final String FIELD_BEAT_HELL = "beat_hell";

	/**
	 * Best Bloodstained Sanctuary time.
	 */
	public static final String FIELD_BEST_HELL_TIME = "best_hell_time";

	/**
	 * "Jukebox unlocked" flag.
	 */
	public static final String FIELD_JUKEBOX_UNLOCK = "jukebox_unlock";

	/**
	 * Best challenge/mod times. Slots 8+ don't have a "best time" index.
	 */
	public static final String FIELD_BEST_MOD_TIMES = "best_mod_times";

	/**
	 * Clones one file to another.
	 *
	 * @param A0
	 *            {@link Integer}, slot to duplicate.
	 * @param A1
	 *            {@link Integer}, slot to insert duplicate into.
	 */
	public static final String METHOD_CLONE_FILE = "file.clone";

	/**
	 * Creates a new file.
	 *
	 * @param A0
	 *            {@link Integer}, slot to initialize.
	 */
	public static final String METHOD_NEW_FILE = "file.new";

	/**
	 * Deletes a file.
	 *
	 * @param A0
	 *            {@link Integer}, slot to clear.
	 */
	public static final String METHOD_DELETE_FILE = "file.delete";

	/**
	 * Checks if a file exists.
	 *
	 * @param A0
	 *            {@link Integer}, slot to check.
	 * @return {@link Boolean}, <code>true</code> if slot is filled,
	 *         <code>false</code> otherwise
	 */
	public static final String METHOD_FILE_EXISTS = "file.exists";

	/**
	 * Gets the currently active file.
	 *
	 * @return {@link Integer}, currently selected slot.
	 */
	public static final String METHOD_GET_ACTIVE_FILE = "file.active.get";

	/**
	 * Sets the currently active file.
	 *
	 * @param A0
	 *            {@link Integer}, slot to select.
	 */
	public static final String METHOD_SET_ACTIVE_FILE = "file.active.set";

	/**
	 * Pushes a new active file.
	 *
	 * @param A0
	 *            {@link Integer}, slot to select.
	 */
	public static final String METHOD_PUSH_ACTIVE_FILE = "file.active.push";

	/**
	 * Pops an older active file.
	 */
	public static final String METHOD_POP_ACTIVE_FILE = "file.active.pop";

	/**
	 * Currently active file.
	 */
	private int curSection = -1;
	/**
	 * Active file queue for {@link #METHOD_PUSH_ACTIVE_FILE} and
	 * {@link #METHOD_POP_ACTIVE_FILE}.
	 */
	private List<Integer> secQueue;

	/**
	 * Initializes and registers fields and methods.
	 */
	public PlusProfile() {
		super(false);
		ptrCorrector = ptr -> {
			// there are variables beyond the 6 save files, so if the pointer is higher than
			// (SECTION_LENGTH * 6), it should be returned as-is
			if (ptr > SECTION_LENGTH * 6)
				return ptr;
			// make sure pointer is in correct section
			while (ptr > SECTION_LENGTH)
				ptr -= SECTION_LENGTH;
			return curSection * SECTION_LENGTH + ptr;
		};
		secQueue = new ArrayList<>();
		setupFieldsPlus();
		setupMethodsPlus();
	}

	/**
	 * Initializes and registers fields exclusive to Cave Story+.
	 */
	protected void setupFieldsPlus() {
		makeFieldLong(FIELD_MODIFY_DATE, 0x608);
		makeFieldShort(FIELD_DIFFICULTY, 0x610);
		makeFieldFlags(FIELD_USED_SLOTS_NORMAL, 3, 0x1F020);
		makeFieldFlags(FIELD_USED_SLOTS_CURLY, 3, 0x1F021);
		makeFieldLong(FIELD_MUSIC_VOLUME, 0x1F1040);
		makeFieldLong(FIELD_SOUND_VOLUME, 0x1F1044);
		makeFieldByte(FIELD_SOUNDTRACK_TYPE, 0x1F1049);
		makeFieldByte(FIELD_GRAPHICS_STYLE, 0x1F104A);
		makeFieldByte(FIELD_LANGUAGE, 0x1F104B);
		makeFieldBool(FIELD_BEAT_HELL, 0x1F04C, false);
		makeFieldLong(FIELD_BEST_HELL_TIME, 0x1F1054);
		makeFieldBool(FIELD_JUKEBOX_UNLOCK, 0x1F105B, true);
		makeFieldLongs(FIELD_BEST_MOD_TIMES, 6, 0, 0x1F105C);
	}

	/**
	 * Initializes and registers methods exclusive to Cave Story+.
	 */
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
					int srcSec = (int) args[0];
					int dstSec = (int) args[1];
					System.arraycopy(data, srcSec * SECTION_LENGTH, data, dstSec * SECTION_LENGTH, SECTION_LENGTH);
					String field = FIELD_USED_SLOTS_NORMAL;
					if (dstSec > 2) {
						int oldSec = curSection;
						curSection = dstSec;
						try {
							setField(FIELD_DIFFICULTY, -1, (short) 0);
						} catch (ProfileFieldException e) {
							e.printStackTrace();
						}
						curSection = oldSec;
						dstSec -= 3;
						field = FIELD_USED_SLOTS_CURLY;
					}
					try {
						setField(field, dstSec, true);
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					fcr.addChange(ProfileManager.EVENT_DATA_MODIFIED, -1, null, null);
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
					int secToReplace = (int) args[0];
					final int newSection = secToReplace;
					byte[] newData = new byte[SECTION_LENGTH];
					ByteUtils.writeString(newData, 0, header);
					ByteUtils.writeString(newData, 0x218, flagH);
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					String field = FIELD_USED_SLOTS_NORMAL;
					if (secToReplace > 2) {
						secToReplace -= 3;
						field = FIELD_USED_SLOTS_CURLY;
					}
					try {
						setField(field, secToReplace, true);
						callMethod(METHOD_PUSH_ACTIVE_FILE, fcr, newSection);
						// set start point fields
						StartPoint sp = ExeData.getStartPoint();
						if (sp != null) {
							try {
								setField(FIELD_MAP, -1, sp.map);
								setField(FIELD_X_POSITION, -1, sp.positionX);
								setField(FIELD_Y_POSITION, -1, sp.positionY);
								setField(FIELD_DIRECTION, -1, sp.direction);
								setField(FIELD_MAXIMUM_HEALTH, -1, sp.maxHealth);
								setField(FIELD_CURRENT_HEALTH, -1, sp.curHealth);
							} catch (ProfileFieldException e) {
								e.printStackTrace();
							}
						}
						callMethod(METHOD_POP_ACTIVE_FILE, fcr);
					} catch (ProfileFieldException | ProfileMethodException e) {
						e.printStackTrace();
					}
					fcr.addChange(ProfileManager.EVENT_DATA_MODIFIED, -1, null, null);
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
					int secToReplace = (int) args[0];
					byte[] newData = new byte[SECTION_LENGTH];
					System.arraycopy(newData, 0, data, secToReplace * SECTION_LENGTH, SECTION_LENGTH);
					String field = FIELD_USED_SLOTS_NORMAL;
					if (secToReplace > 2) {
						secToReplace -= 3;
						field = FIELD_USED_SLOTS_CURLY;
					}
					try {
						setField(field, secToReplace, false);
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					fcr.addChange(ProfileManager.EVENT_DATA_MODIFIED, -1, null, null);
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
					int secToChk = (int) args[0];
					String field = FIELD_USED_SLOTS_NORMAL;
					if (secToChk > 2) {
						secToChk -= 3;
						field = FIELD_USED_SLOTS_CURLY;
					}
					boolean exists = false;
					try {
						exists = (boolean) getField(field, secToChk);
					} catch (ProfileFieldException e) {
						e.printStackTrace();
					}
					return exists;
				}

			});
			addMethod(METHOD_GET_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return ProfileMethod.NO_ARGS;
				}

				@Override
				public Class<?> getRetType() {
					return Integer.class;
				}

				@Override
				public Object call(FieldChangeRecorder fcr, Object... args) {
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
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
				public Object call(FieldChangeRecorder fcr, Object... args) {
					int newSec = (int) args[0];
					secQueue.add(curSection);
					curSection = newSec;
					return null;
				}

			});
			addMethod(METHOD_POP_ACTIVE_FILE, new ProfileMethod() {

				@Override
				public Class<?>[] getArgTypes() {
					return ProfileMethod.NO_ARGS;
				}

				@Override
				public Class<?> getRetType() {
					return Integer.class;
				}

				@Override
				public Object call(FieldChangeRecorder fcr, Object... args) {
					if (secQueue.isEmpty())
						return null;
					curSection = secQueue.remove(0);
					return curSection;
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
		loaded = true;
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
		loaded = true;
		loadedFile = file;
		curSection = 0;
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

}

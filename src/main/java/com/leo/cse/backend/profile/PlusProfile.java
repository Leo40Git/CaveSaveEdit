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

	public PlusProfile() {
		super(false);
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

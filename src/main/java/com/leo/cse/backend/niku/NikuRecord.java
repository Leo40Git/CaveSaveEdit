package com.leo.cse.backend.niku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.backend.ByteUtils;

public class NikuRecord {

	private static boolean modified;

	public static boolean isModified() {
		return modified;
	}

	private static UndoManager undoMan;

	static class NikuEdit implements UndoableEdit {

		private int oldValue;
		private int newValue;
		private boolean hasBeenUndone;

		public NikuEdit(int oldValue, int newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public void undo() throws CannotUndoException {
			setValue(oldValue, false);
			hasBeenUndone = true;
		}

		@Override
		public boolean canUndo() {
			return !hasBeenUndone;
		}

		@Override
		public void redo() throws CannotRedoException {
			setValue(newValue, false);
			hasBeenUndone = false;
		}

		@Override
		public boolean canRedo() {
			return hasBeenUndone;
		}

		@Override
		public void die() {
			oldValue = 0;
			newValue = 0;
		}

		@Override
		public boolean addEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			return false;
		}

		@Override
		public boolean isSignificant() {
			return true;
		}

		@Override
		public String getPresentationName() {
			return "290.rec time";
		}

		@Override
		public String getUndoPresentationName() {
			return "undo " + getPresentationName() + " to " + oldValue;
		}

		@Override
		public String getRedoPresentationName() {
			return "redo " + getPresentationName() + " to " + newValue;
		}

	}

	public static boolean canUndo() {
		if (undoMan == null)
			return false;
		return undoMan.canUndo();
	}

	/**
	 * Undoes an edit.
	 */
	public static void undo() {
		if (!canUndo())
			return;
		undoMan.undo();
	}

	public static boolean canRedo() {
		if (undoMan == null)
			return false;
		return undoMan.canRedo();
	}

	/**
	 * Redoes an edit.
	 */
	public static void redo() {
		if (!canRedo())
			return;
		undoMan.redo();
	}

	private static int value;
	private static File file;

	public static File getFile() {
		return file;
	}

	private static boolean loaded;

	public static boolean isLoaded() {
		return loaded;
	}

	private static byte unsigned(byte b) {
		return (byte) (b & 0xFF);
	}

	public static void create() {
		value = 0;
		file = null;
		loaded = true;
		modified = false;
		undoMan = new UndoManager();
	}

	public static void load(File src) throws IOException {
		int[] result = new int[4];
		byte[] buf = new byte[20];
		FileInputStream fis = new FileInputStream(src);
		fis.read(buf);
		fis.close();
		for (int i = 0; i < 4; i++) {
			byte key = unsigned(buf[i + 16]);
			System.out.println("result " + i + ": key=" + key);
			int j = i * 4;
			buf[j] = (byte) (buf[j] - key);
			System.out.println("buf[" + j + "]=" + buf[j]);
			buf[j + 1] = (byte) (buf[j + 1] - key);
			System.out.println("buf[" + (j + 1) + "]=" + buf[j + 1]);
			buf[j + 2] = (byte) (buf[j + 2] - key);
			System.out.println("buf[" + (j + 2) + "]=" + buf[j + 2]);
			buf[j + 3] = (byte) (buf[j + 3] - (byte) (unsigned(key) / 2));
			System.out.println("buf[" + (j + 3) + "]=" + buf[j + 3]);
			result[i] = ByteUtils.readInt(buf, j);
		}
		System.out.println("result[0]=" + result[0]);
		System.out.println("result[1]=" + result[1]);
		System.out.println("result[2]=" + result[2]);
		System.out.println("result[3]=" + result[3]);
		if (result[0] != result[1] || result[0] != result[2] || result[0] != result[3])
			throw new IOException("290.rec file is corrupt");
		value = result[0];
		file = src;
		loaded = true;
		modified = false;
		undoMan = new UndoManager();
	}

	public static void save(File dest) throws IOException {
		if (dest == null)
			dest = file;
		byte[] bufByte = new byte[20];
		int[] bufInt = new int[4];
		bufInt[0] = value;
		bufInt[1] = value;
		bufInt[2] = value;
		bufInt[3] = value;
		ByteUtils.writeInts(bufByte, 0, 0, bufInt);
		Random r = new Random();
		bufByte[16] = (byte) r.nextInt(0xFF);
		bufByte[17] = (byte) r.nextInt(0xFF);
		bufByte[18] = (byte) r.nextInt(0xFF);
		bufByte[19] = (byte) r.nextInt(0xFF);
		for (int i = 0; i < 4; i++) {
			byte key = unsigned(bufByte[i + 16]);
			int j = i * 4;
			bufByte[j] = (byte) (bufByte[j] + key);
			bufByte[j + 1] = (byte) (bufByte[j + 1] + key);
			bufByte[j + 2] = (byte) (bufByte[j + 2] + key);
			bufByte[j + 3] = (byte) (bufByte[j + 3] + (byte) (unsigned(key) / 2));
		}
		FileOutputStream fos = new FileOutputStream(dest);
		fos.write(bufByte);
		fos.close();
		file = dest;
		modified = false;
	}

	public static void unload() {
		file = null;
		loaded = false;
		value = 0;
		modified = false;
		undoMan = null;
	}

	public static int getValue() {
		return value;
	}

	public static void setValue(int value, boolean addUndo) {
		if (value < 0)
			value = 0;
		if (value > 299999)
			value = 299999;
		if (value != NikuRecord.value) {
			modified = true;
			if (undoMan != null && addUndo)
				undoMan.addEdit(new NikuEdit(NikuRecord.value, value));
		}
		NikuRecord.value = value;
	}

	public static void setValue(int value) {
		setValue(value, true);
	}

	public static int getTenths() {
		return (value / 5) % 10;
	}

	public static int getSeconds() {
		return (value / 50) % 60;
	}

	public static int getMinutes() {
		return value / 3000;
	}

	public static void setTime(int tens, int seconds, int minutes) {
		int value = tens * 5;
		value += seconds * 50;
		value += minutes * 3000;
		setValue(value);
	}

	public static void setTenths(int tens) {
		setTime(tens, getSeconds(), getMinutes());
	}

	public static void setSeconds(int seconds) {
		setTime(getTenths(), seconds, getMinutes());
	}

	public static void setMinutes(int minutes) {
		setTime(getTenths(), getSeconds(), minutes);
	}

}

package com.leo.cse.backend.niku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.leo.cse.backend.ByteUtils;

public class NikuRecord {

	private static List<NikuListener> listeners;
	private static boolean modified;

	public static boolean isModified() {
		return modified;
	}

	public static void addListener(NikuListener l) {
		if (listeners == null)
			listeners = new LinkedList<>();
		listeners.add(l);
	}

	public static void removeListener(NikuListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	private static void notifyListeners(int oldValue, int newValue, boolean addUndo) {
		if (listeners == null)
			return;
		modified = true;
		if (undoMan != null && addUndo)
			undoMan.addEdit(new NikuEdit(oldValue, newValue));
		for (NikuListener l : listeners)
			l.onChange(oldValue, newValue);
	}

	private static UndoManager undoMan;

	public static class NikuEdit implements UndoableEdit {

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
			hasBeenUndone = true;
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

	/**
	 * Undoes an edit.
	 */
	public static void undo() {
		if (undoMan == null || !undoMan.canUndo())
			return;
		undoMan.undo();
	}

	/**
	 * Redoes an edit.
	 */
	public static void redo() {
		if (undoMan == null || !undoMan.canRedo())
			return;
		undoMan.redo();
	}

	private static int value;
	private static File file;

	public static void read(File src) throws IOException {
		int[] result = new int[4];
		byte[] buf = new byte[20];
		FileInputStream fis = new FileInputStream(src);
		fis.read(buf);
		fis.close();
		for (int i = 0; i < 4; i++) {
			byte key = (byte) (buf[i + 16] & 0xFF);
			System.out.println("result " + i + ": key=" + key);
			int j = i * 4;
			buf[j] = (byte) (buf[j] - key);
			System.out.println("buf[" + j + "]=" + buf[j]);
			buf[j + 1] = (byte) (buf[j + 1] - key);
			System.out.println("buf[" + (j + 1) + "]=" + buf[j + 1]);
			buf[j + 2] = (byte) (buf[j + 2] - key);
			System.out.println("buf[" + (j + 2) + "]=" + buf[j + 2]);
			buf[j + 3] = (byte) (buf[j + 3] - (byte) ((key & 0xFF) / 2));
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
		System.out.println("loaded 290.rec with value " + value);
		undoMan = new UndoManager();
		file = src;
	}

	public static void write(File dest) throws IOException {
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
		bufByte[16] = (byte) r.nextInt(255);
		bufByte[17] = (byte) r.nextInt(255);
		bufByte[18] = (byte) r.nextInt(255);
		bufByte[19] = (byte) r.nextInt(255);
		for (int i = 0; i < 4; i++) {
			byte key = (byte) (bufByte[i + 16] & 0xFF);
			int j = i * 4;
			bufByte[j] = (byte) (bufByte[j] + key);
			bufByte[j + 1] = (byte) (bufByte[j + 1] + key);
			bufByte[j + 2] = (byte) (bufByte[j + 2] + key);
			bufByte[j + 3] = (byte) (bufByte[j + 3] + (byte) ((key & 0xFF) / 2));
		}
		FileOutputStream fos = new FileOutputStream(dest);
		fos.write(bufByte);
		fos.close();
	}

	public static int getValue() {
		return value;
	}

	public static void setValue(int value, boolean addUndo) {
		if (NikuRecord.value != value)
			notifyListeners(NikuRecord.value, value, addUndo);
		NikuRecord.value = value;
	}

	public static void setValue(int value) {
		setValue(value, true);
	}

	public static File getFile() {
		return file;
	}

}

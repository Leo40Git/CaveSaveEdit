package com.leo.cse.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class NikuRecord {

	private static int value;
	private static File file;

	public static void read(File src) throws IOException {
		int[] result = new int[4];
		byte[] buf = new byte[20];
		FileInputStream fis = new FileInputStream(file);
		fis.read(buf);
		fis.close();
		for (int i = 0; i < 4; i++) {
			byte key = buf[i + 16];
			int j = i * 4;
			buf[j] -= key;
			buf[j + 1] -= key;
			buf[j + 2] -= key;
			buf[j + 3] -= key / 2;
			result[i] = ByteUtils.readInt(buf, j);
		}
		if (result[0] != result[1] || result[0] != result[2] || result[0] != result[3])
			throw new IOException("290.rec file is corrupt");
		value = result[0];
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
			byte key = bufByte[i + 16];
			int j = i * 4;
			bufByte[j] += key;
			bufByte[j + 1] += key;
			bufByte[j + 2] += key;
			bufByte[j + 3] += key / 2;
		}
		FileOutputStream fos = new FileOutputStream(dest);
		fos.write(bufByte);
		fos.close();
	}

	public static int getValue() {
		return value;
	}

	public static void setValue(int value) {
		NikuRecord.value = value;
	}

	public static File getFile() {
		return file;
	}

}

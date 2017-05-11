package com.leo.cse.backend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {

	public static final int SHORT_SIZE = Short.SIZE / 8;
	public static final int INT_SIZE = Integer.SIZE / 8;
	public static final int LONG_SIZE = Long.SIZE / 8;
	public static final int FLOAT_SIZE = Float.SIZE / 8;
	public static final int DOUBLE_SIZE = Double.SIZE / 8;

	// used for converting bytes to shorts/ints
	private static final ByteBuffer BB;

	static {
		BB = ByteBuffer.allocate(LONG_SIZE);
		BB.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public static short bytesToShort(byte[] data) {
		BB.clear();
		BB.put(data);
		return BB.getShort(0);
	}
	
	public static int bytesToInt(byte[] data) {
		BB.clear();
		BB.put(data);
		return BB.getInt(0);
	}

	private static void readBytesToBuffer(byte[] data, int ptr, int size) {
		BB.clear();
		for (int i = 0; i < size; i++)
			BB.put(data[ptr + i]);
	}

	public static String readString(byte[] data, int ptr, int length) {
		if (length < 1) {
			StringBuilder sb = new StringBuilder();
			while (ptr < data.length) {
				// string is (probably) terminated by 0
				if (data[ptr] == 0)
					break;
				sb.append((char) data[ptr]);
				ptr++;
			}
			return sb.toString();
		} else {
			byte[] dc = new byte[length];
			System.arraycopy(data, ptr, dc, 0, length);
			return new String(dc);
		}
	}

	public static short readShort(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, SHORT_SIZE);
		return BB.getShort(0);
	}

	public static void readShorts(byte[] data, int ptr, short[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readShort(data, ptr);
			ptr += SHORT_SIZE;
		}
	}

	public static int readInt(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, INT_SIZE);
		return BB.getInt(0);
	}

	public static void readInts(byte[] data, int ptr, int[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readInt(data, ptr);
			ptr += INT_SIZE;
		}
	}

	public static void readFlags(byte[] data, int ptr, boolean[] dest) {
		int s = 0;
		for (int i = 0; i < dest.length; i++) {
			byte v = data[ptr];
			dest[i] = ((v & (byte) Math.pow(2, s)) != 0);
			s++;
			if (s >= 8) {
				ptr++;
				s = 0;
			}
		}
	}

	private static void writeBytesFromBuffer(byte[] data, int ptr, int size) {
		for (int i = 0; i < size; i++)
			data[ptr + i] = BB.get(i);
	}

	public static void writeString(byte[] data, int ptr, String value) {
		byte[] dc = value.getBytes();
		System.arraycopy(dc, 0, data, ptr, value.length());
	}

	public static void writeShort(byte[] data, int ptr, short value) {
		BB.clear();
		BB.putShort(value);
		writeBytesFromBuffer(data, ptr, SHORT_SIZE);
	}

	public static void writeShorts(byte[] data, int ptr, short[] value) {
		for (int i = 0; i < value.length; i++) {
			writeShort(data, ptr, value[i]);
			ptr += SHORT_SIZE;
		}
	}

	public static void writeInt(byte[] data, int ptr, int value) {
		BB.clear();
		BB.putInt(value);
		writeBytesFromBuffer(data, ptr, INT_SIZE);
	}

	public static void writeInts(byte[] data, int ptr, int[] value) {
		for (int i = 0; i < value.length; i++) {
			writeInt(data, ptr, value[i]);
			ptr += INT_SIZE;
		}
	}

	public static void writeFlags(byte[] data, int ptr, boolean[] value) {
		byte[] v = new byte[value.length / 8];
		if (v.length == 0) {
			v = new byte[1];
		}
		int vi = 0, s = 0;
		for (int i = 0; i < value.length; i++) {
			if (value[i])
				v[vi] |= (byte) Math.pow(2, s);
			s++;
			if (s >= 8) {
				vi++;
				s = 0;
			}
		}
		System.arraycopy(v, 0, data, ptr, v.length);
	}

}

package com.leo.cse.backend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utilities for reading from and writing to byte arrays.
 * 
 * @author Leo
 *
 */
public class ByteUtils {

	/**
	 * The size of a <code>short</code> in bytes.
	 */
	public static final int SHORT_SIZE = Short.SIZE / 8;
	/**
	 * The size of an <code>int</code> in bytes.
	 */
	public static final int INT_SIZE = Integer.SIZE / 8;
	/**
	 * The size of a <code>long</code> in bytes.
	 */
	public static final int LONG_SIZE = Long.SIZE / 8;
	/**
	 * The size of a <code>float</code> in bytes.
	 */
	public static final int FLOAT_SIZE = Float.SIZE / 8;
	/**
	 * The size of a <code>double</code> in bytes.
	 */
	public static final int DOUBLE_SIZE = Double.SIZE / 8;

	/**
	 * Used for converting bytes to other number types.
	 */
	private static final ByteBuffer BB;

	static {
		BB = ByteBuffer.allocate(LONG_SIZE);
		BB.order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Converts an array of bytes into a <code>short</code>.
	 * 
	 * @param data
	 *            byte array
	 * @return short
	 */
	public static short bytesToShort(byte[] data) {
		BB.clear();
		BB.put(data);
		return BB.getShort(0);
	}

	/**
	 * Converts an array of bytes into an <code>int</code>.
	 * 
	 * @param data
	 *            byte array
	 * @return integer
	 */
	public static int bytesToInt(byte[] data) {
		BB.clear();
		BB.put(data);
		return BB.getInt(0);
	}

	/**
	 * Writes bytes from an array into {@linkplain #BB the byte buffer}.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param size
	 *            amount of bytes to write
	 */
	private static void readBytesToBuffer(byte[] data, int ptr, int size) {
		BB.clear();
		for (int i = 0; i < size; i++)
			BB.put(data[ptr + i]);
	}

	/**
	 * Reads a <code>String</code> from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param length
	 *            string length
	 * @return string
	 */
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

	/**
	 * Reads a <code>short</code> from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return short
	 */
	public static short readShort(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, SHORT_SIZE);
		return BB.getShort(0);
	}

	/**
	 * Reads an array of <code>short</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param dest
	 *            destination array
	 */
	public static void readShorts(byte[] data, int ptr, short[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readShort(data, ptr);
			ptr += SHORT_SIZE;
		}
	}

	/**
	 * Reads an integer from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return integer
	 */
	public static int readInt(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, INT_SIZE);
		return BB.getInt(0);
	}

	/**
	 * Reads an array of <code>int</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param dest
	 *            destination array
	 */
	public static void readInts(byte[] data, int ptr, int[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readInt(data, ptr);
			ptr += INT_SIZE;
		}
	}

	/**
	 * Reads an array of <code>boolean</code> from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting positon
	 * @param dest
	 *            destination array
	 */
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

	/**
	 * Reads bytes from {@linkplain #BB the byte buffer} into an array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param size
	 *            amount of bytes to read
	 */
	private static void writeBytesFromBuffer(byte[] data, int ptr, int size) {
		for (int i = 0; i < size; i++)
			data[ptr + i] = BB.get(i);
	}

	/**
	 * Writes a <code>String</code> to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            string to write
	 */
	public static void writeString(byte[] data, int ptr, String value) {
		byte[] dc = value.getBytes();
		System.arraycopy(dc, 0, data, ptr, value.length());
	}

	/**
	 * Writes a <code>short</code> to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            short to write
	 */
	public static void writeShort(byte[] data, int ptr, short value) {
		BB.clear();
		BB.putShort(value);
		writeBytesFromBuffer(data, ptr, SHORT_SIZE);
	}

	/**
	 * Writes an array of <code>short</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            shorts to write
	 */
	public static void writeShorts(byte[] data, int ptr, short[] value) {
		for (int i = 0; i < value.length; i++) {
			writeShort(data, ptr, value[i]);
			ptr += SHORT_SIZE;
		}
	}

	/**
	 * Writes an <code>int</code> to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            integer to write
	 */
	public static void writeInt(byte[] data, int ptr, int value) {
		BB.clear();
		BB.putInt(value);
		writeBytesFromBuffer(data, ptr, INT_SIZE);
	}

	/**
	 * Writes an array of <code>int</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            integers to write
	 */
	public static void writeInts(byte[] data, int ptr, int[] value) {
		for (int i = 0; i < value.length; i++) {
			writeInt(data, ptr, value[i]);
			ptr += INT_SIZE;
		}
	}

	/**
	 * Writes an array of <code>boolean</code> to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting positon
	 * @param value
	 *            booleans to write
	 */
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

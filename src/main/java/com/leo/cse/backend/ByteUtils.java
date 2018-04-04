package com.leo.cse.backend;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

/**
 * Utilities for reading from and writing to byte arrays.
 * 
 * @author Leo
 *
 */
public class ByteUtils {

	/**
	 * Used for converting bytes to other number types.
	 */
	private static final ByteBuffer BB;

	// Initialize ByteBuffer
	static {
		BB = ByteBuffer.allocate(Long.BYTES);
		BB.order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Converts an array of bytes into a <code>long</code>.
	 * 
	 * @param data
	 *            byte array
	 * @return converted long
	 */
	public static long bytesToLong(byte[] data) {
		BB.clear();
		BB.put(data);
		return BB.getLong(0);
	}

	/**
	 * Converts an array of bytes into a <code>short</code>.
	 * 
	 * @param data
	 *            byte array
	 * @return converted short
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
	 * @return converted integer
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
	 * @param encoding
	 *            string encoding
	 * @return string
	 */
	public static String readString(byte[] data, int ptr, int length, String encoding) {
		byte[] dc;
		if (length < 1) {
			// length was either not specified or specified but invalid
			// we're gonna have to guess the string's length
			Vector<Byte> charVec = new Vector<>();
			while (ptr < data.length) {
				// string is (probably) terminated by 0
				if (data[ptr] == 0)
					break;
				charVec.add(data[ptr]);
				ptr++;
			}
			dc = new byte[charVec.size()];
			for (int i = 0; i < dc.length; i++)
				dc[i] = charVec.get(i);
		} else {
			dc = new byte[length];
			System.arraycopy(data, ptr, dc, 0, length);
		}
		return StrTools.CString(dc, encoding);
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
		return readString(data, ptr, length, StrTools.DEFAULT_ENCODING);
	}

	/**
	 * Reads an array of <code>byte</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param dest
	 *            destination array
	 */
	public static void readBytes(byte[] data, int ptr, int off, byte[] dest) {
		if (off == 0) {
			System.arraycopy(data, ptr, dest, 0, dest.length);
			return;
		}
		for (int i = 0; i < dest.length; i++) {
			dest[i] = data[ptr];
			ptr += 1 + off;
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
		readBytesToBuffer(data, ptr, Short.BYTES);
		return BB.getShort(0);
	}

	/**
	 * Reads an array of <code>short</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param dest
	 *            destination array
	 */
	public static void readShorts(byte[] data, int ptr, int off, short[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readShort(data, ptr);
			ptr += Short.BYTES + off;
		}
	}

	/**
	 * Reads an <code>int</code> from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return integer
	 */
	public static int readInt(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, Integer.BYTES);
		return BB.getInt(0);
	}

	/**
	 * Reads an array of <code>int</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each integer
	 * @param dest
	 *            destination array
	 */
	public static void readInts(byte[] data, int ptr, int off, int[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readInt(data, ptr);
			ptr += Integer.BYTES + off;
		}
	}

	/**
	 * Reads a <code>long</code> from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @return integer
	 */
	public static long readLong(byte[] data, int ptr) {
		readBytesToBuffer(data, ptr, Long.BYTES);
		return BB.getLong(0);
	}

	/**
	 * Reads an array of <code>long</code>s from a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each long
	 * @param dest
	 *            destination array
	 */
	public static void readLongs(byte[] data, int ptr, int off, long[] dest) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = readLong(data, ptr);
			ptr += Long.BYTES + off;
		}
	}

	/**
	 * Reads an array of <code>boolean</code>s from a byte array.
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
			dest[i] = ((v & (1 << s)) != 0);
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
	 * Writes an array of <code>byte</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param value
	 *            shorts to write
	 */
	public static void writeBytes(byte[] data, int ptr, int off, byte[] value) {
		if (off == 0) {
			System.arraycopy(value, 0, data, ptr, value.length);
			return;
		}
		for (int i = 0; i < value.length; i++) {
			data[ptr] = value[i];
			ptr += 1 + off;
		}
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
		writeBytesFromBuffer(data, ptr, Short.BYTES);
	}

	/**
	 * Writes an array of <code>short</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each short
	 * @param value
	 *            shorts to write
	 */
	public static void writeShorts(byte[] data, int ptr, int off, short[] value) {
		for (int i = 0; i < value.length; i++) {
			writeShort(data, ptr, value[i]);
			ptr += Short.BYTES + off;
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
		writeBytesFromBuffer(data, ptr, Integer.BYTES);
	}

	/**
	 * Writes an array of <code>int</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each integer
	 * @param value
	 *            integers to write
	 */
	public static void writeInts(byte[] data, int ptr, int off, int[] value) {
		for (int i = 0; i < value.length; i++) {
			writeInt(data, ptr, value[i]);
			ptr += Integer.BYTES + off;
		}
	}

	/**
	 * Writes a <code>long</code> to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param value
	 *            integer to write
	 */
	public static void writeLong(byte[] data, int ptr, long value) {
		BB.clear();
		BB.putLong(value);
		writeBytesFromBuffer(data, ptr, Long.BYTES);
	}

	/**
	 * Writes an array of <code>long</code>s to a byte array.
	 * 
	 * @param data
	 *            byte array
	 * @param ptr
	 *            starting position
	 * @param off
	 *            byte offset between each long
	 * @param value
	 *            integers to write
	 */
	public static void writeLongs(byte[] data, int ptr, int off, long[] value) {
		for (int i = 0; i < value.length; i++) {
			writeLong(data, ptr, value[i]);
			ptr += Long.BYTES + off;
		}
	}

	/**
	 * Writes an array of <code>boolean</code>s to a byte array.
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
				v[vi] |= 1 << s;
			s++;
			if (s >= 8) {
				vi++;
				s = 0;
			}
		}
		System.arraycopy(v, 0, data, ptr, v.length);
	}

}

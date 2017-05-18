package com.leo.cse.frontend;

import java.awt.Graphics;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FrontUtils {

	public static void drawString(Graphics g, String str, int x, int y) {
		final int lineSpace = g.getFontMetrics().getHeight();
		for (String line : str.split("\n")) {
			y += lineSpace;
			g.drawString(line, x, y);
		}
	}

	public static void drawStringCentered(Graphics g, String str, int x, int y) {
		drawString(g, str, (int) (x - g.getFontMetrics().getStringBounds(str, g).getWidth() / 2), y);
	}

	public static String intsToString(int... nums) {
		String ret = "(";
		for (int i = 0; i < nums.length; i++)
			if (i == nums.length - 1)
				ret += nums[i];
			else
				ret += nums[i] + ",";
		ret += ")";
		return ret;
	}

	public static boolean pointInRectangle(int px, int py, int rx, int ry, int rw, int rh) {
		return (px >= rx && px <= rx + rw) && (py >= ry && py <= ry + rh);
	}

	public static <K extends Comparable<? super K>, V> Map<K, V> sortMapByKey(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getKey()).compareTo(o2.getKey());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static String padLeft(String str, String pad, int length) {
		while (str.length() < length)
			str = pad + str;
		return str;
	}

}

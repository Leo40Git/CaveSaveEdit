package com.leo.cse.frontend;

import java.awt.Graphics;
import java.awt.Image;
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

	public static void drawStringCentered(Graphics g, String str, int x, int y, boolean vert) {
		if (vert)
			y -= (g.getFontMetrics().getStringBounds(str, g).getHeight() / 4) * 3;
		drawString(g, str, (int) (x - g.getFontMetrics().getStringBounds(str, g).getWidth() / 2), y);
	}

	public static void drawStringCentered(Graphics g, String str, int x, int y) {
		drawStringCentered(g, str, x, y, false);
	}

	public static void drawNineSlice(Graphics g, Image img, int x, int y, int w, int h) {
		final int iw = img.getWidth(null), ih = img.getHeight(null);
		final int sw = iw / 3, sh = ih / 3;
		// top left
		g.drawImage(img, x, y, x + sw, y + sh, 0, 0, sw, sh, null);
		// top middle
		g.drawImage(img, x + sw, y, x + (w - sw), y + sh, sw, 0, sw * 2, sh, null);
		// top right
		g.drawImage(img, x + (w - sw), y, x + w, y + sh, sw * 2, 0, sw * 3, sh, null);
		// center left
		g.drawImage(img, x, y + sh, x + sw, y + (h - sh), 0, sh, sw, sh * 2, null);
		// center middle
		g.drawImage(img, x + sw, y + sh, x + (w - sw), y + (h - sh), sw, sh, sw * 2, sh * 2, null);
		// center right
		g.drawImage(img, x + (w - sw), y + sh, x + w, y + (h - sh), sw * 2, sh, sw * 3, sh * 2, null);
		// bottom left
		g.drawImage(img, x, y + (h - sh), x + sw, y + h, 0, sh * 2, sw, sh * 3, null);
		// bottom middle
		g.drawImage(img, x + sw, y + (h - sh), x + (w - sw), y + h, sw, sh * 2, sw * 2, sh * 3, null);
		// bottom right
		g.drawImage(img, x + (w - sw), y + (h - sh), x + w, y + h, sw * 2, sh * 2, sw * 3, sh * 3, null);
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

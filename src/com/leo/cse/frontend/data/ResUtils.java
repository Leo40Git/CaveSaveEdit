package com.leo.cse.frontend.data;

import java.awt.image.BufferedImage;
import java.io.File;

import com.leo.cse.frontend.Defines;

public class ResUtils {

	private ResUtils() {
	}

	public static BufferedImage black2Trans(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int y = 0; y < src.getHeight(); y++)
			for (int x = 0; x < src.getWidth(); x++) {
				int px = src.getRGB(x, y);
				if (px == -16777216) // argb black full opaque
					dest.setRGB(x, y, 0);
				else
					dest.setRGB(x, y, px);
			}

		return dest;
	}
	
	public static File getGraphicsFile(String directory, String name) {
		String ext = Defines.get("Game.GraphicsExtension");
		File ret = new File(directory + "/" + name + "." + ext);
		if (ret.exists()) {
			return ret;
		} else {
			if ("bmp".equalsIgnoreCase(ext)) {
				ext = "pbm";
			} else if ("pbm".equalsIgnoreCase(ext)) {
				ext = "bmp";
			} else
				return null;
			ret = new File(directory + "/" + name + "." + ext);
			if (!ret.exists())
				return null;
		}
		return null;
	}

}

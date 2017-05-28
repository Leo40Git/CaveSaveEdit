package com.leo.cse.frontend.data;

import java.awt.image.BufferedImage;
import java.io.File;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
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
		String ext = ExeData.getExeString(ExeData.STRING_IMG_EXT);
		String file = String.format(ext, directory, name);
		return new File(file);
	}

	public static File getGraphicsFile(String name) {
		String ext = ExeData.getExeString(ExeData.STRING_IMG_EXT);
		String file = String.format(ext, "", name);
		return new File(file);
	}

}

package com.leo.cse.backend;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

//credit to Noxid for making Booster's Lab open source so I could steal code from it
/**
 * Utility methods for loading resources.
 * 
 * @author Leo
 *
 */
public class ResUtils {

	/**
	 * Make sure an instance of this class cannot be created.
	 */
	private ResUtils() {
	}

	/**
	 * Filters an image so that any black pixels are turned into transparent pixels.
	 * 
	 * @param src
	 *            source image
	 * @return filtered image
	 */
	public static BufferedImage black2Trans(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int y = 0; y < src.getHeight(); y++)
			for (int x = 0; x < src.getWidth(); x++) {
				int px = src.getRGB(x, y);
				if (px == Color.black.getRGB()) // argb black full opaque
					dest.setRGB(x, y, 0);
				else
					dest.setRGB(x, y, px);
			}

		return dest;
	}

	/**
	 * Gets a graphics file using the image extension loaded from the executable.
	 * 
	 * @param directory
	 *            file directory
	 * @param name
	 *            file name
	 * @return graphics file
	 * @see ExeData#getExeString(int)
	 * @see ExeData#STRING_IMG_EXT
	 */
	public static File getGraphicsFile(String directory, String name) {
		String ext = ExeData.getExeString(ExeData.STRING_IMG_EXT);
		String file = String.format(ext, directory, name);
		return new File(file);
	}

	/**
	 * Gets a graphics file using the image extension loaded from the executable.
	 * 
	 * @param name
	 *            file name
	 * @return graphics file
	 * @see ExeData#getExeString(int)
	 * @see ExeData#STRING_IMG_EXT
	 */
	public static File getGraphicsFile(String name) {
		return getGraphicsFile("", name);
	}

}

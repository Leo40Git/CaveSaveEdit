package com.leo.cse.backend;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.leo.cse.backend.exe.ExeData;

// credit to Noxid for making Booster's Lab open source so I could steal code
// from it
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
				if (px == Color.black.getRGB())
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

	// code from https://stackoverflow.com/a/35325946
	/**
	 * Maps lower case strings to their case insensitive File
	 */
	private static final Map<String, File> insensitiveFileHandlerCache = new HashMap<>();

	/**
	 * Case insensitive file handler. Cannot return <code>null</code>
	 */
	public static File newFile(String path) {
		if (path == null)
			return new File(path);
		path = path.toLowerCase();
		// First see if it is cached
		if (insensitiveFileHandlerCache.containsKey(path)) {
			return insensitiveFileHandlerCache.get(path);
		} else {
			// If it is not cached, cache it (the path is lower case)
			File file = new File(path);
			insensitiveFileHandlerCache.put(path, file);

			// If the file does not exist, look for the real path
			if (!file.exists()) {

				// get the directory
				String parentPath = file.getParent();
				if (parentPath == null) {
					// No parent directory? -> Just return the file since we can't find the real
					// path
					return file;
				}

				// Find the real path of the parent directory recursively
				File dir = newFile(parentPath);

				File[] files = dir.listFiles();
				if (files == null) {
					// If it is not a directory
					insensitiveFileHandlerCache.put(path, file);
					return file;
				}

				// Loop through the directory and put everything you find into the cache
				for (File otherFile : files) {
					// the path of our file will be updated at this point
					insensitiveFileHandlerCache.put(otherFile.getPath().toLowerCase(), otherFile);
				}

				// if you found what was needed, return it
				if (insensitiveFileHandlerCache.containsKey(path)) {
					return insensitiveFileHandlerCache.get(path);
				}
			}
			// Did not find it? Return the file with the original path
			return file;
		}
	}
	// end code from stack overflow

	/**
	 * Attempts to get CS+'s "base" folder.
	 *
	 * @param currentLoc
	 *            current location
	 * @return location of CS+'s "base" folder
	 */
	public static File getBaseFolder(File currentLoc) {
		String place = "/";
		if (currentLoc == null) {
			// System.out.println(res);
			System.out.println("getBaseFolder: currentLoc == null");
			return null;
		}

		while (!currentLoc.getName().equals("mod")) {
			place = "/" + currentLoc.getName() + place;
			if (currentLoc.getParentFile() == null) {
				System.out.println("getBaseFolder: heirarchy crisis");
				return null; // heirarchy crisis
			}
			currentLoc = currentLoc.getParentFile();
		}
		// so barring shenanigans we should be in the 'mod' directory now
		currentLoc = currentLoc.getParentFile(); // modfolder (hurray unnecessarily nested folders)
		currentLoc = currentLoc.getParentFile(); // data
		currentLoc = new File(currentLoc + "/base"); // base... ofc
		return currentLoc;
	}

}

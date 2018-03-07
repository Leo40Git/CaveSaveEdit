package com.leo.cse.frontend;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Resources {

	private Resources() {
	}

	public static Font fontB, font, fontS, fontL;

	// base images
	private static BufferedImage ui;

	// icons
	public static BufferedImage appIcon;
	private static BufferedImage[] iconsRaw;
	private static ImageIcon[] icons;

	// niku counter
	public static BufferedImage[] nikuNumbers;
	public static BufferedImage nikuIcon, nikuPunc;

	public static void loadWindow() throws IOException {
		appIcon = ImageIO.read(Resources.class.getResourceAsStream("icon.png"));
	}

	public static void loadUI() throws IOException, FontFormatException {
		loadFonts();
		ui = ImageIO.read(Resources.class.getResourceAsStream("ui.png"));
		loadImages();
	}

	private static void loadFonts() throws FontFormatException, IOException {
		fontB = new Font(Font.DIALOG, Font.PLAIN, 1);
		font = fontB.deriveFont(Font.PLAIN, 11.0f);
		fontS = fontB.deriveFont(Font.PLAIN, 9.0f);
		fontL = fontB.deriveFont(Font.BOLD, 48.0f);
	}

	private static void loadImages() {
		iconsRaw = new BufferedImage[21];
		int tbx = 0, tby = 0;
		for (int i = 0; i < iconsRaw.length; i++) {
			iconsRaw[i] = ui.getSubimage(tbx, tby, 24, 24);
			tbx += 24;
			if (tbx >= ui.getWidth()) {
				tbx = 0;
				tby += 24;
			}
		}
		icons = new ImageIcon[iconsRaw.length];
		for (int i = 0; i < icons.length; i++)
			icons[i] = new ImageIcon(iconsRaw[i]);
		nikuNumbers = new BufferedImage[10];
		for (int i = 0; i < nikuNumbers.length; i++)
			nikuNumbers[i] = ui.getSubimage(i * 16, 72, 16, 16);
		nikuIcon = ui.getSubimage(0, 88, 14, 14);
		nikuPunc = ui.getSubimage(14, 88, 62, 14);
	}

	public enum Icon {
		TAB_GENERAL(0),
		TAB_INVENTORY(1),
		TAB_WARPS(2),
		TAB_FLAGS(3),
		TAB_TSCPLUS_VARS(4),
		TAB_MAP_FLAGS(5),
		TAB_EQUIPPLUS(6),
		LOAD_PROFILE(7),
		LOAD_EXE(8),
		SAVE(9),
		SAVE_AS(10),
		SETTINGS(11),
		ABOUT(12),
		QUIT(13),
		NIKU_EDIT(14),
		NEW_PROFILE(15),
		MCI_SETTINGS(16),
		UNLOAD_PROFILE(17),
		UNLOAD_EXE(18),
		PLUS_CHANGE_FILE(19),
		RUN_EXE(20);

		private int id;

		private Icon(int id) {
			this.id = id;
		}

		public int id() {
			return id;
		}
	}

	public static ImageIcon getIcon(Icon icon) {
		return icons[icon.id()];
	}

	public static BufferedImage getIconRaw(Icon icon) {
		return iconsRaw[icon.id()];
	}

}

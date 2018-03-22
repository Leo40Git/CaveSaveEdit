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
		iconsRaw = new BufferedImage[22];
		iconsRaw[0] = new BufferedImage(24, 24, ui.getType());
		int tbx = 0, tby = 0;
		for (int i = 1; i < iconsRaw.length; i++) {
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

	private static int lastIconID = 0;

	public enum Icon {
		EMPTY(), // 0
		TAB_GENERAL(),
		TAB_INVENTORY(),
		TAB_WARPS(),
		TAB_FLAGS(),
		TAB_TSCPLUS_VARS(), // 5
		TAB_MAP_FLAGS(),
		TAB_EQUIPPLUS(),
		LOAD_PROFILE(),
		LOAD_EXE(),
		SAVE(), // 10
		SAVE_AS(),
		SETTINGS(),
		ABOUT(),
		QUIT(),
		NIKU_EDIT(), // 15
		NEW_PROFILE(),
		MCI_SETTINGS(),
		UNLOAD_PROFILE(),
		UNLOAD_EXE(),
		PLUS_CHANGE_SLOT(), // 20
		RUN_EXE();

		private final int id;

		Icon() {
			id = lastIconID++;
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

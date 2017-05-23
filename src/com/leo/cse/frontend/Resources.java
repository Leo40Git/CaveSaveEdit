package com.leo.cse.frontend;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	private Resources() {
	}

	public static Font fontB, font, fontS, fontL;

	public static BufferedImage icon;
	public static BufferedImage ui;
	public static BufferedImage checkboxOff, checkboxOn, checkboxDisabled;
	public static BufferedImage radioOff, radioOn;
	public static BufferedImage radioOffS, radioOnS;
	public static BufferedImage dialogClose;
	public static BufferedImage arrowDown, arrowUp;
	public static BufferedImage[] editorTabIcons, toolbarIcons;
	public static BufferedImage dialog, dialogTitle;

	public static void load() throws IOException, FontFormatException {
		loadFonts();
		icon = ImageIO.read(Resources.class.getResourceAsStream("icon.png"));
		ui = ImageIO.read(Resources.class.getResourceAsStream("ui.png"));
		loadImages();
	}

	private static void loadFonts() throws FontFormatException {
		fontB = new Font("Arial", Font.PLAIN, 1);
		font = fontB.deriveFont(Font.PLAIN, 11.0f);
		fontS = fontB.deriveFont(Font.PLAIN, 9.0f);
		fontL = fontB.deriveFont(Font.BOLD, 48.0f);
	}

	private static void loadImages() {
		checkboxOff = ui.getSubimage(0, 0, 16, 16);
		checkboxOn = ui.getSubimage(16, 0, 16, 16);
		radioOff = ui.getSubimage(88, 0, 16, 16);
		radioOffS = ui.getSubimage(32, 0, 8, 8);
		radioOn = ui.getSubimage(88, 16, 16, 16);
		radioOnS = ui.getSubimage(40, 0, 8, 8);
		dialogClose = ui.getSubimage(48, 0, 14, 14);
		arrowDown = ui.getSubimage(80, 0, 8, 8);
		arrowUp = ui.getSubimage(80, 8, 8, 8);
		checkboxDisabled = ui.getSubimage(64, 0, 16, 16);
		editorTabIcons = new BufferedImage[5];
		for (int i = 0; i < editorTabIcons.length; i++)
			editorTabIcons[i] = ui.getSubimage(i * 16, 16, 16, 16);
		toolbarIcons = new BufferedImage[4];
		for (int i = 0; i < toolbarIcons.length; i++)
			toolbarIcons[i] = ui.getSubimage(i * 16, 32, 16, 16);
		dialog = ui.getSubimage(80, 16, 9, 9);
		dialogTitle = ui.getSubimage(80, 25, 9, 9);
	}

}

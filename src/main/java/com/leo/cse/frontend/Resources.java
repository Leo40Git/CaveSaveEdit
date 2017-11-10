package com.leo.cse.frontend;

import static com.leo.cse.frontend.FrontUtils.generateMask;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	private Resources() {
	}

	public static Font fontB, font, fontS, fontL;
	
	// base images
	public static BufferedImage icon;
	public static BufferedImage shadow;
	public static BufferedImage ui;
	
	// colored images
	public static BufferedImage checkboxOff, checkboxOn, checkboxDisabled;
	public static BufferedImage radioOff, radioOn, radioDisabled;
	public static BufferedImage radioOffS, radioOnS, radioDisabledS;
	public static BufferedImage dialogClose;
	public static BufferedImage arrowDown, arrowUp;
	
	// uncolored images
	public static BufferedImage drag;
	public static BufferedImage[] editorTabIcons, toolbarIcons, miscIcons;
	
	// niku counter
	public static BufferedImage[] nikuNumbers;
	public static BufferedImage nikuIcon, nikuPunc;
	
	public static void loadWindow() throws IOException {
		icon = ImageIO.read(Resources.class.getResourceAsStream("icon.png"));
		shadow = ImageIO.read(Resources.class.getResource("shadow.png"));
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
		reloadColorImages();
		drag = ui.getSubimage(40, 8, 3, 8);
		editorTabIcons = new BufferedImage[5];
		for (int i = 0; i < editorTabIcons.length; i++)
			editorTabIcons[i] = ui.getSubimage(i * 16, 16, 16, 16);
		toolbarIcons = new BufferedImage[8];
		for (int i = 0; i < toolbarIcons.length; i++)
			toolbarIcons[i] = ui.getSubimage(i * 16, 32, 16, 16);
		miscIcons = new BufferedImage[2];
		for (int i = 0; i < miscIcons.length; i++)
			miscIcons[i] = ui.getSubimage(i * 16, 48, 16, 16);
		nikuNumbers = new BufferedImage[10];
		for (int i = 0; i < nikuNumbers.length; i++)
			nikuNumbers[i] = ui.getSubimage(i * 16, 64, 16, 16);
		nikuIcon = ui.getSubimage(0, 80, 14, 14);
		nikuPunc = ui.getSubimage(14, 80, 62, 14);
	}

	private static void reloadColorImages() {
		checkboxOff = ui.getSubimage(0, 0, 16, 16);
		checkboxOn = ui.getSubimage(16, 0, 16, 16);
		checkboxDisabled = ui.getSubimage(64, 0, 16, 16);
		radioOff = ui.getSubimage(88, 0, 16, 16);
		radioOffS = ui.getSubimage(32, 0, 8, 8);
		radioOn = ui.getSubimage(88, 16, 16, 16);
		radioOnS = ui.getSubimage(40, 0, 8, 8);
		radioDisabled = ui.getSubimage(104, 16, 16, 16);
		radioDisabledS = ui.getSubimage(32, 8, 8, 8);
		dialogClose = ui.getSubimage(48, 0, 14, 14);
		arrowDown = ui.getSubimage(80, 0, 8, 8);
		arrowUp = ui.getSubimage(80, 8, 8, 8);
	}

	public static void colorImages(Color tint) {
		reloadColorImages();
		checkboxOff = generateMask(checkboxOff, tint, 1f);
		checkboxOn = generateMask(checkboxOn, tint, 1f);
		checkboxDisabled = generateMask(checkboxDisabled, tint, 1f);
		radioOff = generateMask(radioOff, tint, 1f);
		radioOffS = generateMask(radioOffS, tint, 1f);
		radioOn = generateMask(radioOn, tint, 1f);
		radioOnS = generateMask(radioOnS, tint, 1f);
		radioDisabled = generateMask(radioDisabled, tint, 1f);
		radioDisabledS = generateMask(radioDisabledS, tint, 1f);
		dialogClose = generateMask(dialogClose, tint, 1f);
		arrowDown = generateMask(arrowDown, tint, 1f);
		arrowUp = generateMask(arrowUp, tint, 1f);
	}

}

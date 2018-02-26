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
	
	// base images
	public static BufferedImage icon;
	public static BufferedImage shadow;
	public static BufferedImage ui;
	
	// icons
	public static BufferedImage[] editorTabIcons, icons;
	
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
		editorTabIcons = new BufferedImage[7];
		for (int i = 0; i < editorTabIcons.length; i++)
			editorTabIcons[i] = ui.getSubimage(i * 16, 0, 16, 16);
		icons = new BufferedImage[13];
		int tbx = 0, tby = 0;
		for (int i = 0; i < icons.length; i++) {
			if (i % 10 == 0) {
				tbx = 0;
				tby += 16;
			}
			icons[i] = ui.getSubimage(tbx, tby, 16, 16);
			tbx += 16;
		}
		nikuNumbers = new BufferedImage[10];
		for (int i = 0; i < nikuNumbers.length; i++)
			nikuNumbers[i] = ui.getSubimage(i * 16, 48, 16, 16);
		nikuIcon = ui.getSubimage(0, 64, 14, 14);
		nikuPunc = ui.getSubimage(14, 64, 62, 14);
	}

}

package com.leo.cse.frontend;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {

	private Resources() {
	}

	public static Font fontB, font, fontS;
	
	public static BufferedImage icon;
	public static BufferedImage ui;
	public static BufferedImage checkboxOn, checkboxOff;
	public static BufferedImage checkboxOnS, checkboxOffS;
	public static BufferedImage dialogClose;
	public static BufferedImage[] editorTabIcons, toolbarIcons;

	public static void load() throws IOException, FontFormatException {
		fontB = new Font("Arial", Font.PLAIN, 1);
		font = fontB.deriveFont(Font.PLAIN, 11.0f);
		fontS = fontB.deriveFont(Font.PLAIN, 9.0f);
		icon = ImageIO.read(Resources.class.getResourceAsStream("icon.png"));
		ui = ImageIO.read(Resources.class.getResourceAsStream("ui.png"));
		checkboxOn = ui.getSubimage(0, 0, 16, 16);
		checkboxOff = ui.getSubimage(16, 0, 16, 16);
		checkboxOnS = ui.getSubimage(32, 0, 8, 8);
		checkboxOffS = ui.getSubimage(40, 0, 8, 8);
		dialogClose = ui.getSubimage(48, 0, 14, 14);
		editorTabIcons = new BufferedImage[4];
		for (int i = 0; i < editorTabIcons.length; i++)
			editorTabIcons[i] = ui.getSubimage(i * 16, 16, 16, 16);
		toolbarIcons = new BufferedImage[4];
		for (int i = 0; i < toolbarIcons.length; i++)
			toolbarIcons[i] = ui.getSubimage(i * 16, 32, 16, 16);
	}

}

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
	public static BufferedImage checkboxOff, checkboxOn, checkboxDisabled;
	public static BufferedImage checkboxOffS, checkboxOnS;
	public static BufferedImage radioOff, radioOn;
	public static BufferedImage dialogClose;
	public static BufferedImage arrowDown, arrowUp;
	public static BufferedImage[] editorTabIcons, toolbarIcons;

	public static void load() throws IOException, FontFormatException {
		fontB = new Font("Arial", Font.PLAIN, 1);
		font = fontB.deriveFont(Font.PLAIN, 11.0f);
		fontS = fontB.deriveFont(Font.PLAIN, 9.0f);
		icon = ImageIO.read(Resources.class.getResourceAsStream("icon.png"));
		ui = ImageIO.read(Resources.class.getResourceAsStream("ui.png"));
		checkboxOff = ui.getSubimage(0, 0, 16, 16);
		checkboxOn = ui.getSubimage(16, 0, 16, 16);
		checkboxOffS = ui.getSubimage(32, 0, 8, 8);
		checkboxOnS = ui.getSubimage(40, 0, 8, 8);
		radioOff = ui.getSubimage(32, 8, 8, 8);
		radioOn = ui.getSubimage(40, 8, 8, 8);
		dialogClose = ui.getSubimage(48, 0, 14, 14);
		arrowDown = ui.getSubimage(64, 0, 8, 8);
		arrowUp = ui.getSubimage(72, 0, 8, 8);
		checkboxDisabled = ui.getSubimage(64, 8, 16, 16);
		editorTabIcons = new BufferedImage[4];
		for (int i = 0; i < editorTabIcons.length; i++)
			editorTabIcons[i] = ui.getSubimage(i * 16, 16, 16, 16);
		toolbarIcons = new BufferedImage[4];
		for (int i = 0; i < toolbarIcons.length; i++)
			toolbarIcons[i] = ui.getSubimage(i * 16, 32, 16, 16);
	}

}

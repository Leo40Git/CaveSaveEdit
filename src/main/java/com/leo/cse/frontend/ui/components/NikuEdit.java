package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.niku.NikuRecord;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.Resources;

public class NikuEdit extends Component implements IScrollable {

	private BufferedImage[] numbers;
	private BufferedImage icon, punc;

	private static final int ELEM_ICON = 0, ELEM_MINUTES = 1, ELEM_SECONDS = 2, ELEM_TENTHS = 3;
	private int elemHover = -1;

	private int getElement(int x) {
		x -= this.x;
		if (x <= 14)
			return ELEM_ICON;
		if (x >= 32 && x <= 64)
			return ELEM_MINUTES;
		if (x >= 72 && x <= 104)
			return ELEM_SECONDS;
		if (x >= 112)
			return ELEM_TENTHS;
		return -1;
	}

	public NikuEdit(int x, int y) {
		super("NikuEdit", x, y, 128, 16);
	}

	@Override
	public void render(Graphics g, Rectangle viewport) {
		if (ExeData.isLoaded())
			getSpritesFromTextBox();
		else
			getSpritesFromUI();
		g.setColor(new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31));
		if (elemHover == ELEM_ICON)
			g.fillRect(x, y, 14, 16);
		g.drawImage(icon, x, y, null);
		if (elemHover == ELEM_MINUTES)
			g.fillRect(x + 32, y, 32, 16);
		int mt = NikuRecord.getMinutes() / 10;
		if (mt > 0)
			g.drawImage(numbers[mt], x + 32, y, null);
		g.drawImage(numbers[NikuRecord.getMinutes() % 10], x + 48, y, null);
		if (elemHover == ELEM_SECONDS)
			g.fillRect(x + 72, y, 32, 16);
		g.drawImage(numbers[NikuRecord.getSeconds() / 10], x + 72, y, null);
		g.drawImage(numbers[NikuRecord.getSeconds() % 10], x + 88, y, null);
		if (elemHover == ELEM_TENTHS)
			g.fillRect(x + 112, y, 16, 16);
		g.drawImage(numbers[NikuRecord.getTenths()], x + 112, y, null);
		g.drawImage(punc, x + 62, y, null);
		if (!NikuRecord.isLoaded())
			FrontUtils.drawCheckeredGrid(g, x, y, width, height);
		g.setColor(Main.lineColor);
	}

	private void getSpritesFromUI() {
		numbers = Resources.nikuNumbers;
		icon = Resources.nikuIcon;
		punc = Resources.nikuPunc;
	}

	private void getSpritesFromTextBox() {
		BufferedImage textBox = ExeData.getImage(ExeData.getTextBox());
		icon = textBox.getSubimage(224, 208, 14, 14);
		punc = textBox.getSubimage(258, 208, 62, 14);
		numbers = new BufferedImage[10];
		for (int i = 0; i < numbers.length; i++)
			numbers[i] = textBox.getSubimage(i * 16, 112, 16, 16);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		if (!NikuRecord.isLoaded())
			return;
		int elem = getElement(x);
		if (elem < 0)
			return;
		String nVal;
		switch (elem) {
		case ELEM_ICON:
			if (NikuRecord.getValue() == 0)
				break;
			int confirmVal = JOptionPane.showConfirmDialog(Main.window, "Are you sure you want to reset to 0?",
					"Reset confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (confirmVal != JOptionPane.YES_OPTION)
				break;
			NikuRecord.setValue(0);
			break;
		case ELEM_MINUTES:
			nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for minutes:",
					Integer.toUnsignedString(NikuRecord.getMinutes()));
			if (nVal == null)
				return;
			try {
				int min = Integer.parseUnsignedInt(nVal);
				if (min > 99)
					min = 99;
				NikuRecord.setMinutes(min);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(Main.window, "Input \"" + nVal + "\" was not a valid number!",
						"Error while parsing input!", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case ELEM_SECONDS:
			nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for seconds:",
					Integer.toUnsignedString(NikuRecord.getSeconds()));
			if (nVal == null)
				return;
			try {
				int sec = Integer.parseUnsignedInt(nVal);
				if (sec > 59)
					sec = 59;
				NikuRecord.setSeconds(sec);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(Main.window, "Input \"" + nVal + "\" was not a valid number!",
						"Error while parsing input!", JOptionPane.ERROR_MESSAGE);
			}
			break;
		case ELEM_TENTHS:
			nVal = JOptionPane.showInputDialog(Main.window, "Enter new value for tenths of seconds:",
					Integer.toUnsignedString(NikuRecord.getTenths()));
			if (nVal == null)
				return;
			try {
				int ten = Integer.parseUnsignedInt(nVal);
				if (ten > 9)
					ten = 9;
				NikuRecord.setTenths(ten);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(Main.window, "Input \"" + nVal + "\" was not a valid number!",
						"Error while parsing input!", JOptionPane.ERROR_MESSAGE);
			}
			break;
		default:
			return;
		}
		elemHover = elem;
	}

	@Override
	public void onScroll(int rotations, boolean shiftDown, boolean ctrlDown) {
		if (!NikuRecord.isLoaded())
			return;
		if (shiftDown) {
			int newMin = NikuRecord.getMinutes() - rotations;
			if (newMin < 0)
				newMin = 0;
			if (newMin > 99)
				newMin = 99;
			NikuRecord.setMinutes(newMin);
		} else if (ctrlDown) {
			int newTen = NikuRecord.getTenths() - rotations;
			if (newTen < 0 && NikuRecord.getSeconds() == 0 && NikuRecord.getMinutes() == 0)
				newTen = 0;
			if (newTen > 9 && NikuRecord.getSeconds() == 59 && NikuRecord.getMinutes() == 99)
				newTen = 9;
			NikuRecord.setTenths(newTen);
		} else {
			int newSec = NikuRecord.getSeconds() - rotations;
			if (newSec < 0 && NikuRecord.getMinutes() == 0)
				newSec = 0;
			if (newSec > 59 && NikuRecord.getMinutes() == 99)
				newSec = 59;
			NikuRecord.setSeconds(newSec);
		}
	}

	@Override
	public void updateHover(int x, int y, boolean hover) {
		if (NikuRecord.isLoaded() && hover)
			elemHover = getElement(x);
		else
			elemHover = -1;
	}

}

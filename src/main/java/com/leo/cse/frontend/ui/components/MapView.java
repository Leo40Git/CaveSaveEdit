package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;

import javax.swing.JComponent;

import com.leo.cse.frontend.FrontUtils;

public class MapView extends JComponent {

	private static final long serialVersionUID = 1L;

	public MapView() {
		setBounds(getX(), getY(), 640, 480);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		FrontUtils.applyQualityRenderingHints(g2d);
		g2d.setColor(SystemColor.controlShadow);
		g2d.fillRect(0, 0, 640, 480);
		g2d.setFont(getFont());
		g2d.setColor(SystemColor.textText);
		g2d.drawString("MapView", 4, 474);
	}

}

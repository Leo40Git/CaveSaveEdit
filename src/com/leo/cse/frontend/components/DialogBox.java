package com.leo.cse.frontend.components;

import java.awt.Graphics;

public abstract class DialogBox {

	public abstract void render(Graphics g);

	public abstract boolean onClick(int x, int y);

}

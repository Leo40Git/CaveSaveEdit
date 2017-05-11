package com.leo.cse.frontend.dialogs;

import java.awt.Graphics;

public abstract class DialogBox {

	public abstract void render(Graphics g);

	public abstract boolean onClick(int x, int y);

}

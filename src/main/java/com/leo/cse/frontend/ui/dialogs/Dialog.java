package com.leo.cse.frontend.ui.dialogs;

import java.awt.Graphics;

public abstract class Dialog {

	public abstract void render(Graphics g);

	public abstract void onClick(int x, int y, boolean shift, boolean ctrl);
	
	public abstract boolean wantsToClose();
	
	public abstract void updateHover(int x, int y);
	
	public abstract void onScroll(int rotations, boolean shift, boolean ctrl);

}

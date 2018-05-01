package com.leo.cse.frontend.ui.components;

public interface IDraggable {

	public void onDrag(int x, int y, boolean shiftDown, boolean ctrlDown);

	public void onDragEnd(int px, int py, boolean shiftDown, boolean ctrlDown);

}

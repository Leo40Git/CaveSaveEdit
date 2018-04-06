package com.leo.cse.frontend.ui.components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.function.Supplier;

import com.leo.cse.frontend.Main;

public abstract class Component {

	protected String name;
	protected int x, y, width, height;
	protected Supplier<Boolean> enabled;
	protected boolean hover;

	public Component(String name, int x, int y, int width, int height, Supplier<Boolean> enabled) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.enabled = enabled;
	}

	public Component(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, Main.TRUE_SUPPLIER);
	}

	public abstract void render(Graphics g, Rectangle viewport);

	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
	}

	public void updateHover(int x, int y, boolean hover) {
		this.hover = hover;
	}

	public void onKey(int code, boolean shiftDown, boolean ctrlDown) {
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Component setEnabled(Supplier<Boolean> enabled) {
		this.enabled = enabled;
		return this;
	}

	@Override
	public String toString() {
		return name + ":" + getClass().getSimpleName();
	}

}

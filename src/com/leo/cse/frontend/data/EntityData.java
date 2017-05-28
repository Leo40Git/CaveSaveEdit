package com.leo.cse.frontend.data;

import java.awt.Rectangle;

public class EntityData {

	private Rectangle frameRect;

	public Rectangle getFramerect() {
		return frameRect;
	}
	
	void setFramerect(Rectangle frameRect) {
		this.frameRect = frameRect;
	}

	private int entityNum;

	public int getID() {
		return entityNum;
	}

	private int tbl_HP;

	public int getHP() {
		return tbl_HP;
	}

	private Rectangle tbl_display;

	public Rectangle getDisplay() {
		return new Rectangle(tbl_display);
	}

	private Rectangle tbl_hitbox;

	public Rectangle getHit() {
		return new Rectangle(tbl_hitbox);
	}

	private int tbl_tileset;

	public int getTileset() {
		return tbl_tileset;
	}

	private int tbl_exp;

	public int getXP() {
		return tbl_exp;
	}

	private int tbl_damage;

	public int getDmg() {
		return tbl_damage;
	}

	private int tbl_flags;

	public int getFlags() {
		return tbl_flags;
	}

	private int tbl_deathSound;

	public int getDeath() {
		return tbl_deathSound;
	}

	private int tbl_hurtSound;

	public int getHurt() {
		return tbl_hurtSound;
	}

	private int tbl_size;

	public int getSize() {
		return tbl_size;
	}

	EntityData(int num) {
		frameRect = new Rectangle(0, 0, 0, 0);
		this.tbl_hitbox = new Rectangle(4, 4, 4, 4);
		this.tbl_display = new Rectangle(8, 8, 8, 8);
		entityNum = num;
	}

	public EntityData(int num, int dam, int deathSound, int exp, int flags, int hp, int hurt, int size, int tileset,
			Rectangle display, Rectangle hitbox) {
		frameRect = new Rectangle(0, 0, 0, 0);
		entityNum = num;
		tbl_damage = dam;
		tbl_deathSound = deathSound;
		tbl_exp = exp;
		tbl_flags = flags;
		tbl_HP = hp;
		tbl_hurtSound = hurt;
		tbl_size = size;
		tbl_tileset = tileset;
		tbl_display = display;
		tbl_hitbox = hitbox;
	}

	EntityData(String n, int num) {
		entityNum = num;
	}

	EntityData(EntityData other) {
		frameRect = new Rectangle(other.frameRect);
		entityNum = other.entityNum;
		tbl_damage = other.tbl_damage;
		tbl_deathSound = other.tbl_deathSound;
		tbl_exp = other.tbl_exp;
		tbl_flags = other.tbl_flags;
		tbl_HP = other.tbl_HP;
		tbl_hurtSound = other.tbl_hurtSound;
		tbl_size = other.tbl_size;
		tbl_tileset = other.tbl_tileset;
		tbl_display = new Rectangle(other.tbl_display);
		tbl_hitbox = new Rectangle(other.tbl_hitbox);
	}
}
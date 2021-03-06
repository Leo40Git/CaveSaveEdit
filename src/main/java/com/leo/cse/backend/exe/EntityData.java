package com.leo.cse.backend.exe;

import java.awt.Rectangle;

/**
 * Stores information for a npc.tbl entry, plus the entity's framerect.
 *
 * @author Leo
 *
 */
public class EntityData {

	/**
	 * The entity's ID.
	 */
	private int entityNum;

	/**
	 * Gets the entity's ID.
	 *
	 * @return entity ID
	 */
	public int getID() {
		return entityNum;
	}

	/**
	 * The entity's HP.
	 */
	private int tbl_HP;

	/**
	 * Gets the entity's HP.
	 *
	 * @return entity HP
	 */
	public int getHP() {
		return tbl_HP;
	}

	/**
	 * The entity's display rectangle.
	 */
	private Rectangle tbl_display;

	/**
	 * Gets the entity's display rectangle.
	 *
	 * @return entity display rect
	 */
	public Rectangle getDisplay() {
		return new Rectangle(tbl_display);
	}

	/**
	 * The entity's hitbox.
	 */
	private Rectangle tbl_hitbox;

	/**
	 * Gets the entity's hitbox.
	 *
	 * @return entity hitbox
	 */
	public Rectangle getHit() {
		return new Rectangle(tbl_hitbox);
	}

	/**
	 * The entity's tileset ID.
	 */
	private int tbl_tileset;

	/**
	 * Gets the entity's tileset ID.
	 *
	 * @return entity tileset ID
	 */
	public int getTileset() {
		return tbl_tileset;
	}

	/**
	 * The amount of EXP this entity drops on death.
	 */
	private int tbl_exp;

	/**
	 * Gets the amount of EXP this entity drops on death.
	 *
	 * @return entity EXP drops
	 */
	public int getXP() {
		return tbl_exp;
	}

	/**
	 * The amount of damage this entity deals.
	 */
	private int tbl_damage;

	/**
	 * Gets the amount of damage this entity deals.
	 *
	 * @return entity damage
	 */
	public int getDmg() {
		return tbl_damage;
	}

	/**
	 * The entity's flags.
	 */
	private int tbl_flags;

	/**
	 * Get the entity's flags.
	 *
	 * @return entity flags
	 */
	public int getFlags() {
		return tbl_flags;
	}

	/**
	 * The entity's "death" sound.
	 */
	private int tbl_deathSound;

	/**
	 * Get the entity's "death" sound.
	 *
	 * @return entity death sound
	 */
	public int getDeath() {
		return tbl_deathSound;
	}

	/**
	 * The entity's "hurt" sound.
	 */
	private int tbl_hurtSound;

	/**
	 * Get the entity's "hurt" sound.
	 *
	 * @return entity hurt sound
	 */
	public int getHurt() {
		return tbl_hurtSound;
	}

	/**
	 * The amount of smoke this entity creates on death.
	 */
	private int tbl_size;

	/**
	 * Gets the amount of smoke this entity creates on death.
	 *
	 * @return entity smoke amount
	 */
	public int getSize() {
		return tbl_size;
	}

	/**
	 * Creates a blank npc.tbl entry.
	 *
	 * @param num
	 *            entity ID
	 */
	EntityData(int num) {
		tbl_hitbox = new Rectangle(4, 4, 4, 4);
		tbl_display = new Rectangle(8, 8, 8, 8);
		entityNum = num;
	}

	/**
	 * Creates a npc.tbl entry.
	 *
	 * @param num
	 *            entity ID
	 * @param dam
	 *            damage amount
	 * @param deathSound
	 *            death sound
	 * @param exp
	 *            EXP amount
	 * @param flags
	 *            entity flags
	 * @param hp
	 *            HP amount
	 * @param hurt
	 *            hurt sound
	 * @param size
	 *            smoke amount
	 * @param tileset
	 *            tileset ID
	 * @param display
	 *            display rectangle
	 * @param hitbox
	 *            hitbox
	 */
	public EntityData(int num, int dam, int deathSound, int exp, int flags, int hp, int hurt, int size, int tileset,
			Rectangle display, Rectangle hitbox) {
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

	/**
	 * Clones an npc.tbl entry from another entry.
	 *
	 * @param other
	 *            source entry
	 */
	EntityData(EntityData other) {
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
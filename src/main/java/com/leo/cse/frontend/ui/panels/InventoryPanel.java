package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;

import com.leo.cse.backend.profile.NormalProfile;
import com.leo.cse.backend.profile.ProfileManager;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.box.BooleanBox;
import com.leo.cse.frontend.ui.components.box.IntegerBox;
import com.leo.cse.frontend.ui.components.box.ItemBox;
import com.leo.cse.frontend.ui.components.box.LongBox;
import com.leo.cse.frontend.ui.components.box.RadioBoxes;
import com.leo.cse.frontend.ui.components.box.ShortBox;
import com.leo.cse.frontend.ui.components.box.WeaponBox;
import com.leo.cse.frontend.ui.components.visual.Label;

public class InventoryPanel extends Panel {

	public InventoryPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		final String l = "Selected";
		int xx = 4;
		compList.add(new RadioBoxes(xx + 32, 8, xx + (122 * 7), 7, new String[] { l, l, l, l, l, l, l }, () -> {
			return (Integer) ProfileManager.getField(NormalProfile.FIELD_CURRENT_WEAPON);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_CURRENT_WEAPON, t);
			return t;
		}, true, (Integer index) -> {
			return true;
		}));
		for (int i = 0; i < 7; i++) {
			final int i2 = i;
			compList.add(new WeaponBox(xx, 22, i));
			compList.add(new Label("Level:", xx, 72));
			compList.add(new IntegerBox(xx, 90, 120, 16, () -> {
				return (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_LEVEL, i2);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_WEAPON_LEVEL, i2, t);
				return t;
			}, "weapon " + (i + 1) + " level"));
			compList.add(new Label("Extra EXP:", xx, 108));
			compList.add(new IntegerBox(xx, 126, 120, 16, () -> {
				return (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_EXP, i2);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_WEAPON_EXP, i2, t);
				return t;
			}, "weapon " + (i + 1) + " extra EXP"));
			compList.add(new Label("Ammo:", xx + 10, 144));
			compList.add(new IntegerBox(xx + 10, 162, 110, 16, () -> {
				return (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_CURRENT_AMMO, i2);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_WEAPON_CURRENT_AMMO, i2, t);
				return t;
			}, "weapon " + (i + 1) + " current ammo"));
			compList.add(new Label("/", xx + 2, 180));
			compList.add(new IntegerBox(xx + 10, 180, 110, 16, () -> {
				return (Integer) ProfileManager.getField(NormalProfile.FIELD_WEAPON_MAXIMUM_AMMO, i2);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_WEAPON_MAXIMUM_AMMO, i2, t);
				return t;
			}, "weapon " + (i + 1) + " maximum ammo"));
			xx += 122;
		}
		int itemId = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				compList.add(new ItemBox(22 + j * 164, 204 + i * 50, 160, 48, itemId));
				itemId++;
			}
		}
		int equipId = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				final int i2 = equipId;
				compList.add(new BooleanBox("Equip." + equipId, true, 4 + (winSize.width / 3) * i, 506 + 18 * j, () -> {
					return (Boolean) ProfileManager.getField(NormalProfile.FIELD_EQUIPS, i2);
				}, t -> {
					ProfileManager.setField(NormalProfile.FIELD_EQUIPS, i2, t);
					return t;
				}));
				equipId++;
			}
		}
		compList.add(new Label("Whimsical Star Count:", 4 + (winSize.width / 3) * 2, 505));
		compList.add(new ShortBox(4 + (winSize.width / 3) * 2, 521, 120, 16, () -> {
			return (Short) ProfileManager.getField(NormalProfile.FIELD_STAR_COUNT);
		}, t -> {
			ProfileManager.setField(NormalProfile.FIELD_STAR_COUNT, t);
			return t;
		}, "Whimsical Star count"));
		if (!MCI.getSpecial("VarHack") && MCI.getSpecial("MimHack")) {
			compList.add(new Label("<MIM Costume:", 4 + (winSize.width / 3) * 2, 539));
			compList.add(new LongBox(4 + (winSize.width / 3) * 2, 555, 120, 16, () -> {
				return (Long) ProfileManager.getField(NormalProfile.FIELD_MIM_COSTUME);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_MIM_COSTUME, t);
				return t;
			}, "<MIM costume"));
		} else if (MCI.getSpecial("BuyHack")) {
			compList.add(new Label("Amount of Cash:", 4 + (winSize.width / 3) * 2, 539));
			compList.add(new LongBox(4 + (winSize.width / 3) * 2, 555, 120, 16, () -> {
				return (Long) ProfileManager.getField(NormalProfile.FIELD_CASH);
			}, t -> {
				ProfileManager.setField(NormalProfile.FIELD_CASH, t);
				return t;
			}, "amount of cash"));
		}
	}

}

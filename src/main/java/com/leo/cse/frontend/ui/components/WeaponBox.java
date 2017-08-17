package com.leo.cse.frontend.ui.components;

//import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.exe.ExeData;
import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;

public class WeaponBox extends DefineBox {

	//private int id;

	public WeaponBox(int x, int y, int weaponId) {
		super(x, y, 120, 48, new Supplier<Integer>() {
			@Override
			public Integer get() {
				return Profile.getWeapon(weaponId).getId();
			}
		}, new Function<Integer, Integer>() {
			@Override
			public Integer apply(Integer t) {
				Profile.getWeapon(weaponId).setId(t);
				return t;
			}
		}, "Weapon", "weapon " + (weaponId + 1));
		//id = weaponId;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Main.COLOR_BG);
		g.fillRect(x, y, width, height - 1);
		g.setColor(Main.lineColor);
		g.drawRect(x, y, width, height - 1);
		/*
		if (id != 0 && Profile.getWeapon(id - 1).getId() == 0) {
			Color lc2 = new Color(Main.lineColor.getRed(), Main.lineColor.getGreen(), Main.lineColor.getBlue(), 31);
			g.setColor(lc2);
			g.fillRect(x, y, width, height - 1);
			return;
		}
		*/
		int wep = vSup.get();
		FrontUtils.drawStringCentered(g, wep + " - " + MCI.get(type, wep), x + width / 2, y + 31);
		if (wep == 0)
			return;
		if (!ExeData.isLoaded())
			return;
		int ystart = MCI.getInteger("Game.ArmsImageYStart", 0), size = MCI.getInteger("Game.ArmsImageSize", 32);
		g.drawImage(ExeData.getImage(ExeData.getArmsImage()), x + width / 2 - size / 2, y - (size - 32) + 1,
				x + width / 2 + size / 2, y - (size - 32) + size + 1, wep * size, ystart, (wep + 1) * size,
				ystart + size, null);
	}

	@Override
	public void onClick(int x, int y, boolean shiftDown, boolean ctrlDown) {
		super.onClick(x, y, shiftDown, ctrlDown);
	}

}

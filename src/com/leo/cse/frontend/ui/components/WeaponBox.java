package com.leo.cse.frontend.ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.Profile;
import com.leo.cse.frontend.FrontUtils;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.data.CSData;

public class WeaponBox extends DefineBox {

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
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		g.setColor(Color.white);
		g.drawRect(x, y, width, height);
		int wep = vSup.get();
		FrontUtils.drawStringCentered(g, wep + " - " + MCI.get(type, wep), x + width / 2, y + 32);
		if (wep == 0)
			return;
		if (!CSData.isLoaded())
			return;
		int ystart = 0, size = 32;
		String yss = MCI.getNullable("Game", "ArmsImageYStart");
		if (yss != null)
			try {
				ystart = Integer.parseUnsignedInt(yss);
			} catch (Exception ignore) {
			}
		String sis = MCI.getNullable("Game", "ArmsImageSize");
		if (sis != null)
			try {
				size = Integer.parseUnsignedInt(sis);
			} catch (Exception ignore) {
			}
		g.drawImage(CSData.getArmsImage(), x + width / 2 - size / 2, y - (size - 32) + 1, x + width / 2 + size / 2,
				y - (size - 32) + size + 1, wep * size, ystart, (wep + 1) * size, ystart + size, null);
	}

}
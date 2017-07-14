package com.leo.cse.frontend.ui.panels;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.function.Supplier;

import com.leo.cse.backend.profile.Profile;
import com.leo.cse.frontend.MCI;
import com.leo.cse.frontend.Main;
import com.leo.cse.frontend.ui.components.BooleanBox;
import com.leo.cse.frontend.ui.components.IntegerBox;
import com.leo.cse.frontend.ui.components.ItemBox;
import com.leo.cse.frontend.ui.components.Label;
import com.leo.cse.frontend.ui.components.LongBox;
import com.leo.cse.frontend.ui.components.RadioBoxes;
import com.leo.cse.frontend.ui.components.ShortBox;
import com.leo.cse.frontend.ui.components.WeaponBox;

public class InventoryPanel extends Panel {

	public InventoryPanel() {
		super();
		final Dimension winSize = Main.WINDOW_SIZE;
		final String l = "Selected";
		int xx = 4;
		compList.add(new RadioBoxes(xx + 32, 8, xx + (122 * 7), 7, new String[] { l, l, l, l, l, l, l },
				new Supplier<Integer>() {
					@Override
					public Integer get() {
						return Profile.getCurWeapon();
					}
				}, new Function<Integer, Integer>() {
					@Override
					public Integer apply(Integer t) {
						Profile.setCurWeapon(t);
						return t;
					}
				}, true, (int index) -> {
					if (Profile.getWeapon(index).getId() == 0)
						return false;
					return true;
				}));
		for (int i = 0; i < 7; i++) {
			final int i2 = i;
			compList.add(new WeaponBox(xx, 22, i));
			Supplier<Boolean> enabled = new Supplier<Boolean>() {
				public Boolean get() {
					return Profile.getWeapon(i2).getId() != 0;
				};
			};
			compList.add(new Label("Level:", xx, 72));
			compList.add(new IntegerBox(xx, 90, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getLevel();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setLevel(t);
					return t;
				}
			}, "weapon " + (i + 1) + " level", enabled));
			compList.add(new Label("Extra EXP:", xx, 108));
			compList.add(new IntegerBox(xx, 126, 120, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getExp();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setExp(t);
					return t;
				}
			}, "weapon " + (i + 1) + " extra EXP", enabled));
			compList.add(new Label("Ammo:", xx + 10, 144));
			compList.add(new IntegerBox(xx + 10, 162, 110, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getCurAmmo();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setCurAmmo(t);
					return t;
				}
			}, "weapon " + (i + 1) + " current ammo", enabled));
			compList.add(new Label("/", xx + 2, 180));
			compList.add(new IntegerBox(xx + 10, 180, 110, 16, new Supplier<Integer>() {
				@Override
				public Integer get() {
					return Profile.getWeapon(i2).getMaxAmmo();
				}
			}, new Function<Integer, Integer>() {
				@Override
				public Integer apply(Integer t) {
					Profile.getWeapon(i2).setMaxAmmo(t);
					return t;
				}
			}, "weapon " + (i + 1) + " maximum ammo", enabled));
			xx += 122;
		}
		int itemId = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				compList.add(new ItemBox(j * (winSize.width / 5), 204 + i * 50, winSize.width / 5 - 5, 48, itemId));
				itemId++;
			}
		}
		int equipId = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				final int i2 = equipId;
				compList.add(new BooleanBox("Equip." + equipId, 4 + (winSize.width / 3) * i, 507 + 18 * j,
						new Supplier<Boolean>() {
							@Override
							public Boolean get() {
								return Profile.getEquip(i2);
							}
						}, new Function<Boolean, Boolean>() {
							@Override
							public Boolean apply(Boolean t) {
								Profile.setEquip(i2, t);
								return t;
							}
						}));
				equipId++;
			}
		}
		compList.add(new Label("Whimsical Star Count:", 4 + (winSize.width / 3) * 2, 505));
		compList.add(new ShortBox(4 + (winSize.width / 3) * 2, 521, 120, 16, new Supplier<Short>() {
			@Override
			public Short get() {
				return Profile.getStarCount();
			}
		}, new Function<Short, Short>() {
			@Override
			public Short apply(Short t) {
				Profile.setStarCount(t);
				return t;
			}
		}, "Whimsical Star count"));
		if (!MCI.getSpecial("VarHack") && MCI.getSpecial("MimHack")) {
			compList.add(new Label("<MIM Costume:", 4 + (winSize.width / 3) * 2, 539));
			compList.add(new LongBox(4 + (winSize.width / 3) * 2, 555, 120, 16, new Supplier<Long>() {
				@Override
				public Long get() {
					return Profile.getMimCostume();
				}
			}, new Function<Long, Long>() {
				@Override
				public Long apply(Long t) {
					Profile.setMimCostume(t);
					return t;
				}
			}, "<MIM costume"));
		} else if (MCI.getSpecial("BuyHack")) {
			compList.add(new Label("Amount of Cash:", 4 + (winSize.width / 3) * 2, 539));
			compList.add(new LongBox(4 + (winSize.width / 3) * 2, 555, 120, 16, new Supplier<Long>() {
				@Override
				public Long get() {
					return Profile.getMimCostume();
				}
			}, new Function<Long, Long>() {
				@Override
				public Long apply(Long t) {
					Profile.setMimCostume(t);
					return t;
				}
			}, "amount of cash"));
		}
	}

}

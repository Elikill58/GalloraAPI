package com.elikill58.galloraapi.api.utils;

import com.elikill58.galloraapi.api.item.Enchantment;
import com.elikill58.galloraapi.api.item.ItemStack;

public class ItemUtils {


	public static boolean hasDigSpeedEnchant(ItemStack item) {
		return item != null && item.hasEnchant(Enchantment.DIG_SPEED) && item.getEnchantLevel(Enchantment.DIG_SPEED) > 2;
	}
}

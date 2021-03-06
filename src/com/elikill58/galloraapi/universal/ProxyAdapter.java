package com.elikill58.galloraapi.universal;

import com.elikill58.galloraapi.api.entity.FakePlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.inventory.Inventory;
import com.elikill58.galloraapi.api.inventory.NegativityHolder;
import com.elikill58.galloraapi.api.item.ItemBuilder;
import com.elikill58.galloraapi.api.item.ItemRegistrar;
import com.elikill58.galloraapi.api.item.Material;
import com.elikill58.galloraapi.api.location.Location;
import com.elikill58.galloraapi.api.location.World;
import com.elikill58.galloraapi.api.timers.Scheduler;

public abstract class ProxyAdapter extends Adapter {

	@Override
	public ItemRegistrar getItemRegistrar() {
		return null;
	}

	@Override
	public ItemBuilder createItemBuilder(Material type) {
		return null;
	}

	@Override
	public ItemBuilder createItemBuilder(String type) {
		return null;
	}

	@Override
	public ItemBuilder createSkullItemBuilder(Player owner) {
		return null;
	}

	@Override
	public Location createLocation(World w, double x, double y, double z) {
		return null;
	}

	@Override
	public Inventory createInventory(String inventoryName, int size, NegativityHolder holder) {
		return null;
	}

	@Override
	public FakePlayer createFakePlayer(Location loc, String name) {
		return null;
	}

	@Override
	public Scheduler getScheduler() {
		throw new UnsupportedOperationException("Scheduler can't be used on proxy servers");
	}
}

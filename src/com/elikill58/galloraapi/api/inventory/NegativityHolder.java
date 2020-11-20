package com.elikill58.galloraapi.api.inventory;

public abstract class NegativityHolder extends InventoryHolder {

	@Override
	public NegativityHolder getBasicHolder() {
		return this;
	}
}

package com.elikill58.galloraapi.api.events.inventory;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.events.Event;

public class InventoryOpenEvent implements Event {

	private final Player p;
	private boolean cancel = false;
	
	public InventoryOpenEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}

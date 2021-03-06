package com.elikill58.galloraapi.api.events.player;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.events.Event;

public class PlayerRegainHealthEvent implements Event {
	
	private final Player p;
	private boolean cancel = false;
	
	public PlayerRegainHealthEvent(Player p) {
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

package com.elikill58.galloraapi.api.events.player;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.events.Event;

public class PlayerDeathEvent implements Event {

	private final Player p;
	
	public PlayerDeathEvent(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
}

package com.elikill58.galloraapi.api.events.player;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.events.Event;

public class PlayerConnectEvent implements Event {

	private final Player p;
	private final GalloraPlayer np;
	private String joinMessage;
	
	public PlayerConnectEvent(Player p, GalloraPlayer np, String joinMessage) {
		this.p = p;
		this.np = np;
		this.joinMessage = joinMessage;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public GalloraPlayer getGalloraPlayer() {
		return np;
	}
	
	public String getJoinMessage() {
		return joinMessage;
	}
	
	public void setJoinMessage(String joinMessage) {
		this.joinMessage = joinMessage;
	}
}

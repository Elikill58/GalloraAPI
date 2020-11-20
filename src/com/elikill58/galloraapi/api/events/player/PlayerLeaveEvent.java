package com.elikill58.galloraapi.api.events.player;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.events.Event;

public class PlayerLeaveEvent implements Event {

	private final Player p;
	private final GalloraPlayer np;
	private String quitMessage;
	
	public PlayerLeaveEvent(Player p, GalloraPlayer np, String quitMessage) {
		this.p = p;
		this.np = np;
		this.quitMessage = quitMessage;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public GalloraPlayer getGalloraPlayer() {
		return np;
	}
	
	public String getQuitMessage() {
		return quitMessage;
	}
	
	public void setQuitMessage(String quitMessage) {
		this.quitMessage = quitMessage;
	}
}

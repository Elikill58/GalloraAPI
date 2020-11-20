package com.elikill58.galloraapi.api.events.packets;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.packets.AbstractPacket;

public class PacketSendEvent extends PacketEvent {

	public PacketSendEvent(PacketSourceType source, AbstractPacket packet, Player p) {
		super(source, packet, p);
	}

	public boolean isCancelled() {
		return getPacket().isCancelled();
	}

	public void setCancelled(boolean cancel) {
		getPacket().setCancelled(cancel);
	}
}

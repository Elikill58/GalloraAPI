package com.elikill58.galloraapi.sponge.packets.packetgate;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.galloraapi.universal.PacketType;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;

public class PacketGatePacket extends AbstractPacket {
	
	private PacketEvent event;
	
	public PacketGatePacket(PacketType type, Object packet, Player p, PacketEvent event) {
		super(type, packet, SpongeEntityManager.getPlayer(p));
		this.event = event;
	}
	
	public PacketEvent getPacketGateEvent() {
		return event;
	}

}

package com.elikill58.galloraapi.spigot.packets.protocollib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketEvent;
import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.galloraapi.universal.PacketType;

public class ProtocollibPacket extends AbstractPacket {
	
	private PacketEvent event;
	
	public ProtocollibPacket(PacketType type, Object packet, Player p, PacketEvent event) {
		super(type, packet, SpigotEntityManager.getPlayer(p));
		this.event = event;
	}
	
	public PacketEvent getProtocollibEvent() {
		return event;
	}
}

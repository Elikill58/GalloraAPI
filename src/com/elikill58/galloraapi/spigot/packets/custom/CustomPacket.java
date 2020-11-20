package com.elikill58.galloraapi.spigot.packets.custom;

import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.universal.PacketType;

public class CustomPacket extends AbstractPacket {
	
	public CustomPacket(PacketType type, Object packet, Player p) {
		super(type, packet, p);
	}
}
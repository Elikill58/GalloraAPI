package com.elikill58.galloraapi.sponge.packets;

import org.slf4j.Logger;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.api.packets.PacketHandler;
import com.elikill58.galloraapi.sponge.SpongeNegativity;
import com.elikill58.galloraapi.sponge.impl.packet.SpongePacketManager;
import com.elikill58.galloraapi.sponge.packets.packetgate.PacketGateManager;

public class NegativityPacketManager {

	private SpongePacketManager spongePacketManager;
	
	public NegativityPacketManager(SpongeNegativity pl) {

		try {
			Class.forName("eu.crushedpixel.sponge.packetgate.api.registry.PacketGate");
			SpongeNegativity.hasPacketGate = true;
			spongePacketManager = new PacketGateManager();
		} catch (ClassNotFoundException e1) {
			Logger log = pl.getLogger();
			log.warn("----- Negativity Problem -----");
			log.warn("");
			log.warn("Error while loading PacketGate. Plugin not found.");
			log.warn("Please download it available here: https://github.com/CrushedPixel/PacketGate/releases");
			log.warn("Then, put it in the mods folder.");
			log.warn("Restart your server and now, it will be working");
			log.warn("");
			log.warn("----- Negativity Problem -----");
		}
		
		spongePacketManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				Player p = packet.getPlayer();
				if (!GalloraPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				manageReceive(packet);
			}
		});
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p);
		np.PACKETS.put(packet.getPacketType(), np.PACKETS.getOrDefault(packet.getPacketType(), 0) + 1);
	}
}

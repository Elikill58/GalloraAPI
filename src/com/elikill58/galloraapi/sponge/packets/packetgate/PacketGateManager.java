package com.elikill58.galloraapi.sponge.packets.packetgate;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.galloraapi.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.sponge.SpongeAdapter;
import com.elikill58.galloraapi.sponge.impl.packet.SpongePacketManager;
import com.elikill58.galloraapi.universal.PacketType;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

public class PacketGateManager extends SpongePacketManager {

	private final PacketGate packetGate;
	
	public PacketGateManager() {
		packetGate = Sponge.getServiceManager().provideUnchecked(PacketGate.class);
		packetGate.registerListener(new PacketGateListener(this), ListenerPriority.DEFAULT);
	}
	
	@Override
	public void addPlayer(com.elikill58.galloraapi.api.entity.Player p) {}
	
	@Override
	public void removePlayer(com.elikill58.galloraapi.api.entity.Player p) {}

	@Override
	public void clear() {}

	public AbstractPacket onPacketSent(PacketType type, Player sender, Object packet, PacketEvent event) {
		PacketGatePacket customPacket = new PacketGatePacket(type, packet, sender, event);
		notifyHandlersSent(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(PacketType type, Player sender, Object packet, PacketEvent event) {
		PacketGatePacket customPacket = new PacketGatePacket(type, packet, sender, event);
		notifyHandlersReceive(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public class PacketGateListener extends PacketListenerAdapter {

		private final PacketGateManager packetManager;

		public PacketGateListener(PacketGateManager packetManager) {
			this.packetManager = packetManager;
		}

		@Override
		public void onPacketRead(PacketEvent e, PacketConnection connection) {
			try {
				Object mcPacket = e.getClass().getMethod("getPacket").invoke(e);
				String[] parts = mcPacket.getClass().getName().split("\\.");
				String packetName = parts[parts.length - 1];
				Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(connection.getPlayerUUID());
				if (!optionalPlayer.isPresent())
					return;
				Player p = optionalPlayer.get();
				PacketType packetType = null;
				if (packetName.equalsIgnoreCase("CPacketPlayer"))
					packetType = PacketType.Client.FLYING;
				else {
					String newName = packetName.replaceFirst("CPacket", (e.isOutgoing() ? "PacketPlayOut" : "PacketPlayIn"))
							.replaceAll("\\$", "").replaceAll("Player", "");
					packetType = PacketType.getType(newName);
					if (packetType == null)
						SpongeAdapter.getAdapter().getLogger().error("Unknow Packet " + packetName + ", parsed as "
								+ newName + ". Please, report this to Elikill58.");
				}
				AbstractPacket packet = e.isOutgoing() ? packetManager.onPacketSent(packetType, p, mcPacket, e)
						: packetManager.onPacketReceive(packetType, p, mcPacket, e);
				e.setCancelled(packet.isCancelled());
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
}

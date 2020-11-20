package com.elikill58.galloraapi.sponge.impl.packet;

import org.spongepowered.api.scheduler.Task;

import com.elikill58.galloraapi.api.events.EventManager;
import com.elikill58.galloraapi.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.galloraapi.api.events.packets.PacketReceiveEvent;
import com.elikill58.galloraapi.api.events.packets.PacketSendEvent;
import com.elikill58.galloraapi.api.packets.AbstractPacket;
import com.elikill58.galloraapi.api.packets.PacketManager;
import com.elikill58.galloraapi.sponge.SpongeAdapter;

public abstract class SpongePacketManager extends PacketManager {
	
	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Task.builder().execute(() -> {
			PacketReceiveEvent event = new PacketReceiveEvent(source, packet, packet.getPlayer());
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onReceive(packet));
		}).submit(SpongeAdapter.getPlugin());
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		// Go on main Thread
		Task.builder().execute(() -> {
			PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
			EventManager.callEvent(event);
			handlers.forEach((handler) -> handler.onSend(packet));
		}).submit(SpongeAdapter.getPlugin());
	}
}

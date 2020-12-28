package com.elikill58.galloraapi.velocity.impl.entity;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.galloraapi.api.entity.AbstractProxyPlayer;
import com.elikill58.galloraapi.universal.Version;
import com.elikill58.galloraapi.velocity.VelocityAdapter;

import net.kyori.adventure.text.Component;

public class VelocityPlayer extends AbstractProxyPlayer {

	private final com.velocitypowered.api.proxy.Player pp;
	
	public VelocityPlayer(com.velocitypowered.api.proxy.Player pp) {
		this.pp = pp;
	}

	@Override
	public UUID getUniqueId() {
		return pp.getUniqueId();
	}
	
	@Override
	public String getIP() {
		return pp.getRemoteAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return pp.isActive();
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersionByProtocolID(pp.getProtocolVersion().getProtocol());
	}

	@Override
	public boolean isOp() {
		return pp.hasPermission("*");
	}

	@Override
	public void sendMessage(String msg) {
		pp.sendMessage(Component.text(msg));
	}

	@Override
	public String getName() {
		return pp.getUsername();
	}

	@Override
	public Object getDefault() {
		return pp;
	}

	@Override
	public boolean hasPermission(String perm) {
		return pp.hasPermission(perm);
	}
	
	@Override
	public void kick(String reason) {
		pp.disconnect(Component.text(reason));
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		// TODO implement channelID for all channel and not only negativity's one
		pp.sendPluginMessage(VelocityAdapter.NEGATIVITY_CHANNEL_ID, writeMessage);
	}

	@Override
	public int getPing() {
		return (int) pp.getPing();
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return pp.getRemoteAddress();
	}
}

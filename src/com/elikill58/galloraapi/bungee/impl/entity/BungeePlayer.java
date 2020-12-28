package com.elikill58.galloraapi.bungee.impl.entity;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.elikill58.galloraapi.api.entity.AbstractProxyPlayer;
import com.elikill58.galloraapi.universal.Version;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayer extends AbstractProxyPlayer {

	private final ProxiedPlayer pp;
	
	public BungeePlayer(ProxiedPlayer pp) {
		this.pp = pp;
	}

	@Override
	public UUID getUniqueId() {
		return pp.getUniqueId();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getIP() {
		return pp.getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return pp.isConnected();
	}

	@Override
	public Version getPlayerVersion() {
		return Version.getVersionByProtocolID(pp.getPendingConnection().getVersion());
	}

	@Override
	public boolean isOp() {
		return pp.hasPermission("*");
	}

	@Override
	public void sendMessage(String msg) {
		pp.sendMessage(new ComponentBuilder(msg).create());
	}

	@Override
	public String getName() {
		return pp.getName();
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
		pp.disconnect(new ComponentBuilder(reason).create());
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		pp.sendData(channelId, writeMessage);
	}

	@Override
	public int getPing() {
		return pp.getPing();
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return pp.getPendingConnection().getVirtualHost();
	}
}

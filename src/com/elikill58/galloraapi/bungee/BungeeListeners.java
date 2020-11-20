package com.elikill58.galloraapi.bungee;

import java.io.IOException;
import java.util.ArrayList;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.events.EventManager;
import com.elikill58.galloraapi.api.events.player.LoginEvent;
import com.elikill58.galloraapi.api.events.player.LoginEvent.Result;
import com.elikill58.galloraapi.api.events.player.PlayerConnectEvent;
import com.elikill58.galloraapi.api.events.player.PlayerLeaveEvent;
import com.elikill58.galloraapi.bungee.impl.entity.BungeePlayer;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Messages;
import com.elikill58.galloraapi.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("deprecation")
public class BungeeListeners implements Listener {
	
	@EventHandler
	public void onPreLogin(net.md_5.bungee.api.event.LoginEvent e) {
		PendingConnection co = e.getConnection();
		LoginEvent event = new LoginEvent(co.getUniqueId(), co.getName(), e.isCancelled() ? Result.KICK_BANNED : Result.ALLOWED, co.getAddress().getAddress(), e.getCancelReason());
		EventManager.callEvent(event);
		if(!event.getLoginResult().equals(Result.ALLOWED)) {
			e.setCancelled(true);
			e.setCancelReason(new ComponentBuilder(event.getKickMessage()).create());
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onServerChange(ServerConnectedEvent event) {
		try {
			ClientModsListMessage message = new ClientModsListMessage(event.getPlayer().getModList());
			event.getServer().sendData(NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(message));
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage : " + e.getMessage());
		}
	}

	public static class Report {

		private Object[] place;
		private String cmd;

		public Report(String cmd, Object... parts) {
			place = new Object[] { "%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2], "%ping%",
					parts[3] };
			this.cmd = cmd;
		}

		public TextComponent toMessage(ProxiedPlayer p) {
			TextComponent msg = new TextComponent(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
			if (hover.contains("\\n")) {
				ArrayList<TextComponent> components = new ArrayList<>();
				TextComponent hoverMessage = new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 2]).color(ChatColor.GOLD)
								.create());
				hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
				hoverMessage.addExtra(new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 1]).create()));
				components.add(hoverMessage);
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						(BaseComponent[]) components.toArray(new BaseComponent[components.size()])));
			} else
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg;
		}
	}
}

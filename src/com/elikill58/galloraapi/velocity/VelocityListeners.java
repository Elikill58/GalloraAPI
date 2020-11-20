package com.elikill58.galloraapi.velocity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.events.EventManager;
import com.elikill58.galloraapi.api.events.player.LoginEvent;
import com.elikill58.galloraapi.api.events.player.LoginEvent.Result;
import com.elikill58.galloraapi.api.events.player.PlayerConnectEvent;
import com.elikill58.galloraapi.api.events.player.PlayerLeaveEvent;
import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.Messages;
import com.elikill58.galloraapi.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.galloraapi.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.galloraapi.velocity.impl.entity.VelocityPlayer;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ModInfo;

import net.kyori.text.TextComponent;
import net.kyori.text.TextComponent.Builder;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;

public class VelocityListeners {

	@Subscribe
	public void onLogin(com.velocitypowered.api.event.connection.LoginEvent e) {
		Player p = e.getPlayer();
		LoginEvent event = new LoginEvent(p.getUniqueId(), p.getUsername(), e.getResult().isAllowed() ? Result.ALLOWED : Result.KICK_BANNED, p.getRemoteAddress().getAddress(), "");
		EventManager.callEvent(event);
		if(!event.getLoginResult().equals(Result.ALLOWED))
			e.setResult(ResultedEvent.ComponentResult.denied(TextComponent.of(event.getKickMessage())));
	}

	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@Subscribe
	public void onPlayerQuit(DisconnectEvent e) {
		Player p = e.getPlayer();
		GalloraPlayer np = GalloraPlayer.getPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@Subscribe
	public void onServerChange(ServerConnectedEvent event) {
		List<ModInfo.Mod> modsList = event.getPlayer().getModInfo()
				.map(ModInfo::getMods)
				.orElseGet(Collections::emptyList);
		if (modsList.isEmpty()) {
			return;
		}

		Map<String, String> mods = new HashMap<>();
		for (ModInfo.Mod mod : modsList) {
			mods.put(mod.getId(), mod.getVersion());
		}

		try {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new ClientModsListMessage(mods));
			event.getServer().sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, rawMessage);
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage: " + e.getMessage());
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

		public TextComponent toMessage(Player p) {
			Builder msg = TextComponent.builder(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
			if (hover.contains("\\n")) {
				Builder hoverMessage = TextComponent.builder("");
				hoverMessage.color(TextColor.GOLD);
				for(String s : hover.split("\\n"))
					hoverMessage.append(TextComponent.builder(s));
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
			} else
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.builder(hover).build()));
			msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg.build();
		}
	}
}

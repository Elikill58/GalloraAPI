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
import com.elikill58.galloraapi.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessagesManager;
import com.elikill58.galloraapi.velocity.impl.entity.VelocityPlayer;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ModInfo;

import net.kyori.adventure.text.Component;

public class VelocityListeners {

	@Subscribe
	public void onLogin(com.velocitypowered.api.event.connection.LoginEvent e) {
		Player p = e.getPlayer();
		LoginEvent event = new LoginEvent(p.getUniqueId(), p.getUsername(), e.getResult().isAllowed() ? Result.ALLOWED : Result.KICK_BANNED, p.getRemoteAddress().getAddress(), "");
		EventManager.callEvent(event);
		if(!event.getLoginResult().equals(Result.ALLOWED))
			e.setResult(ResultedEvent.ComponentResult.denied(Component.text(event.getKickMessage())));
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
			byte[] rawMessage = GalloraMessagesManager.writeMessage(new ClientModsListMessage(mods));
			event.getServer().sendPluginMessage(VelocityAdapter.NEGATIVITY_CHANNEL_ID, rawMessage);
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage: " + e.getMessage());
		}
	}
}

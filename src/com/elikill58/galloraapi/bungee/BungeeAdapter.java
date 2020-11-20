package com.elikill58.galloraapi.bungee;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.plugin.ExternalPlugin;
import com.elikill58.galloraapi.api.yaml.config.Configuration;
import com.elikill58.galloraapi.bungee.impl.entity.BungeeOfflinePlayer;
import com.elikill58.galloraapi.bungee.impl.entity.BungeePlayer;
import com.elikill58.galloraapi.bungee.impl.plugin.BungeeExternalPlugin;
import com.elikill58.galloraapi.universal.Platform;
import com.elikill58.galloraapi.universal.ProxyAdapter;
import com.elikill58.galloraapi.universal.account.NegativityAccountManager;
import com.elikill58.galloraapi.universal.account.SimpleAccountManager;
import com.elikill58.galloraapi.universal.logger.JavaLoggerAdapter;
import com.elikill58.galloraapi.universal.logger.LoggerAdapter;
import com.elikill58.galloraapi.universal.pluginMessages.GalloraMessagesManager;
import com.elikill58.galloraapi.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.galloraapi.universal.translation.TranslationProviderFactory;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.google.gson.Gson;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeAdapter extends ProxyAdapter {

	public static BungeeAdapter getAdapter() {
		return (BungeeAdapter) adapter;
	}

	private final Configuration config;
	private final Plugin pl;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Proxy();
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;

	public BungeeAdapter(Plugin pl) {
		this.pl = pl;
		File dataFolder = pl.getDataFolder();
		this.config = UniversalUtils.loadConfig(new File(dataFolder, "config.yml"), "config.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(dataFolder.toPath().resolve("lang"));
		this.logger = new JavaLoggerAdapter(pl.getLogger());
		
		ProxyServer proxy = this.pl.getProxy();
		proxy.getPluginManager().registerListener(pl, new BungeeListeners());
		proxy.registerChannel(GalloraMessagesManager.CHANNEL_ID);
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.BUNGEE;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public void debug(String msg) {
		if(UniversalUtils.DEBUG)
			getLogger().info(msg);
	}

	@Nullable
	@Override
	public InputStream openBundledFile(String name) {
		return pl.getResourceAsStream("assets/" + UniversalUtils.PLUGIN_NAME + "/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {

	}

	@SuppressWarnings("deprecation")
	@Override
	public String getVersion() {
		return ProxyServer.getInstance().getGameVersion();
	}
	
	@Override
	public String getPluginVersion() {
		return pl.getDescription().getVersion();
	}

	@Override
	public void reloadConfig() {

	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		pl.getProxy().getPluginManager().dispatchCommand(pl.getProxy().getConsole(), cmd);
	}

	@Override
	public CompletableFuture<Boolean> isUsingMcLeaks(UUID playerId) {
		return UniversalUtils.requestMcleaksData(playerId.toString()).thenApply(response -> {
			if (response == null) {
				return false;
			}
			try {
				Gson gson = new Gson();
				Map<?, ?> data = gson.fromJson(response, Map.class);
				Object isMcleaks = data.get("isMcleaks");
				if (isMcleaks != null) {
					return Boolean.parseBoolean(isMcleaks.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		});
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		pl.getProxy().getPlayers().forEach((p) -> list.add(p.getUniqueId()));
		return list;
	}

	@Override
	public double[] getTPS() {
		return null;
	}

	@Override
	public double getLastTPS() {
		return 0;
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		pl.getProxy().getPlayers().forEach((p) -> list.add(GalloraPlayer.getPlayer(p.getUniqueId(), () -> new BungeePlayer(p)).getPlayer()));
		return list;
	}

	@Override
	public Player getPlayer(String name) {
		ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(name);
		if(pp == null)
			return null;
		return GalloraPlayer.getPlayer(pp.getUniqueId(), () -> new BungeePlayer(pp)).getPlayer();
	}

	@Override
	public Player getPlayer(UUID uuid) {
		ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(uuid);
		if(pp == null)
			return null;
		return GalloraPlayer.getPlayer(uuid, () -> new BungeePlayer(pp)).getPlayer();
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		Player tempP = getPlayer(name);
		if(tempP != null)
			return tempP;
		return null;
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		Player tempP = getPlayer(uuid);
		if(tempP != null)
			return tempP;
		return new BungeeOfflinePlayer(uuid);
	}
	
	@Override
	public boolean hasPlugin(String name) {
		return pl.getProxy().getPluginManager().getPlugin(name) != null;
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new BungeeExternalPlugin(pl.getProxy().getPluginManager().getPlugin(name));
	}
	
	@Override
	public void runSync(Runnable call) {
		pl.getProxy().getScheduler().schedule(pl, call, 0, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void disable() {
	
	}
}

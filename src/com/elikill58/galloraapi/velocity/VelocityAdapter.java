package com.elikill58.galloraapi.velocity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.elikill58.galloraapi.api.GalloraPlayer;
import com.elikill58.galloraapi.api.entity.OfflinePlayer;
import com.elikill58.galloraapi.api.entity.Player;
import com.elikill58.galloraapi.api.plugin.ExternalPlugin;
import com.elikill58.galloraapi.api.yaml.config.Configuration;
import com.elikill58.galloraapi.universal.Platform;
import com.elikill58.galloraapi.universal.ProxyAdapter;
import com.elikill58.galloraapi.universal.account.NegativityAccountManager;
import com.elikill58.galloraapi.universal.account.SimpleAccountManager;
import com.elikill58.galloraapi.universal.logger.LoggerAdapter;
import com.elikill58.galloraapi.universal.logger.Slf4jLoggerAdapter;
import com.elikill58.galloraapi.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.galloraapi.universal.translation.TranslationProviderFactory;
import com.elikill58.galloraapi.universal.utils.UniversalUtils;
import com.elikill58.galloraapi.velocity.impl.entity.VelocityPlayer;
import com.elikill58.galloraapi.velocity.impl.plugin.VelocityExternalPlugin;
import com.google.gson.Gson;

public class VelocityAdapter extends ProxyAdapter {

	private Configuration config;
	private VelocityNegativity pl;
	private final NegativityAccountManager accountManager = new SimpleAccountManager.Proxy();
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;

	public VelocityAdapter(VelocityNegativity pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config_bungee.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().toPath().resolve("lang"), "NegativityProxy", "CheatHover");
		this.logger = new Slf4jLoggerAdapter(pl.getLogger());
	}
	
	@Override
	public Platform getPlatformID() {
		return Platform.VELOCITY;
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
		return pl.getResourceAsStream("assets/negativity/" + name);
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {

	}

	@Override
	public String getVersion() {
		return pl.getServer().getVersion().getVersion();
	}
	
	@Override
	public String getPluginVersion() {
		return UniversalUtils.NEGATIVITY_VERSION;
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
		pl.getServer().getCommandManager().execute(pl.getServer().getConsoleCommandSource(), cmd);
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
		pl.getServer().getAllPlayers().forEach((p) -> list.add(p.getUniqueId()));
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
	public void sendMessageRunnableHover(com.elikill58.galloraapi.api.entity.Player p, String message, String hover,
			String command) {
		
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		pl.getServer().getAllPlayers().forEach((p) -> list.add(GalloraPlayer.getPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer()));
		return list;
	}

	@Override
	public Player getPlayer(String name) {
		Optional<com.velocitypowered.api.proxy.Player> opt = pl.getServer().getPlayer(name);
		if(opt.isPresent()) {
			com.velocitypowered.api.proxy.Player p = opt.get();
			return GalloraPlayer.getPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer();
		} else
			return null;
	}

	@Override
	public Player getPlayer(UUID uuid) {
		Optional<com.velocitypowered.api.proxy.Player> opt = pl.getServer().getPlayer(uuid);
		if(opt.isPresent()) {
			return GalloraPlayer.getPlayer(uuid, () -> new VelocityPlayer(opt.get())).getPlayer();
		} else
			return null;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		return null;
	}
	
	@Override
	public OfflinePlayer getOfflinePlayer(UUID uuid) {
		return null;
	}

	@Override
	public boolean hasPlugin(String name) {
		return pl.getServer().getPluginManager().isLoaded(name);
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new VelocityExternalPlugin(pl.getServer().getPluginManager().getPlugin(name).orElse(null));
	}
	
	@Override
	public void runSync(Runnable call) {
		pl.getServer().getScheduler().buildTask(pl, call);
	}
}

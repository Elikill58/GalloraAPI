package com.elikill58.galloraapi.universal.dataStorage.file;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.galloraapi.api.yaml.config.Configuration;
import com.elikill58.galloraapi.api.yaml.config.YamlConfiguration;
import com.elikill58.galloraapi.universal.TranslatedMessages;
import com.elikill58.galloraapi.universal.account.NegativityAccount;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;

public class FileNegativityAccountStorage extends NegativityAccountStorage {

	private final File userDir;

	public FileNegativityAccountStorage(File userDir) {
		this.userDir = userDir;
	}

	@Override
	public CompletableFuture<NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			File file = new File(userDir, playerId + ".yml");
			if (!file.exists()) {
				return new NegativityAccount(playerId);
			}
			Configuration config = YamlConfiguration.load(file);
			String playerName = config.getString("playername");
			String language = config.getString("lang", TranslatedMessages.getDefaultLang());
			String IP = config.getString("ip", "0.0.0.0");
			long creationTime = config.getLong("creation-time", System.currentTimeMillis());
			return new NegativityAccount(playerId, playerName, language, IP, creationTime);
		});
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.runAsync(() -> {
			File file = new File(userDir, account.getPlayerId() + ".yml");
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Configuration accountConfig = YamlConfiguration.load(file);
			accountConfig.set("playername", account.getPlayerName());
			accountConfig.set("lang", account.getLang());
			accountConfig.set("ip", account.getIp());
			accountConfig.set("creation-time", account.getCreationTime());
			accountConfig.save();
		});
	}
	
	@Override
	public List<UUID> getPlayersOnIP(String ip) {
		// TODO Implement getting players on IP when using file system
		return Collections.emptyList();
	}
}

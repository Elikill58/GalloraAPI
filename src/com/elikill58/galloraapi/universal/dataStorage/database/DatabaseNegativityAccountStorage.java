package com.elikill58.galloraapi.universal.dataStorage.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.galloraapi.universal.Database;
import com.elikill58.galloraapi.universal.account.NegativityAccount;
import com.elikill58.galloraapi.universal.dataStorage.NegativityAccountStorage;

public class DatabaseNegativityAccountStorage extends NegativityAccountStorage {

	public DatabaseNegativityAccountStorage() {
		try {
			Connection connection = Database.getConnection();
			if (connection != null) {
				DatabaseMigrator.executeRemainingMigrations(connection, "accounts");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CompletableFuture<NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.supplyAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_accounts WHERE id = ?")) {
				stm.setString(1, playerId.toString());
				ResultSet result = stm.executeQuery();
				if (result.next()) {
					String playerName = result.getString("playername");
					String language = result.getString("language");
					String IP = result.getString("ip");
					long creationTime = result.getTimestamp("creation_time").getTime();
					return new NegativityAccount(playerId, playerName, language, IP, creationTime);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return new NegativityAccount(playerId);
		});
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.runAsync(() -> {
			try (PreparedStatement stm = Database.getConnection().prepareStatement(
					"REPLACE INTO negativity_accounts (id, playername, language, ip, creation_time) VALUES (?, ?, ?, ?, ?)")) {
				stm.setString(1, account.getPlayerId().toString());
				stm.setString(2, account.getPlayerName());
				stm.setString(3, account.getLang());
				stm.setString(4, account.getIp());
				stm.setTimestamp(5, new Timestamp(account.getCreationTime()));
				stm.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public List<UUID> getPlayersOnIP(String ip) {
		return CompletableFuture.supplyAsync(() -> {
			List<UUID> list = new ArrayList<>();
			try (PreparedStatement stm = Database.getConnection().prepareStatement("SELECT * FROM negativity_accounts WHERE ip = ?")) {
				stm.setString(1, ip);
				ResultSet result = stm.executeQuery();
				while (result.next()) {
					list.add(UUID.fromString(result.getString("id")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return list;
		}).join();
	}
}

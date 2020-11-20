package com.elikill58.galloraapi.universal.account;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.galloraapi.universal.Adapter;
import com.elikill58.galloraapi.universal.TranslatedMessages;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
public final class NegativityAccount {

	private final UUID playerId;
	private String lang, playerName, ip;
	private final long creationTime;
	private boolean inBanning = false, isMcLeaks = false;

	public NegativityAccount(UUID playerId) {
		this(playerId, null, TranslatedMessages.getDefaultLang(), "0.0.0.0", System.currentTimeMillis());
	}

	public NegativityAccount(UUID playerId, String playerName, String lang, String ip, long creationTime) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.lang = lang;
		this.ip = ip;
		this.creationTime = creationTime;
		Adapter.getAdapter().isUsingMcLeaks(playerId).thenAccept(isUsingMcLeaks -> {
			this.isMcLeaks = isUsingMcLeaks;
		});
	}

	public UUID getPlayerId() {
		return playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@NonNull
	public static NegativityAccount get(UUID accountId) {
		return Adapter.getAdapter().getAccountManager().getNow(accountId);
	}

	public long getCreationTime() {
		return creationTime;
	}
	
	public boolean isMcLeaks() {
		return isMcLeaks;
	}
	
	public void setInBanning(boolean b) {
		this.inBanning = b;
	}

	public boolean isInBanning() {
		return inBanning;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
}

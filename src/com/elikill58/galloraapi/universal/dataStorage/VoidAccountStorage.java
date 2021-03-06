package com.elikill58.galloraapi.universal.dataStorage;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.galloraapi.universal.account.NegativityAccount;

public final class VoidAccountStorage extends NegativityAccountStorage {

	public static final VoidAccountStorage INSTANCE = new VoidAccountStorage();

	@Override
	public CompletableFuture<@Nullable NegativityAccount> loadAccount(UUID playerId) {
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> saveAccount(NegativityAccount account) {
		return CompletableFuture.completedFuture(null);
	}
	
	@Override
	public List<UUID> getPlayersOnIP(String ip) {
		return Collections.emptyList();
	}
}

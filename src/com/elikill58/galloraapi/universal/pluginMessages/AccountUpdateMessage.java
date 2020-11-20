package com.elikill58.galloraapi.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.elikill58.galloraapi.universal.account.NegativityAccount;

public class AccountUpdateMessage implements GalloraMessage {

	public static final byte MESSAGE_ID = 6;

	private NegativityAccount account;

	public AccountUpdateMessage() {
		super();
	}

	public AccountUpdateMessage(NegativityAccount account) {
		this.account = account;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		UUID playerId = new UUID(input.readLong(), input.readLong());
		String playerName = input.readUTF();
		String language = input.readUTF();
		String IP = input.readUTF();
		long creationTime = input.readLong();
		account = new NegativityAccount(playerId, playerName, language, IP, creationTime);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		UUID playerId = account.getPlayerId();
		output.writeLong(playerId.getMostSignificantBits());
		output.writeLong(playerId.getLeastSignificantBits());

		output.writeUTF(account.getPlayerName());
		output.writeUTF(account.getLang());

		output.writeUTF(account.getIp());
		output.writeLong(account.getCreationTime());
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public NegativityAccount getAccount() {
		return account;
	}
}

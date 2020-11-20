package com.elikill58.galloraapi.universal.pluginMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class GalloraMessagesManager {

	private static final Map<Byte, Supplier<GalloraMessage>> MESSAGES_BY_ID;

	public static final String CHANNEL_ID = "gallora:msg";
	public static final int PROTOCOL_VERSION = 1;

	static {
		Map<Byte, Supplier<GalloraMessage>> messages = new HashMap<>();
		messages.put(ProxyPingMessage.MESSAGE_ID, ProxyPingMessage::new);
		messages.put(ClientModsListMessage.MESSAGE_ID, ClientModsListMessage::new);
		messages.put(AccountUpdateMessage.MESSAGE_ID, AccountUpdateMessage::new);
		MESSAGES_BY_ID = Collections.unmodifiableMap(messages);
	}

	/**
	 * Tries to read a message from the given input.
	 * <p>
	 * The message will be identified by the first byte of the input.
	 * If unknown, {@code null} will be returned.
	 *
	 * @param input the stream containing the message
	 * @return the message if it was valid, null otherwise
	 * @throws IOException if an error of some kind occurred whilst reading the message
	 */
	@Nullable
	public static GalloraMessage readMessage(DataInputStream input) throws IOException {
		byte messageId = input.readByte();
		Supplier<GalloraMessage> messageSupplier = MESSAGES_BY_ID.get(messageId);
		if (messageSupplier == null) {
			return null;
		}

		GalloraMessage message = messageSupplier.get();
		message.readFrom(input);
		return message;
	}

	/**
	 * Tries to read a message from the given raw data.
	 * <p>
	 * The message will be identified by the first byte of the input.
	 * If unknown, {@code null} will be returned.
	 *
	 * @param data the raw data of the message
	 * @return the message if it was valid, null otherwise
	 * @throws IOException if an error of some kind occurred whilst reading the message
	 */
	@Nullable
	public static GalloraMessage readMessage(byte[] data) throws IOException {
		try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(data))) {
			return readMessage(input);
		}
	}

	/**
	 * Writes the give message into a raw {@code byte[]}.
	 *
	 * @param message the message to write
	 * @return the raw data of the written message
	 * @throws IOException if an error of some kind occurred whilst writing the message
	 */
	public static byte[] writeMessage(GalloraMessage message) throws IOException {
		try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(); DataOutputStream dataOutput = new DataOutputStream(byteOutput)) {
			dataOutput.writeByte(message.messageId());
			message.writeTo(dataOutput);
			return byteOutput.toByteArray();
		}
	}
}

package com.elikill58.galloraapi.api.yaml.tokens;

import com.elikill58.galloraapi.api.yaml.error.Mark;

public final class StreamEndToken extends Token {
	public StreamEndToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.StreamEnd;
	}
}

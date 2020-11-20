package com.elikill58.galloraapi.api.yaml.tokens;

import com.elikill58.galloraapi.api.yaml.error.Mark;

public final class FlowMappingEndToken extends Token {
	public FlowMappingEndToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.FlowMappingEnd;
	}
}

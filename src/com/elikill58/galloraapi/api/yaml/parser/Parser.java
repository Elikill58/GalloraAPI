package com.elikill58.galloraapi.api.yaml.parser;

import com.elikill58.galloraapi.api.yaml.events.Event;

public interface Parser {
	boolean checkEvent(final Event.ID p0);

	Event peekEvent();

	Event getEvent();
}

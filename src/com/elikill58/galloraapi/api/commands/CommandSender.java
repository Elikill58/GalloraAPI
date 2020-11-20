package com.elikill58.galloraapi.api.commands;

import com.elikill58.galloraapi.api.GalloraObject;

public abstract class CommandSender extends GalloraObject {
	
	public abstract void sendMessage(String msg);

	public abstract String getName();
}

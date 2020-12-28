package com.elikill58.galloraapi.api.commands;

import com.elikill58.galloraapi.api.GalloraObject;

public interface CommandSender extends GalloraObject {
	
	public void sendMessage(String msg);

	public String getName();
}

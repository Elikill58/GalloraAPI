package com.elikill58.galloraapi.api.commands;

import java.util.List;

public interface TabListeners {

	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix);
}

package com.elikill58.galloraapi.api.commands;

import java.util.HashMap;

import com.elikill58.galloraapi.api.events.EventListener;
import com.elikill58.galloraapi.api.events.Listeners;
import com.elikill58.galloraapi.api.events.others.CommandExecutionEvent;
import com.elikill58.galloraapi.api.events.others.TabExecutionEvent;

public class CommandManager implements Listeners {

	private final HashMap<String, CommandListeners> commands = new HashMap<>();
	private final HashMap<String, TabListeners> tabs = new HashMap<>();
	
	public CommandManager() {
		/*NegativityCommand negativity = new NegativityCommand();
		commands.put("negativity", negativity);
		tabs.put("negativity", negativity);*/
	}
	
	@EventListener
	public void onCommand(CommandExecutionEvent e) {
		CommandListeners cmd = commands.get(e.getCommand().toLowerCase());
		if(cmd != null)
			e.setGoodResult(cmd.onCommand(e.getSender(), e.getArgument(), e.getPrefix()));
	}
	
	@EventListener
	public void onTab(TabExecutionEvent e) {
		TabListeners cmd = tabs.get(e.getCommand().toLowerCase());
		if(cmd != null)
			e.setTabContent(cmd.onTabComplete(e.getSender(), e.getArgument(), e.getPrefix()));
	}
	
}

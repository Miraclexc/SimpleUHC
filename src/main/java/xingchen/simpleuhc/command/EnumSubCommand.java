package xingchen.simpleuhc.command;

import java.util.HashMap;
import java.util.Map;

public enum EnumSubCommand {
	HELP("help", ""),
	START("start", ""),
	CREATE("create", ""),
	JOIN("join", ""),
	LEAVE("leave", ""),
	LIST("list", ""),
	GUI("gui", ""),
	RELOAD("reload", "spacetimegap.command.adminCommand.reload");
	
	public static Map<String, EnumSubCommand> lookup;
	
	private String name;
	private String permission;
	
	private EnumSubCommand(String name, String permission) {
		this.name = name;
		this.permission = permission;
	}
	
	public boolean isSubCommand(String arg) {
		return this.name.equalsIgnoreCase(arg);
	}
	
	public String getName() {
		return this.name;
	}

	public String getPermission() {
		return this.permission;
	}
	
	public static EnumSubCommand fromName(String name) {
		return lookup.get(name);
	}
	
	static {
		lookup = new HashMap<>();
		for(EnumSubCommand subCommand : values()) {
			lookup.put(subCommand.getName(), subCommand);
		}
	}
}

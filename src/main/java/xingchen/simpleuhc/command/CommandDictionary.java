package xingchen.simpleuhc.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 命令补全工具
 */
public class CommandDictionary {
	private List<String> commands;

	public CommandDictionary(String... commands) {
		this.commands = new ArrayList<>(commands.length);
		Arrays.stream(commands).forEach(i -> this.commands.add(i));
	}
	
	public Stream<String> searchStream(String prefix) {
		return filterStream(this.commands.stream(), prefix);
	}
	
	/**
	 * 查找符合前缀的字符串
	 * 
	 * @param prefix 前缀
	 * 
	 * @return 符合前缀的字符串列表
	 */
	public List<String> search(String prefix) {
		return this.searchStream(prefix).collect(Collectors.toList());
	}
	
	/**
	 * 所有字符串
	 */
	public List<String> getCommands() {
		return this.commands;
	}
	
	public static List<String> filter(Stream<String> stream, String prefix) {
		return filterStream(stream, prefix).collect(Collectors.toList());
	}
	
	public static Stream<String> filterStream(Stream<String> stream, String prefix) {
		if(prefix == null || prefix.isEmpty()) {
			return stream;
		}
		return stream.filter(i -> i.startsWith(prefix));
	}
}

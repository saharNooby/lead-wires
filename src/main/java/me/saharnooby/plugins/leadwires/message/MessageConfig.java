package me.saharnooby.plugins.leadwires.message;

import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author saharNooby
 * @since 16:41 25.03.2020
 */
public final class MessageConfig {

	private final Map<String, String> messages = new HashMap<>();

	public MessageConfig(@NonNull File file) throws IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		config.load(file);
		for (String key : config.getKeys(false)) {
			this.messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString(key)));
		}
	}

	public MessageConfig() {

	}

	public String format(@NonNull String key, Object... args) {
		return String.format(this.messages.getOrDefault(key, "§c" + key), args);
	}

}

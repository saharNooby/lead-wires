package me.saharnooby.plugins.leadwires.module.placement;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author saharNooby
 * @since 10:35 30.03.2020
 */
@Getter
final class ModuleConfig {

	private final boolean enabled;
	private final int maxLength;
	private final boolean continueFromLastPoint;
	private final String permission;
	private final Set<String> allowedBlocks;
	private final Map<String, String> messages;

	public ModuleConfig(@NonNull ConfigurationSection section) {
		this.enabled = section.getBoolean("enabled");
		this.maxLength = section.getInt("maxLength");
		this.continueFromLastPoint = section.getBoolean("continueFromLastPoint");
		this.permission = section.getString("permission", "");

		List<String> allowedBlocks = section.getStringList("allowedBlocks");

		if (allowedBlocks == null) {
			throw new IllegalArgumentException("allowedBlocks not set in the config");
		}

		this.allowedBlocks = new HashSet<>(allowedBlocks);

		this.messages = new HashMap<>();

		ConfigurationSection messages = section.getConfigurationSection("messages");

		if (messages != null) {
			for (String key : messages.getKeys(false)) {
				this.messages.put(key, ChatColor.translateAlternateColorCodes('&', messages.getString(key, key)));
			}
		}
	}

}

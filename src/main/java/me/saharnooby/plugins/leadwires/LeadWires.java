package me.saharnooby.plugins.leadwires;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.NonNull;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPI;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPIImpl;
import me.saharnooby.plugins.leadwires.chunk.LoadedChunkTracker;
import me.saharnooby.plugins.leadwires.command.*;
import me.saharnooby.plugins.leadwires.listener.PlaceToolListener;
import me.saharnooby.plugins.leadwires.listener.RemoveToolListener;
import me.saharnooby.plugins.leadwires.message.MessageConfig;
import me.saharnooby.plugins.leadwires.metrics.Metrics;
import me.saharnooby.plugins.leadwires.tracker.WireTracker;
import me.saharnooby.plugins.leadwires.tracker.WireTrackerListener;
import me.saharnooby.plugins.leadwires.wire.WireStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * @author saharNooby
 * @since 20:20 24.03.2020
 */
public final class LeadWires extends JavaPlugin {

	@Getter
	private static LeadWires instance;

	private final WireStorage storage = new WireStorage();

	private WireTracker tracker;

	private LeadWiresAPI api;

	private MessageConfig messages;

	@Override
	public void onEnable() {
		instance = this;

		try {
			this.storage.load();
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Failed to load wire storage", e);
		}

		LoadedChunkTracker chunkTracker = new LoadedChunkTracker(this);
		Bukkit.getPluginManager().registerEvents(chunkTracker, this);
		ProtocolLibrary.getProtocolManager().addPacketListener(chunkTracker);

		this.tracker = new WireTracker(this.storage, chunkTracker);
		Bukkit.getPluginManager().registerEvents(new WireTrackerListener(this.tracker), this);

		// Check all players that are currently online in case of /reload command.
		this.tracker.checkAllPlayers();
		// Check all players every 5 sec just in case.
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> this.tracker.checkAllPlayers(), 5 * 20, 5 * 20);

		this.api = new LeadWiresAPIImpl(this.storage, this.tracker);

		Bukkit.getPluginManager().registerEvents(new PlaceToolListener(), this);
		Bukkit.getPluginManager().registerEvents(new RemoveToolListener(), this);

		registerCommands();

		try {
			loadMessages();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Failed to load plugin messages", e);

			this.messages = new MessageConfig();
		}

		new Metrics(this, 6873);
	}

	@Override
	public void onDisable() {
		this.storage.close();
		this.tracker.removeAllPlayers();
	}

	private void registerCommands() {
		getCommand("add-wire").setExecutor(new AddWireCommand());
		getCommand("remove-wire").setExecutor(new RemoveWireCommand());
		getCommand("remove-wires-in-block").setExecutor(new RemoveWiresInBlockCommand());
		getCommand("remove-nearest-wire").setExecutor(new RemoveNearestWireCommand());
		getCommand("wire-tool").setExecutor(new WireToolCommand());
	}

	private void loadMessages() throws IOException, InvalidConfigurationException {
		File dir = getDataFolder();

		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Failed to mkdir " + dir);
		}

		File file = new File(dir, "messages.yml");

		if (!file.exists()) {
			Files.copy(LeadWires.class.getResourceAsStream("/messages.yml"), file.toPath());
		}

		this.messages = new MessageConfig(file);
	}

	public static void sendMessage(@NonNull CommandSender sender, @NonNull String key, Object... args) {
		sender.sendMessage(instance.messages.format(key, args));
	}

	/**
	 * @return An API instance.
	 */
	public static LeadWiresAPI getApi() {
		return instance.api;
	}

}

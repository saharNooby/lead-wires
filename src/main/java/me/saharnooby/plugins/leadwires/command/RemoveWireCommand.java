package me.saharnooby.plugins.leadwires.command;

import me.saharnooby.plugins.leadwires.LeadWires;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class RemoveWireCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.remove-wire")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		UUID uuid;

		try {
			uuid = UUID.fromString(args[0]);
		} catch (Exception e) {
			LeadWires.sendMessage(sender, "invalidUUIDFormat");
			return true;
		}

		if (!LeadWires.getApi().getWire(uuid).isPresent()) {
			LeadWires.sendMessage(sender, "wireDoesNotExists", uuid);
			return true;
		}

		LeadWires.getApi().removeWire(uuid);

		LeadWires.sendMessage(sender, "wireRemoved", uuid);

		return true;
	}

}

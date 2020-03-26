package me.saharnooby.plugins.leadwires.command;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.wire.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class AddWireCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.add-wire")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		if (args.length != 1 + 3 + 3 && args.length != 1 + 3 + 3 + 1) {
			return false;
		}

		World world = Bukkit.getWorld(args[0]);

		if (world == null) {
			LeadWires.sendMessage(sender, "worldNotFound", args[0]);
			return true;
		}

		Vector a;
		Vector b;

		try {
			a = new Vector(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
			b = new Vector(Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]));
		} catch (NumberFormatException e) {
			return false;
		}

		UUID uuid;

		if (args.length > 7) {
			try {
				uuid = UUID.fromString(args[7]);
			} catch (Exception e) {
				LeadWires.sendMessage(sender, "invalidUUIDFormat");
				return true;
			}
		} else {
			uuid = UUID.randomUUID();
		}

		if (LeadWires.getApi().getWire(uuid).isPresent()) {
			LeadWires.sendMessage(sender, "wireAlreadyExists", uuid);
			return true;
		}

		LeadWires.getApi().addWire(uuid, new Location(world, a.getX(), a.getY(), a.getZ()), new Location(world, b.getX(), b.getY(), b.getZ()));

		LeadWires.sendMessage(sender, "wireCreated", uuid);

		return true;
	}

}

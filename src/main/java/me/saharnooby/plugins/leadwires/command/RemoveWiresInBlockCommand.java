package me.saharnooby.plugins.leadwires.command;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.wire.Vector;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class RemoveWiresInBlockCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.remove-wires-in-block")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		if (args.length != 4) {
			return false;
		}

		World world = Bukkit.getWorld(args[0]);

		if (world == null) {
			LeadWires.sendMessage(sender, "worldNotFound", args[0]);
			return true;
		}

		Location loc;

		try {
			loc = new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
		} catch (NumberFormatException e) {
			return false;
		}

		Set<UUID> toRemove = new HashSet<>();

		for (Wire wire: LeadWires.getApi().getWires().values()) {
			if (wire.getWorld().equals(world.getName()) && (isInBlock(loc, wire.getA()) || isInBlock(loc, wire.getB()))) {
				toRemove.add(wire.getUuid());
			}
		}

		for (UUID uuid : toRemove) {
			LeadWires.getApi().removeWire(uuid);
		}

		LeadWires.sendMessage(sender, "wiresRemoved", toRemove.size());

		return true;
	}

	private static boolean isInBlock(@NonNull Location loc, @NonNull Vector vec) {
		return loc.getBlockX() == Math.floor(vec.getX()) &&
				loc.getBlockY() == Math.floor(vec.getY()) &&
				loc.getBlockZ() == Math.floor(vec.getZ());
	}

}

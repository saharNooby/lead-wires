package me.saharnooby.plugins.leadwires.command;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.wire.Wire;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class RemoveNearestWireCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.remove-nearest-wire")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		Location loc;

		if (sender instanceof Entity) {
			loc = ((Entity) sender).getLocation();
		} else if (sender instanceof BlockCommandSender) {
			loc = ((BlockCommandSender) sender).getBlock().getLocation().add(0.5, 0.5, 0.5);
		} else {
			LeadWires.sendMessage(sender, "canNotDetermineLocation");
			return true;
		}

		String world = loc.getWorld().getName();
		Vector vector = loc.toVector();

		Wire nearest = null;

		for (Wire wire : LeadWires.getApi().getWires().values()) {
			if (wire.getWorld().equals(world) && (nearest == null || dist(vector, nearest) > dist(vector, wire))) {
				nearest = wire;
			}
		}

		if (nearest == null) {
			LeadWires.sendMessage(sender, "noWiresInWorld", world);
			return true;
		}

		LeadWires.getApi().removeWire(nearest.getUuid());

		LeadWires.sendMessage(sender, "wireRemoved", nearest.getUuid());

		return true;
	}

	private static double dist(@NonNull Vector vec, @NonNull Wire wire) {
		double a = vec.distanceSquared(new Vector(wire.getA().getX(), wire.getA().getY(), wire.getA().getZ()));
		double b = vec.distanceSquared(new Vector(wire.getB().getX(), wire.getB().getY(), wire.getB().getZ()));
		return Math.min(a, b);
	}

}

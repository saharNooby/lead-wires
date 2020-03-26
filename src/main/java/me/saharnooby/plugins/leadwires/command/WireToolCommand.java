package me.saharnooby.plugins.leadwires.command;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class WireToolCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.use-tools")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		if (!(sender instanceof Player)) {
			LeadWires.sendMessage(sender, "notAPlayer");
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		ItemStack stack;

		switch (args[0]) {
			case "place":
			case "placer":
				stack = Tools.getPlaceTool();
				break;
			case "place-thick":
			case "thick-placer":
				stack = Tools.getThickPlaceTool();
				break;
			case "remove":
			case "remover":
				stack = Tools.getRemoveTool();
				break;
			default:
				return false;
		}

		((Player) sender).getInventory().addItem(stack);

		LeadWires.sendMessage(sender, "toolGiven", args[0]);

		return true;
	}

}

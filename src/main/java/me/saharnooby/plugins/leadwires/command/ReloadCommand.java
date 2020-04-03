package me.saharnooby.plugins.leadwires.command;

import me.saharnooby.plugins.leadwires.LeadWires;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author saharNooby
 * @since 12:30 25.03.2020
 */
public final class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.reload")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		LeadWires.getInstance().reload();

		LeadWires.sendMessage(sender, "pluginReloaded");

		return true;
	}

}

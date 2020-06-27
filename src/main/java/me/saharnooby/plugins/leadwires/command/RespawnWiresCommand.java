package me.saharnooby.plugins.leadwires.command;

import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.tracker.PlayerDebugInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author saharNooby
 * @since 13:13 27.06.2020
 */
public final class RespawnWiresCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("leadwires.respawn-wires")) {
			LeadWires.sendMessage(sender, "noPermissions");
			return true;
		}

		Player target;

		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendMessage("§cSpecify player name");
				return true;
			}
		} else if (args.length == 1) {
			target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				sender.sendMessage("§cPlayer not found");
				return true;
			}
		} else {
			sender.sendMessage("§c/respawn-wires [player name]");
			return true;
		}

		LeadWires.getInstance().respawnWires(target);

		sender.sendMessage("§aWires were respawned for " + target.getName());

		PlayerDebugInfo info = LeadWires.getInstance().getPlayerDebugInfo(target);

		sender.sendMessage("§7Additional info:");
		sender.sendMessage("§7- chunks visible to the player: §f" + info.getVisibleChunks().size());
		sender.sendMessage("§7- wires visible to the player: §f" + info.getSentWires().size());

		return true;
	}

}

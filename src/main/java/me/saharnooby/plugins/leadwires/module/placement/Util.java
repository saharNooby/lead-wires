package me.saharnooby.plugins.leadwires.module.placement;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.util.NMSUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * @author saharNooby
 * @since 19:24 30.03.2020
 */
final class Util {

	static boolean isLead(@NonNull Material type) {
		return type.name().equals("LEASH") || type.name().equals("LEAD");
	}

	static void takeFromHand(@NonNull PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (NMSUtil.getMinorVersion() < 9 || e.getHand() != EquipmentSlot.OFF_HAND) {
			takeFromMainHand(player);
		} else {
			takeFromOffHand(player);
		}
	}

	private static void takeFromMainHand(@NonNull Player player) {
		player.setItemInHand(takeOne(player.getItemInHand()));
	}

	private static void takeFromOffHand(@NonNull Player player) {
		player.getInventory().setItemInOffHand(takeOne(player.getInventory().getItemInOffHand()));
	}

	private static ItemStack takeOne(@NonNull ItemStack stack) {
		ItemStack copy = stack.clone();
		copy.setAmount(copy.getAmount() - 1);
		return copy.getAmount() == 0 ? new ItemStack(Material.AIR) : copy;
	}

}

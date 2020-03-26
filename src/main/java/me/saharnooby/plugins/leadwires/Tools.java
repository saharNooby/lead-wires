package me.saharnooby.plugins.leadwires;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author saharNooby
 * @since 16:06 25.03.2020
 */
public final class Tools {

	public static ItemStack getPlaceTool() {
		return getItem(Material.STICK, "§6Wire Placer");
	}

	public static boolean isPlaceTool(ItemStack stack) {
		return isItem(stack, Material.STICK, "§6Wire Placer");
	}

	public static ItemStack getThickPlaceTool() {
		ItemStack item = getItem(Material.STICK, "§bThick Wire Placer");
		item.addUnsafeEnchantment(Enchantment.LUCK, 1);
		return item;
	}

	public static boolean isThickPlaceTool(ItemStack stack) {
		return isItem(stack, Material.STICK, "§bThick Wire Placer");
	}

	public static ItemStack getRemoveTool() {
		return getItem(Material.BLAZE_ROD, "§cWire Remover");
	}

	public static boolean isRemoveTool(ItemStack stack) {
		return isItem(stack, Material.BLAZE_ROD, "§cWire Remover");
	}

	private static ItemStack getItem(@NonNull Material type, @NonNull String name) {
		ItemStack stack = new ItemStack(type);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
		return stack;
	}

	private static boolean isItem(ItemStack stack, @NonNull Material type, @NonNull String name) {
		return stack != null && stack.getType() == type && stack.hasItemMeta() && name.equals(stack.getItemMeta().getDisplayName());
	}


}

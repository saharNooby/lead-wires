package me.saharnooby.plugins.leadwires.listener;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.Tools;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author saharNooby
 * @since 16:09 25.03.2020
 */
public final class PlaceToolListener implements Listener {

	private static final String PLACER_KEY = "LeadWires.placer.first";
	private static final String THICK_PLACER_KEY = "LeadWires.thickPlacer.first";

	@EventHandler
	public void onPlacerUse(PlayerInteractEvent e) {
		if (Tools.isPlaceTool(e.getItem())) {
			onPlacerUse(e, PLACER_KEY, false);
		}
	}

	@EventHandler
	public void onThickPlacerUse(PlayerInteractEvent e) {
		if (Tools.isThickPlaceTool(e.getItem())) {
			onPlacerUse(e, THICK_PLACER_KEY, true);
		}
	}

	private static void onPlacerUse(@NonNull PlayerInteractEvent e, @NonNull String metaKey, boolean isThick) {
		e.setCancelled(true);

		Player player = e.getPlayer();

		if (!player.hasPermission("leadwires.use-tools")) {
			LeadWires.sendMessage(player, "noPermissionsForTool");
			return;
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();

			if (player.hasMetadata(metaKey)) {
				Block first = (Block) player.getMetadata(metaKey).get(0).value();

				if (first.equals(block)) {
					LeadWires.sendMessage(player, "blockAlreadySelected");
					return;
				}

				if (first.getWorld() != block.getWorld()) {
					LeadWires.sendMessage(player, "worldsAreDifferent");
					return;
				}

				player.removeMetadata(metaKey, LeadWires.getInstance());
				player.setMetadata(metaKey, new FixedMetadataValue(LeadWires.getInstance(), block));

				placeWire(first, block, isThick);

				LeadWires.sendMessage(player, "wirePlaced");
			} else {
				player.setMetadata(metaKey, new FixedMetadataValue(LeadWires.getInstance(), block));

				LeadWires.sendMessage(player, "firstPointSet");
			}
		} else if (e.getAction().name().startsWith("LEFT_CLICK_")) {
			if (player.hasMetadata(metaKey)) {
				player.removeMetadata(metaKey, LeadWires.getInstance());

				LeadWires.sendMessage(player, "selectionReset");
			} else {
				LeadWires.sendMessage(player, "noBlockSelected");
			}
		}
	}

	private static void placeWire(@NonNull Block from, @NonNull Block to, boolean isThick) {
		Location a = from.getLocation().add(0.5, 0.5, 0.5);
		Location b = to.getLocation().add(0.5, 0.5, 0.5);

		if (a.getY() > b.getY()) {
			// Swap the locations so the lead does not bend upwards.
			Location temp = a;
			a = b;
			b = temp;
		}

		LeadWires.getApi().addWire(a, b);

		if (isThick) {
			double v = 0.8 / 16; // Vertical offset
			double h = 0.4 / 16; // Horizontal offset
			LeadWires.getApi().addWire(a.clone().add(0, v, 0), b.clone().add(0, v, 0));
			LeadWires.getApi().addWire(a.clone().add(h, v / 2, h), b.clone().add(h, v / 2, h));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		player.removeMetadata(PLACER_KEY, LeadWires.getInstance());
		player.removeMetadata(THICK_PLACER_KEY, LeadWires.getInstance());
	}

}

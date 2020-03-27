package me.saharnooby.plugins.leadwires.listener;

import lombok.NonNull;
import me.saharnooby.plugins.leadwires.LeadWires;
import me.saharnooby.plugins.leadwires.Tools;
import me.saharnooby.plugins.leadwires.api.LeadWiresAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

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

		LeadWiresAPI api = LeadWires.getApi();

		if (!isThick) {
			api.addWire(a, b);
			return;
		}

		// The lead consists of two long bent rectangles, each angled at 45 degrees. It gives leads "X" shape.
		// Width of the rectangle is 0.025 (as set in Minecraft client code). So, width of the wire itself is 0.025 / sqrt(2), or 0.0176.
		// To place a thick wire, we can place three wires in the shape of a triangle:
		// ><><
		//  ><
		// Center of the triangle will be the initial location specified by the player.
		// The resulting wire will appear as 2x thicker that regular.

		double size = 0.025 / Math.sqrt(2);
		double half = size / 2;

		Vector dir = b.toVector().subtract(a.toVector()).setY(0).normalize();
		// A vector that is perpendicular to the horizontal direction of the wire.
		double px = -dir.getZ();
		double pz = dir.getX();

		// Bottom wire
		api.addWire(add(a, 0, -half, 0), add(b, 0, -half, 0));
		// Top wires
		api.addWire(add(a, -half * px, half, -half * pz), add(b, -half * px, half, -half * pz));
		api.addWire(add(a, half * px, half, half * pz), add(b, half * px, half, half * pz));
	}
	
	// Calls add() on a copy of the location.
	private static Location add(@NonNull Location loc, double x, double y, double z) {
		return loc.clone().add(x, y, z);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		player.removeMetadata(PLACER_KEY, LeadWires.getInstance());
		player.removeMetadata(THICK_PLACER_KEY, LeadWires.getInstance());
	}

}

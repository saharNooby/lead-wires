package me.saharnooby.plugins.leadwires.module.placement;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author saharNooby
 * @since 10:30 30.03.2020
 */
@RequiredArgsConstructor
final class LeadBreakListener implements Listener {

	private final LeadPlacementModule module;

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		checkBlockLater(e.getBlock());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent e) {
		checkBlocksLater(new ArrayList<>(e.blockList()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockExplode(BlockExplodeEvent e) {
		checkBlocksLater(new ArrayList<>(e.blockList()));
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		checkBlockLater(e.getBlock());
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		checkBlockLater(e.getBlock());
	}

	private void checkBlockLater(@NonNull Block block) {
		checkBlocksLater(Collections.singletonList(block));
	}

	private void checkBlocksLater(@NonNull Collection<Block> blocks) {
		this.module.checkBlocksLater(blocks);
	}

}

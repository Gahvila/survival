package net.gahvila.survival.Features.ElytraDisabler;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraReplacer implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!event.getWorld().getEnvironment().equals(World.Environment.THE_END)) return;

        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ItemFrame itemFrame) {
                ItemStack item = itemFrame.getItem();
                if (item.getType() == Material.ELYTRA) {
                    itemFrame.setItem(new ItemStack(Material.NETHERITE_BLOCK));
                }
            }
        }
    }
}

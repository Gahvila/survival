package net.gahvila.selviytymisharpake.PlayerFeatures.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunkUnload implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        // Get entities inside chunk
        Entity[] ents = event.getChunk().getEntities();

        // Find pets
        for (int i = 0; i < ents.length; ++i)
        {
            Entity ent = ents[i];
            // Get pets that can teleport with you
            if (ent.getType() == EntityType.WOLF || ent.getType() == EntityType.CAT || ent.getType() == EntityType.PARROT)
            {
                // Teleport standing pets
                Sittable sit = (Sittable) ent;
                if (!sit.isSitting())
                {
                    Tameable tame = (Tameable) ent;

                    // But only if the player is online!
                    if (tame.getOwner() instanceof Player)
                    {
                        Player ply = (Player) tame.getOwner();
                        ent.teleport(ply.getLocation());
                        ply.sendMessage("Lemmikkisi teleportattiin luoksesi!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event)
    {
        // Get entities inside chunk
        List<Entity> ents = event.getFrom().getEntities();

        // Find pets
        for (int i = 0; i < ents.size(); ++i)
        {
            Entity ent = ents.get(i);
            // Get pets that can teleport with you
            if (ent.getType() == EntityType.WOLF || ent.getType() == EntityType.CAT || ent.getType() == EntityType.PARROT)
            {
                // Teleport standing pets
                Sittable sit = (Sittable) ent;
                if (!sit.isSitting())
                {
                    Tameable tame = (Tameable) ent;

                    // But only if the player is online!
                    if (tame.getOwner() instanceof Player)
                    {
                        Player ply = (Player) tame.getOwner();
                        ent.teleport(ply.getLocation());
                        ent.setPortalCooldown(300); // Don't want pet to go through nether portal instantly
                        ply.sendMessage("Lemmikkisi teleportattiin luoksesi!");
                    }
                }
            }
        }
    }
}

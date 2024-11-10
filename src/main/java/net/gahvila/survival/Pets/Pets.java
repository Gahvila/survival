package net.gahvila.survival.Pets;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.gahvila.survival.survival;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

import static net.gahvila.gahvilacore.Utils.MiniMessageUtils.toMM;

public class Pets implements Listener {
    public static HashMap<Player, Player> transferingPet = new HashMap<>();
    public void registerCommands() {
        new CommandAPICommand("givepet")
                .withArguments(new PlayerArgument("nimi"))
                .executesPlayer((p, args) -> {
                    if (transferingPet.containsKey(p)) {
                        p.sendMessage("Peruttu lemmikin antaminen.");
                        transferingPet.remove(p);
                    } else {
                        Player receiver = (Player) args.get("nimi");
                        p.sendMessage(toMM("Aloitit lemmikin siirtämisen pelaajalle <#85FF00>" + receiver.getName() + "</#85FF00>."));
                        p.sendMessage(toMM("Sinulla on <#85FF00>10 sekuntia</#85FF00> aikaa valita lemmikki. Siirrettävän valitset oikea-klikkaamalla sitä."));
                        transferingPet.put(p, receiver);

                        Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
                            @Override
                            public void run() {
                                if (transferingPet.get(p) != null) {
                                    transferingPet.remove(p);
                                    p.sendMessage(toMM("Siirto vanhentui."));
                                }
                            }
                        }, 20 * 10);
                    }
                })
                .register();
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e){
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!(e.getRightClicked() instanceof Tameable pet)) return;

        Player currentOwner = e.getPlayer();
        UUID currentOwnerUUID = e.getPlayer().getUniqueId();

        if (pet.getOwnerUniqueId() != currentOwnerUUID) return;
        if (!transferingPet.containsKey(currentOwner)) return;

        e.setCancelled(true);
        Player receiver = transferingPet.get(currentOwner);

        pet.setOwner(receiver);
        pet.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, true, false));

        currentOwner.playSound(receiver.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_VILLAGER_YES, 2F, 1F);

        currentOwner.sendMessage(toMM("Lemmikki siirretty pelaajalle <#85FF00>" + receiver.getName() + "</#85FF00> onnistuneesti."));
        receiver.sendMessage(toMM("Pelaaja <#85FF00>" + currentOwner.getName() + "</#85FF00> siirsi lemmikin omistajuuden sinulle."));

        transferingPet.remove(currentOwner);
    }

    //pet teleport
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Chunk teleportedFrom = e.getFrom().getChunk();
        if (teleportedFrom.isForceLoaded()) return;

        teleportedFrom.addPluginChunkTicket(survival.instance);
        Bukkit.getServer().getScheduler().runTaskLater(survival.instance, new Runnable() {
            @Override
            public void run() {
                teleportedFrom.removePluginChunkTicket(survival.instance);
            }
        },20L * 10);
    }

    //anti kill
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) return;
        if (!(e.getEntity() instanceof Tameable pet)) return;
        if (e.getEntityType() == EntityType.TRADER_LLAMA) return;

        UUID currentOwnerUUID = pet.getOwnerUniqueId();
        UUID damagerUUID = damager.getUniqueId();

        if (damagerUUID == currentOwnerUUID) return;

        e.setCancelled(true);
        damager.sendMessage("Et voi vahingoittaa tuota lemmikkiä.");

    }
}

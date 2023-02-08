package net.gahvila.selviytymisharpake.PlayerFeatures.Events;

import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackListener;
import net.gahvila.selviytymisharpake.PlayerFeatures.Spawn.SpawnTeleport;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import static java.lang.Long.MAX_VALUE;

public class PlayerDeath implements Listener {


    @EventHandler
    public void onDeath(PlayerDeathEvent e){

        Player p = e.getPlayer();
        EntityDamageEvent.DamageCause dc = p.getLastDamageCause().getCause();
        switch (dc) {

            case SONIC_BOOM:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fhälytti wardenin kylille.");
                break;
            case BLOCK_EXPLOSION:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fpainoi väärää numeroa.");
                break;
            case CONTACT:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §ftökittiin kuoliaaksi.");
                break;
            case CRAMMING:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §foli kontaktissa liian monen elukan kanssa ;)");
                break;
            case CUSTOM:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli tuntemattomasta syystä.");
                break;
            case DRAGON_BREATH:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli äärikäärmeen hönkäyksestä.");
                break;
            case DROWNING:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §favasi suunsa veden alla.");
                break;
            case ENTITY_ATTACK:
                break;
            case ENTITY_EXPLOSION:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli Creeperin itsemurhahyökkäyksen johdattamana.");
                break;
            case FALL:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fputosi kuolemaansa.");
                break;
            case FALLING_BLOCK:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fsai luovan idean jäädä putoavan asian alle!");
                break;
            case FIRE:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkoki tulen lämmön ylitsepääsemättömäksi.");
                break;
            case FIRE_TICK:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fpaloi tuhkaksi.");
                break;
            case FLY_INTO_WALL:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkoki liike-energiaa ja kuoli.");
                p.setGliding(false);
                break;
            case FREEZE:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fjäätyi.");
                break;
            case HOT_FLOOR:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fastui kuumien kivien päälle.");
                break;
            case LAVA:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fyritti uida laavassa.");
                break;
            case LIGHTNING:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli ukkosesta, taitava?");
                break;
            case MAGIC:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli taikajuomien johdosta.");
                break;
            case PROJECTILE:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fjäi katsomaan nopeasti lähestyvää objektia.");
                break;
            case STARVATION:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli nälkään.");
                break;
            case SUFFOCATION:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §ftukehtui.");
                break;
            case THORNS:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli piikkihaarniskaan.");
                break;
            case VOID:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §ftipahti litteän maailman laidalta.");
                break;
            case WITHER:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuihtui pois.");
                break;
            default:
                e.setDeathMessage("§8[§c☠§8] §e" + p.getDisplayName() + " §fkuoli.");

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        if (p.getBedSpawnLocation() != null){
            if (!BackListener.died.contains(p.getUniqueId())){
                BackListener.back.put(p, p.getLocation());
            }
            p.teleportAsync(p.getBedSpawnLocation());
            p.sendMessage("");
            p.sendMessage("");

            p.sendMessage("Höh, sinä kuolit. Sinut teleportattiin sängyllesi.\n§fTeleportataksesi kuolinpaikallesi, suorita komento §e/back§f.");
        }else{
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage("");
            p.sendMessage("");
            p.sendMessage("Höh, sinä kuolit eikä sinulla ole sänkyä asetettuna. Sinut teleportattiin spawnille.\n§fTeleportataksesi kuolinpaikallesi, suorita komento §e/back§f.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, MAX_VALUE, 1F);
            e.setRespawnLocation(new Location(Bukkit.getWorld("spawn"), -2.5, 102, 1.5, 0.0f, 0.0f));
            if (!BackListener.died.contains(p.getUniqueId())){
                BackListener.back.put(p, p.getLocation());
            }
            SpawnTeleport.teleportSpawn(p);

        }
    }
}

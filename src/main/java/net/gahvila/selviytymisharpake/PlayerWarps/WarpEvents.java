package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
import net.gahvila.selviytymisharpake.PlayerFeatures.Back.BackManager;
import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class WarpEvents implements Listener {
    private final WarpManager warpManager;


    public WarpEvents(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (warpManager.settingWarp.containsKey(p.getUniqueId())) {
            if (p.hasPermission("warps.yes")) {
                if (!e.getMessage().equals("lopeta")) {
                    switch (warpManager.settingWarp.get(p.getUniqueId())) {
                        //Set komento kysyy haluaako asettaa warpin
                        case 1:
                            e.setCancelled(true);
                            if (e.getMessage().matches("[a-zA-ZöÖäÄåÅ0-9]*")) {
                                if (e.getMessage().length() <= 32) {
                                    if (!warpManager.getWarps().contains(e.getMessage())){
                                        warpManager.settingWarp.put(p.getUniqueId(), 2);
                                        warpManager.settingWarpName.put(p.getUniqueId(), e.getMessage());
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("");
                                        p.sendMessage("OK! Warpin nimeksi tulee §e" + e.getMessage());
                                        p.sendMessage("");
                                        p.sendMessage("§fHaluatko että warpin käyttö maksaa kultaa?");
                                        p.sendMessage("§fVoit syöttää hinnan §e0-100 §fväliltä.");
                                        p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                                    }else{
                                        p.sendMessage("Tuolla nimellä on jo warppi.");
                                    }
                                } else {
                                    p.sendMessage("Warpin nimi voi olla maksimissaan 32 kirjainta pitkä.");
                                }
                            } else {
                                p.sendMessage("Warpin nimi voi sisältää vain aakkosia ja numeroita.");
                            }
                            break;
                        case 2:
                            e.setCancelled(true);
                            if (e.getMessage().matches("[0-9]*")) {
                                Integer price = Integer.valueOf(e.getMessage());
                                if (price > -1 && price < 101) {
                                    warpManager.settingWarp.put(p.getUniqueId(), 3);
                                    warpManager.settingWarpPrice.put(p.getUniqueId(), Integer.valueOf(e.getMessage()));
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("");
                                    p.sendMessage("§fOK! Warppisi käyttöhinta tulee olemaan §e" + e.getMessage());
                                    p.sendMessage("");
                                    p.sendMessage("§fValmista, nyt sinun vain täytyy varmistaa että kaikki tässä on oikein:");
                                    p.sendMessage("§fWarpin käyttöhinta: §e" + warpManager.settingWarpPrice.get(p.getUniqueId()));
                                    p.sendMessage("§fWarpin nimi: §e" + warpManager.settingWarpName.get(p.getUniqueId()));
                                    p.sendMessage("§cVarmista, että olet sijainnissa johon haluat warpin tulevan.");
                                    p.sendMessage("");
                                    p.sendMessage("Kun kaikki on valmista, kirjoita '§evarmista§f' chattiin, ja warppi asetetaan.");
                                    p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                                } else {
                                    p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                                }
                            } else {
                                p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                            }
                            break;
                        case 3:
                            e.setCancelled(true);
                            if (e.getMessage().equals("varmista")) {
                                p.sendMessage("");
                                p.sendMessage("");
                                p.sendMessage("");
                                p.sendMessage("");
                                p.sendMessage("");
                                p.sendMessage("");
                                p.sendMessage("§fKiitoksia! Warppisi on nyt julkisesti saatavilla koko palvelimelle.");
                                warpManager.saveWarp(p, warpManager.settingWarpName.get(p.getUniqueId()), p.getLocation(), warpManager.settingWarpPrice.get(p.getUniqueId()));

                                //
                                warpManager.settingWarp.remove(p.getUniqueId());
                                warpManager.settingWarpPrice.remove(p.getUniqueId());
                                warpManager.settingWarpName.remove(p.getUniqueId());
                            } else {
                                p.sendMessage("");
                                p.sendMessage("§cVarmista, että olet sijainnissa johon haluat warpin tulevan.");
                                p.sendMessage("");
                                p.sendMessage("Kun kaikki on valmista, kirjoita '§evarmista§f' chattiin, ja warppi asetetaan.");
                                p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                            }
                            break;
                    }
                } else {
                    e.setCancelled(true);
                    warpManager.settingWarp.remove(p.getUniqueId());
                    warpManager.settingWarpPrice.remove(p.getUniqueId());
                    warpManager.settingWarpName.remove(p.getUniqueId());
                    p.sendMessage("Peruit warpin luonnin.");
                }
            }
        }else if (warpManager.editingWarp.containsKey(p.getUniqueId())){
            if (!e.getMessage().equals("lopeta")) {
                switch (warpManager.editingWarp.get(p.getUniqueId())) {
                    case 1:
                        e.setCancelled(true);
                        if (warpManager.getOwnedWarps(p).contains(e.getMessage())){
                            warpManager.editingWarp.put(p.getUniqueId(), 3);
                            p.sendMessage("");
                            p.sendMessage("§fMihin vaihdetaan hinta?");
                            p.sendMessage("§fVoit syöttää hinnan §e0-50 §fväliltä.");
                            p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                        }else{
                            p.sendMessage("Et omista warppia tuolla nimellä.");
                        }
                        break;
                    case 2:
                        e.setCancelled(true);
                        if (warpManager.getOwnedWarps(p).contains(e.getMessage())){
                            warpManager.editingWarp.put(p.getUniqueId(), 4);
                            p.sendMessage("");
                            p.sendMessage("§fSeiso siellä mihin haluat vaihtaa sijainnin.");
                            p.sendMessage("§fKun olet valmis, kirjoita aivan mitä tahdot chattiin.");
                            p.sendMessage("§7Jos haluat perua warpin luonnin, kirjoita '§elopeta§7' chattiin.");
                        }else{
                            p.sendMessage("Et omista warppia tuolla nimellä.");
                        }
                        break;
                    case 3:
                        e.setCancelled(true);
                        if (e.getMessage().matches("[0-9]*")) {
                            Integer price = Integer.valueOf(e.getMessage());
                            if (price > -1 && price < 51) {
                                warpManager.updateWarpPrice(p, warpManager.settingWarpName.get(p.getUniqueId()), price);
                                p.sendMessage("Warpin hinta muokattu.");
                            } else {
                                p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                            }
                        } else {
                            p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                        }

                        warpManager.editingWarp.remove(p.getUniqueId());
                        warpManager.editingWarpName.remove(p.getUniqueId());
                        break;
                    case 4:
                        e.setCancelled(true);
                        p.sendMessage("Warpin sijainti muokattu.");
                        warpManager.updateWarpLocation(p, warpManager.settingWarpName.get(p.getUniqueId()), p.getLocation());

                        warpManager.editingWarp.remove(p.getUniqueId());
                        warpManager.editingWarpName.remove(p.getUniqueId());
                        break;
                }
            }else{
                e.setCancelled(true);
                warpManager.editingWarp.remove(p.getUniqueId());
                p.sendMessage("Peruit warpin muokkaamisen..");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        warpManager.settingWarp.remove(p.getUniqueId());
        warpManager.settingWarpPrice.remove(p.getUniqueId());
        warpManager.settingWarpName.remove(p.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        warpManager.updateWarpOwnerName(p);

        Integer money = warpManager.getMoneyInQueue(p.getUniqueId().toString());
        if (money > 0){
            Integer toBePaid = warpManager.getMoneyInQueue(String.valueOf(p.getUniqueId()));
            warpManager.setMoneyInQueue(p.getUniqueId().toString(), 0);
            SelviytymisHarpake.getEconomy().depositPlayer(p, toBePaid);
            p.sendMessage("Sinun maksullista warppia käytettiin kun olit poissa, sait §e" + toBePaid + "Ⓖ§f.");
        }

        //WarpManager.setAllowedWarps(p);

        warpManager.settingWarp.remove(p.getUniqueId());
        warpManager.settingWarpPrice.remove(p.getUniqueId());
        warpManager.settingWarpName.remove(p.getUniqueId());
    }
}
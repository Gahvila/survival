package net.gahvila.selviytymisharpake.PlayerWarps;

import net.gahvila.selviytymisharpake.PlayerFeatures.AddonShop.AddonManager;
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


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (WarpManager.settingWarp.containsKey(p.getUniqueId())) {
            if (p.hasPermission("warps.yes")) {
                if (!e.getMessage().equals("lopeta")) {
                    switch (WarpManager.settingWarp.get(p.getUniqueId())) {
                        //Set komento kysyy haluaako asettaa warpin
                        case 1:
                            e.setCancelled(true);
                            if (e.getMessage().matches("[a-zA-ZöÖäÄåÅ0-9]*")) {
                                if (e.getMessage().length() <= 32) {
                                    if (!WarpManager.getWarps().contains(e.getMessage())){
                                        WarpManager.settingWarp.put(p.getUniqueId(), 2);
                                        WarpManager.settingWarpName.put(p.getUniqueId(), e.getMessage());
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
                                        p.sendMessage("§fVoit syöttää hinnan §e0-50 §fväliltä.");
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
                                if (price > -1 && price < 51) {
                                    WarpManager.settingWarp.put(p.getUniqueId(), 3);
                                    WarpManager.settingWarpPrice.put(p.getUniqueId(), Integer.valueOf(e.getMessage()));
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
                                    p.sendMessage("§fWarpin käyttöhinta: §e" + WarpManager.settingWarpPrice.get(p.getUniqueId()));
                                    p.sendMessage("§fWarpin nimi: §e" + WarpManager.settingWarpName.get(p.getUniqueId()));
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
                                WarpManager.saveWarp(p, WarpManager.settingWarpName.get(p.getUniqueId()), p.getLocation(), WarpManager.settingWarpPrice.get(p.getUniqueId()));

                                //
                                WarpManager.settingWarp.remove(p.getUniqueId());
                                WarpManager.settingWarpPrice.remove(p.getUniqueId());
                                WarpManager.settingWarpName.remove(p.getUniqueId());
                            } else {
                                p.sendMessage("Sinulla ei ole tarpeeksi rahaa! Tarvitset 500 kolikkoa.");
                            }
                            break;
                    }
                } else {
                    e.setCancelled(true);
                    WarpManager.settingWarp.remove(p.getUniqueId());
                    WarpManager.settingWarpPrice.remove(p.getUniqueId());
                    WarpManager.settingWarpName.remove(p.getUniqueId());
                    p.sendMessage("Peruit warpin luonnin.");
                }
            }
        }else if (WarpManager.editingWarp.containsKey(p.getUniqueId())){
            if (!e.getMessage().equals("lopeta")) {
                switch (WarpManager.editingWarp.get(p.getUniqueId())) {
                    case 1:
                        e.setCancelled(true);
                        if (WarpManager.getOwnedWarps(p).contains(e.getMessage())){
                            WarpManager.editingWarp.put(p.getUniqueId(), 3);
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
                        if (WarpManager.getOwnedWarps(p).contains(e.getMessage())){
                            WarpManager.editingWarp.put(p.getUniqueId(), 4);
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
                                WarpManager.updateWarpPrice(p, WarpManager.settingWarpName.get(p.getUniqueId()), price);
                                p.sendMessage("Warpin hinta muokattu.");
                            } else {
                                p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                            }
                        } else {
                            p.sendMessage("Voit syöttää vain numeron §e0-50 §fväliltä.");
                        }

                        WarpManager.editingWarp.remove(p.getUniqueId());
                        WarpManager.editingWarpName.remove(p.getUniqueId());
                        break;
                    case 4:
                        e.setCancelled(true);
                        p.sendMessage("Warpin sijainti muokattu.");
                        WarpManager.updateWarpLocation(p, WarpManager.settingWarpName.get(p.getUniqueId()), p.getLocation());

                        WarpManager.editingWarp.remove(p.getUniqueId());
                        WarpManager.editingWarpName.remove(p.getUniqueId());
                        break;
                }
            }else{
                e.setCancelled(true);
                WarpManager.editingWarp.remove(p.getUniqueId());
                p.sendMessage("Peruit warpin muokkaamisen..");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        WarpManager.settingWarp.remove(p.getUniqueId());
        WarpManager.settingWarpPrice.remove(p.getUniqueId());
        WarpManager.settingWarpName.remove(p.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        WarpManager.updateWarpOwnerName(p);

        Integer money = WarpManager.getMoneyInQueue(p.getUniqueId().toString());
        if (money > 0){
            Integer toBePaid = WarpManager.getMoneyInQueue(String.valueOf(p.getUniqueId()));
            WarpManager.setMoneyInQueue(p.getUniqueId().toString(), 0);
            SelviytymisHarpake.getEconomy().depositPlayer(p, toBePaid);
            p.sendMessage("Sinun maksullista warppia käytettiin kun olit poissa, sait §e" + toBePaid + " §fkultaa.");
        }

        //WarpManager.setAllowedWarps(p);

        WarpManager.settingWarp.remove(p.getUniqueId());
        WarpManager.settingWarpPrice.remove(p.getUniqueId());
        WarpManager.settingWarpName.remove(p.getUniqueId());
    }
}
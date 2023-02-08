package net.gahvila.selviytymisharpake.PlayerFeatures.Homes;

import net.gahvila.selviytymisharpake.SelviytymisHarpake;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SetHomeCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (args.length == 1) {

            if (HomeManager.getHomes(p) == null) {
                if (args[0].equalsIgnoreCase("sänky")) {
                    p.sendMessage("Et voi asettaa kotia tuolla nimellä!");
                    return true;
                }

                //Check if name is allowed
                if (args[0].matches("[a-zA-ZöÖäÄåÅ0-9- ]*")) {
                    if (args[0].length() < 16) {

                        //Check if world is not spawn or resurssinether
                        if (!p.getWorld().getName().equals("spawn") || (!p.getWorld().getName().equals("resurssinether"))) {
                            Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                                @Override
                                public void run() {
                                    //Save the home to the database async.
                                    HomeManager.saveHome(p, args[0], p.getLocation());
                                }
                            });
                            p.sendTitle("§a" + args[0], "§7Koti asetettu.", 1, 60, 1);
                            return true;
                        } else {
                            p.sendMessage("Et voi asettaa kotia tässä maassa.");
                        }
                    } else {
                        p.sendMessage("Kodin nimi voi olla vain 16 kirjainta pitkä.");
                    }
                } else {
                    p.sendMessage("Kodin nimessä ei voi olla erikoiskirjaimia.");
                    return true;
                }
            }
            if (HomeManager.getHomes(p).size() < HomeManager.getAllowedHomes(p)) {
                if (args[0].equalsIgnoreCase("sänky")) {
                    p.sendMessage("Et voi asettaa kotia tuolla nimellä!");
                    return true;
                }
                if (args[0].matches("[a-zA-ZöÖäÄåÅ0-9- ]*")) {
                    if (args[0].length() <= 16) {
                        if (!p.getWorld().getName().equals("spawn") || (!p.getWorld().getName().equals("resurssinether"))) {
                            Bukkit.getScheduler().runTaskAsynchronously(SelviytymisHarpake.instance, new Runnable() {
                                @Override
                                public void run() {
                                    HomeManager.saveHome(p, args[0], p.getLocation());
                                }
                            });
                            p.sendTitle("§a" + args[0], "§7Koti asetettu.", 1, 60, 1);
                            return true;
                        } else {
                            p.sendMessage("Et voi asettaa kotia tässä maassa.");
                        }
                    } else {
                        p.sendMessage("\n \n \n");
                        p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                        p.sendMessage("§fKodin nimi voi olla maksimissaan §e16 kirjainta §fpitkä, ja se voi sisältää vain §eaakkosia §fja §enumeroita§f.\n \n§fKomento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot.");
                        p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                        p.sendMessage("§fSuorita komento §e/sethome [kodin nimi] §fasettaaksesi kotisi.");
                    }
                } else {
                    p.sendMessage("\n \n \n");
                    p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                    p.sendMessage("§fKodin nimi voi olla maksimissaan §e16 kirjainta §fpitkä, ja se voi sisältää vain §eaakkosia §fja §enumeroita§f.\n \n§fKomento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot.");
                    p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
                    p.sendMessage("§fSuorita komento §e/sethome [kodin nimi] §fasettaaksesi kotisi.");
                    return true;
                }
            } else {
                p.sendMessage("Sinulla on maksimi määrä koteja.");
            }


        } else {
            p.sendMessage("\n \n \n");
            p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
            p.sendMessage("§fKodin nimi voi olla maksimissaan §e16 kirjainta §fpitkä, ja se voi sisältää vain §eaakkosia §fja §enumeroita§f.\n \n§fKomento asettaa kodin tarkasti siihen kohtaan missä seisot, mukaanlukien sen, minne suuntaan katsot.");
            p.sendMessage("§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
            p.sendMessage("§fSuorita komento §e/sethome [kodin nimi] §fasettaaksesi kotisi.");
            return true;
        }
        return false;
    }
}
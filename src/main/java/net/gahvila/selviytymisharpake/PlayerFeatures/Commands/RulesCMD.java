package net.gahvila.selviytymisharpake.PlayerFeatures.Commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCMD implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                TextComponent rules1 = new TextComponent();
                rules1.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                rules1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                rules1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt text2")); //runs command when they click the text
                switch (args[0]){

                    case "text1":
                        p.sendMessage("");
                        p.sendMessage("§fEnsinnäkin, pärjäät Gahvilassa helpolla, ja useasti rangaistukset ovat todella rentoja jos sellaisia edes annetaan, yleisesti suullinen varoitus riittää kuiteskin, yritetään pitää sama fiilis yllä jatkossakin!");
                        p.spigot().sendMessage(rules1);
                        break;
                    case "text2":
                        p.sendMessage("");
                        p.sendMessage("§fSäännöt voidaan lyhentää helposti kolmeen sanaan, “älä ole tyhmä.” Jos ymmärrät mitä tällä tarkoitetaan, ei sinun välttämättä tarvitse lukea sääntöjä loppuun. Valitettavasti vuosi on 2022, ja osa ei valitettavasti pysty harkitsemaan kolmen maagisen sanan tarkoitusta.");
                        TextComponent rules2 = new TextComponent();
                        rules2.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                        rules2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                        rules2.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt 1")); //runs command when they click the text
                        //Sends online players the clickable texts
                        p.spigot().sendMessage(rules2);
                        break;
                    case "1":
                        p.sendMessage("");
                        p.sendMessage("§6» §fRespektiä, olkaa mukavia. §7(Kategoria: 1. Asiaton käytös)");
                        p.sendMessage("§fIkinä ei ole pakko aiheuttaa kenellekään paskaa fiilistä, vaikka saattaakin tuntua mahdottomalta väitteeltä. Älä ole kusipää chatissa, älä tunge laavaa jonkun jalkoihin, yms.");
                        TextComponent rules3 = new TextComponent();
                        rules3.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                        rules3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                        rules3.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt 2")); //runs command when they click the text
                        //Sends online players the clickable texts
                        p.spigot().sendMessage(rules3);
                        break;
                    case "2":
                        p.sendMessage("");
                        p.sendMessage("§6» §fEpäreilut edut, jee voin häkkää servun meetvurstilla. (Kategoria: 2. Epäreilut edut)");
                        p.sendMessage("§fToivon, että tämä asia on selkeä kaikille. Tähän ei kuulu pelkästään clientit, jotka ovat suunniteltu pelkästään tätä varten. Jotkin yksittäiset moditkin voivat antaa sinulle epäreiluja etuja. Listan sallituista modeista löydät Gahvilan discordista (#modit)");
                        TextComponent rules4 = new TextComponent();
                        rules4.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                        rules4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                        rules4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt 3")); //runs command when they click the text
                        //Sends online players the clickable texts
                        p.spigot().sendMessage(rules4);
                        break;
                    case "3":
                        p.sendMessage("");
                        p.sendMessage("§6» §fPalvelin on rikki, jes! (Kategoria: 3. Hyväksikäyttö)");
                        p.sendMessage("§fLöytyikö jokin oikein mukava aukko, jolla voit tehdä itsestäsi mahtipontisimman pelaajan? Älä kuiteskaan, saat pitkän istunnon sellissä. Sen sijaan, ilmoita asiasta lipukkeella Gahvilan discordissa (#tukikanava).");
                        TextComponent rules5 = new TextComponent();
                        rules5.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                        rules5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                        rules5.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt 4")); //runs command when they click the text
                        //Sends online players the clickable texts
                        p.spigot().sendMessage(rules5);
                        break;
                    case "4":
                        p.sendMessage("");
                        p.sendMessage("§6» §fTähän voisi vielä listata lyhyesti asioita, joita emme salli.");
                        p.sendMessage(" §6- §7Rangaistusten kiertäminen.");
                        p.sendMessage(" §6- §7Asiattomat nimet ja skinit.");
                        p.sendMessage("Kiitoksia, kun lukaisit säännöt läpi!");
                        break;
                    default:
                        p.sendMessage("");
                        p.sendMessage("Ensinnäkin, pärjäät Gahvilassa helpolla, ja useasti rangaistukset ovat todella rentoja jos sellaisia edes annetaan, yleisesti suullinen varoitus riittää kuiteskin, yritetään pitää sama fiilis yllä jatkossakin!");
                        p.spigot().sendMessage(rules1);
                        break;
                }
            }else{
                p.sendMessage("");
                p.sendMessage("Ensinnäkin, pärjäät Gahvilassa helpolla, ja useasti rangaistukset ovat todella rentoja jos sellaisia edes annetaan, yleisesti suullinen varoitus riittää kuiteskin, yritetään pitää sama fiilis yllä jatkossakin!");
                TextComponent rules1 = new TextComponent();
                rules1.setText("§a§lJATKA SEURAAVAAN"); //set clickable text
                rules1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§bKlikkaa minua!").create())); //display text msg when hovering
                rules1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/säännöt text2")); //runs command when they click the text
                //Sends online players the clickable texts
                p.spigot().sendMessage(rules1);
            }
        }
        return false;
    }
}

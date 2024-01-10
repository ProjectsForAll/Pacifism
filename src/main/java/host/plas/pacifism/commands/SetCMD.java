package host.plas.pacifism.commands;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.players.PVPPlayer;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.Sender;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class SetCMD extends SimplifiedCommand {
    public SetCMD() {
        super("setpvp", Pacifism.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        Optional<CommandSender> senderOptional = ctx.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            ctx.sendMessage("&cCould not find you as a sender!");
            return true;
        }
        CommandSender sender = senderOptional.get();

        if (! ctx.isArgUsable(0)) {
            ctx.sendMessage("&cYou must specify a value to set!");
            return true;
        }

        String value = ctx.getStringArg(0).toLowerCase();
        boolean valueBool;
        switch (value) {
            case "1":
            case "on":
            case "true":
                valueBool = true;
                break;
            case "0":
            case "off":
            case "false":
                valueBool = false;
                break;
            default:
                ctx.sendMessage("&cYou must specify a valid boolean value!");
                return true;
        }

        OfflinePlayer target = null;

        if (ctx.isArgUsable(1)) {
            if (! sender.hasPermission("togglepvp.others.toggle")) {
                ctx.sendMessage("&cYou do not have permission to toggle other players' PVP!");
                return true;
            }

            String targetName = ctx.getStringArg(1);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
            if (offlinePlayer == null) {
                ctx.sendMessage("That player does not exist!");
                return true;
            }

            target = offlinePlayer;
        } else {
            if (sender instanceof Player) {
                target = (OfflinePlayer) sender;
            } else {
                ctx.sendMessage("&cYou must be a player to use that part of the command!");
                return true;
            }
        }

        PVPPlayer pvpPlayer = PVPPlayer.getOrGetPlayer(target.getUniqueId().toString());

        pvpPlayer.setPvpEnabled(valueBool);

        if (! sender.equals(target)) {
            ctx.sendMessage("&eYou have " + (pvpPlayer.isPvpEnabled() ? "&aenabled" : "&cdisabled") + " " +
                    "&b" + target.getName() + "&e's PVP&8!" + (pvpPlayer.isPvpEnabled() ? "\n&7(They will be able to " +
                    "take damage from other " +
                    "players.)" : "\n&7(They will not be able to take damage from other players.)"));
            if (target.getPlayer() != null) {
                Sender targetSender = new Sender(target.getPlayer());
                targetSender.sendMessage("&eYour PVP has been " + (pvpPlayer.isPvpEnabled() ? "&aenabled" : "&cdisabled") + "&e" +
                        " by &b" + sender.getName() + "&e!");
            }
        } else {
            ctx.sendMessage("&eYou have " + (pvpPlayer.isPvpEnabled() ? "&aenabled" : "&cdisabled") + " &eyour PVP&8!" +
                    (pvpPlayer.isPvpEnabled() ? "\n&7(You will be able to take damage from other players.)" : "\n&7(You will not be able to take damage from other players.)"));
        }
        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext ctx) {
        Optional<OfflinePlayer> senderOptional = ctx.getSender().getOfflinePlayer();
        if (senderOptional.isEmpty()) {
            return new ConcurrentSkipListSet<>();
        }

        if (ctx.getArgs().size() == 2) {
            OfflinePlayer sender = senderOptional.get();
            if (sender.getPlayer() == null) {
                return new ConcurrentSkipListSet<>();
            }
            Player sPlayer = sender.getPlayer();

            if (!sPlayer.hasPermission("togglepvp.others.set")) {
                return new ConcurrentSkipListSet<>();
            }

            if (!ctx.isArgUsable(1)) {
                return new ConcurrentSkipListSet<>();
            }

            return new ConcurrentSkipListSet<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        } else if (ctx.getArgs().size() <= 1) {
            return new ConcurrentSkipListSet<>(List.of("on", "off", "true", "false", "1", "0"));
        } else {
            return new ConcurrentSkipListSet<>();
        }
    }
}

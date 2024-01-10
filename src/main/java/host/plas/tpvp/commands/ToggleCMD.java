package host.plas.tpvp.commands;

import host.plas.tpvp.TogglePVP;
import host.plas.tpvp.players.PVPPlayer;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.Sender;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class ToggleCMD extends SimplifiedCommand {
    public ToggleCMD() {
        super("togglepvp", TogglePVP.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        Optional<CommandSender> senderOptional = ctx.getSender().getCommandSender();
        if (senderOptional.isEmpty()) {
            ctx.sendMessage("&cCould not find you as a sender!");
            return true;
        }
        CommandSender sender = senderOptional.get();

        OfflinePlayer target = null;

        if (ctx.isArgUsable(0)) {
            if (! sender.hasPermission("togglepvp.others.toggle")) {
                ctx.sendMessage("&cYou do not have permission to toggle other players' PVP!");
                return true;
            }

            String targetName = ctx.getStringArg(0);
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

        pvpPlayer.togglePVP();

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

        OfflinePlayer sender = senderOptional.get();
        if (sender.getPlayer() == null) {
            return new ConcurrentSkipListSet<>();
        }
        Player sPlayer = sender.getPlayer();

        if (! sPlayer.hasPermission("togglepvp.others.toggle")) {
            return new ConcurrentSkipListSet<>();
        }

        if (! ctx.isArgUsable(0)) {
            return new ConcurrentSkipListSet<>();
        }

        return new ConcurrentSkipListSet<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }
}

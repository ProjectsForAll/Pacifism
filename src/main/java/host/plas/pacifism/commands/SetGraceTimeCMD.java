package host.plas.pacifism.commands;

import host.plas.bou.commands.CommandArgument;
import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.Sender;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class SetGraceTimeCMD extends SimplifiedCommand {
    public SetGraceTimeCMD() {
        super("setgracetime", Pacifism.getInstance());
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

        Optional<Long> valueOptional = ctx.getLongArg(0);
        if (valueOptional.isEmpty()) {
            ctx.sendMessage("&cYou must specify a valid number!");
            return true;
        }
        long value = valueOptional.get();

        OfflinePlayer target = null;

        if (ctx.isArgUsable(1)) {
            if (! sender.hasPermission("pacifism.others.gracetime")) {
                ctx.sendMessage("&cYou do not have permission to set other players' grace-time!");
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

        PacifismPlayer pvpPlayer = PlayerManager.getOrGetPlayer(target.getUniqueId().toString());

        pvpPlayer.setAddedGraceTicks(value);

        if (! sender.equals(target)) {
            ctx.sendMessage("&eYou have set &b" + target.getName() + "&e's grace-time to &b" + value + "&e!");
            if (target.getPlayer() != null) {
                target.getPlayer().sendMessage("&eYour grace-time has been set to &b" + value + "&e!");
            }
        } else {
            ctx.sendMessage("&eYou have set your grace-time to &b" + value + "&e!");
        }
        return true;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext ctx) {
        ConcurrentSkipListSet<String> completions = new ConcurrentSkipListSet<>();

        Optional<OfflinePlayer> senderOptional = ctx.getSender().getOfflinePlayer();
        if (senderOptional.isEmpty()) {
            return completions;
        }

        OfflinePlayer sender = senderOptional.get();
        if (sender.getPlayer() == null) {
            return completions;
        }
        Player sPlayer = sender.getPlayer();


        if (ctx.getArgs().size() == 2) {
            if (sPlayer.hasPermission("pacifism.force")) completions.add("-f");

            if (! sPlayer.hasPermission("pacifism.others.gracetime")) {
                return completions;
            } else {
                if (ctx.getArgs().size() != 1) return completions;

                completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ConcurrentSkipListSet::new)));
            }

            return completions;
        } else if (ctx.getArgs().size() <= 1) {
            if (! sPlayer.hasPermission("pacifism.command.set")) return completions;

            completions.addAll(List.of("100", "200", "300", "400", "500", "600", "700", "800", "900", "1000"));

            return completions;
        } else {
            return completions;
        }
    }
}

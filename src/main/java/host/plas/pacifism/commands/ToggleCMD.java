package host.plas.pacifism.commands;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import host.plas.bou.commands.CommandArgument;
import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.Sender;
import host.plas.bou.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class ToggleCMD extends SimplifiedCommand {
    public ToggleCMD() {
        super("togglepvp", Pacifism.getInstance());
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

        if (ctx.isArgUsable(0) && ! ctx.getStringArg(0).equals("-f")) {
            if (! sender.hasPermission("pacifism.others.toggle")) {
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

        boolean isForced = false;
        for (CommandArgument argument : ctx.getArgs()) {
            if (argument.getContent().equals("-f") && sender.hasPermission("pacifism.force")) {
                isForced = true;
                break;
            }
        }

        PacifismPlayer pvpPlayer = PlayerManager.getOrGetPlayer(target.getUniqueId().toString());
        boolean valueBool = ! pvpPlayer.isPvpEnabled();

        if (! isForced) {
            if (! pvpPlayer.canTogglePvp() && valueBool != pvpPlayer.isPvpEnabled() && Pacifism.getMainConfig().getPlayerToggleCooldownEnabled()) {
                if (sender.equals(target)) {
                    String css = Pacifism.getMessageConfig().getToggleCannotSelfSelf();
                    String cstl = Pacifism.getMessageConfig().getToggleCannotSelfTimeLeft()
                            .replace("%time_seconds%", String.valueOf(pvpPlayer.getCooldownSecondsLeft()))
                            ;

                    if (! css.isBlank()) ctx.sendMessage(css);
                    if (! cstl.isBlank()) ctx.sendMessage(cstl);
                } else {
                    String cos = Pacifism.getMessageConfig().getToggleCannotOtherSelf()
                            .replace("%player_name%", target.getName())
                            ;
                    String cotl = Pacifism.getMessageConfig().getToggleCannotOtherTimeLeft()
                            .replace("%time_seconds%", String.valueOf(pvpPlayer.getCooldownSecondsLeft()))
                            ;

                    if (! cos.isBlank()) ctx.sendMessage(cos);
                    if (! cotl.isBlank()) ctx.sendMessage(cotl);
                }
                return false;
            }
        }

        pvpPlayer.setPvpEnabledAs(valueBool);

        if (! sender.equals(target)) {
            String os = Pacifism.getMessageConfig().getToggleOtherSelf()
                    .replace("%player_name%", target.getName())
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;
            String oue = Pacifism.getMessageConfig().getToggleOtherUponEnable()
                    .replace("%player_name%", sender.getName())
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;
            String oud = Pacifism.getMessageConfig().getToggleOtherUponDisable()
                    .replace("%player_name%", sender.getName())
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;

            if (! os.isBlank()) ctx.sendMessage(os);
            if (! oue.isBlank() && pvpPlayer.isPvpEnabled()) ctx.sendMessage(oue);
            if (! oud.isBlank() && ! pvpPlayer.isPvpEnabled()) ctx.sendMessage(oud);

            if (target.getPlayer() != null) {
                String oo = Pacifism.getMessageConfig().getToggleOtherOther()
                        .replace("%player_name%", sender.getName())
                        .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                        ;

                Sender targetSender = new Sender(target.getPlayer());
                if (! oo.isBlank()) targetSender.sendMessage(oo);
            }
        } else {
            String ss = Pacifism.getMessageConfig().getToggleSelfSelf()
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;
            String sue = Pacifism.getMessageConfig().getToggleSelfUponEnable()
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;
            String sud = Pacifism.getMessageConfig().getToggleSelfUponDisable()
                    .replace("%status%", pvpPlayer.isPvpEnabled() ? Pacifism.getMessageConfig().getStatusEnabled() : Pacifism.getMessageConfig().getStatusDisabled())
                    ;

            if (! ss.isBlank()) ctx.sendMessage(ss);
            if (! sue.isBlank() && pvpPlayer.isPvpEnabled()) ctx.sendMessage(sue);
            if (! sud.isBlank() && ! pvpPlayer.isPvpEnabled()) ctx.sendMessage(sud);
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

        if (sPlayer.hasPermission("pacifism.force")) {
            completions.add("-f");
        }

        if (! sPlayer.hasPermission("pacifism.others.toggle")) {
            return completions;
        } else {
            if (ctx.getArgs().size() != 1) {
                return completions;
            }

            completions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ConcurrentSkipListSet::new)));
        }

        return completions;
    }
}

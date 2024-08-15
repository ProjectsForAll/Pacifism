package host.plas.pacifism.utils;

import host.plas.bou.commands.Sender;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.config.WorldConfig;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class WorldUtils {
    public static boolean canWorldPacify(World world) {
        WorldConfig worldConfig = Pacifism.getWorldConfig();
        return worldConfig.canPacifyInWorld(world);
    }

    public static boolean canWorldPacify(Location from) {
        return canWorldPacify(from.getWorld());
    }

    public static boolean check(Player damagerPlayer, Player damagedPlayer, Cancellable event) {
        PacifismPlayer damagerPVPPlayer = PlayerManager.getOrGetPlayer(damagerPlayer);
        PacifismPlayer damagedPVPPlayer = PlayerManager.getOrGetPlayer(damagedPlayer);

        if (! damagerPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cYou have PVP disabled! You cannot hurt other players.");
            return true;
        }

        if (! damagedPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cThat player has PVP disabled! You cannot hurt them.");
            return true;
        }

        return false;
    }
}

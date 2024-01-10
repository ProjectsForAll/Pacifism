package host.plas.pacifism.runnables;

import host.plas.pacifism.players.PVPPlayer;
import io.streamlined.bukkit.instances.BaseRunnable;
import org.bukkit.Bukkit;

public class Ticker extends BaseRunnable {
    @Override
    public void execute() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PVPPlayer pvpPlayer = PVPPlayer.getOrGetPlayer(player);
            pvpPlayer.tick();
        });
    }
}

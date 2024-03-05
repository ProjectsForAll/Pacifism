package host.plas.pacifism.runnables;

import host.plas.pacifism.players.PacifismPlayer;
import io.streamlined.bukkit.instances.BaseRunnable;
import org.bukkit.Bukkit;

public class Ticker extends BaseRunnable {
    @Override
    public void execute() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PacifismPlayer pvpPlayer = PacifismPlayer.getOrGetPlayer(player);
            pvpPlayer.tick();
        });
    }
}

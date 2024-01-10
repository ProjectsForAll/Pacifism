package host.plas.pacifism;

import host.plas.pacifism.commands.SetCMD;
import host.plas.pacifism.commands.ToggleCMD;
import host.plas.pacifism.config.MainConfig;
import host.plas.pacifism.events.MainListener;
import host.plas.pacifism.players.PVPPlayer;
import host.plas.pacifism.runnables.Ticker;
import io.streamlined.bukkit.PluginBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter @Setter
public final class Pacifism extends PluginBase {
    @Getter @Setter
    private static Pacifism instance;

    @Getter @Setter
    private static MainConfig mainConfig;

    @Getter @Setter
    private static Ticker ticker;

    @Getter @Setter
    private static MainListener mainListener;

    @Getter @Setter
    private static ToggleCMD toggleCMD;
    @Getter @Setter
    private static SetCMD setCMD;

    public Pacifism() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        instance = this;

        mainConfig = new MainConfig();

        ticker = new Ticker();

        mainListener = new MainListener();
        Bukkit.getPluginManager().registerEvents(mainListener, this);

        toggleCMD = new ToggleCMD();
        toggleCMD.register();
        setCMD = new SetCMD();
        setCMD.register();
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        ticker.cancel();

        PVPPlayer.getPlayers().forEach(pvpPlayer -> {
            pvpPlayer.saveAll();
            PVPPlayer.unregisterPlayer(pvpPlayer.getUuid());
        });
    }
}

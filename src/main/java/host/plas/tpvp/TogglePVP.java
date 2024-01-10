package host.plas.tpvp;

import host.plas.tpvp.commands.SetCMD;
import host.plas.tpvp.commands.ToggleCMD;
import host.plas.tpvp.config.MainConfig;
import host.plas.tpvp.events.MainListener;
import host.plas.tpvp.players.PVPPlayer;
import host.plas.tpvp.runnables.Ticker;
import io.streamlined.bukkit.PluginBase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter @Setter
public final class TogglePVP extends PluginBase {
    @Getter @Setter
    private static TogglePVP instance;

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

    public TogglePVP() {
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

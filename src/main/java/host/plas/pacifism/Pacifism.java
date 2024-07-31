package host.plas.pacifism;

import host.plas.bou.BetterPlugin;
import host.plas.pacifism.commands.SetCMD;
import host.plas.pacifism.commands.ToggleCMD;
import host.plas.pacifism.commands.WorldWhitelistCMD;
import host.plas.pacifism.config.MainConfig;
import host.plas.pacifism.config.WorldConfig;
import host.plas.pacifism.database.PacifismDBOperator;
import host.plas.pacifism.events.MainListener;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import host.plas.pacifism.runnables.Ticker;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter @Setter
public final class Pacifism extends BetterPlugin {
    @Getter @Setter
    private static Pacifism instance;

    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static WorldConfig worldConfig;

    @Getter @Setter
    private static Ticker ticker;

    @Getter @Setter
    private static MainListener mainListener;

    @Getter @Setter
    private static ToggleCMD toggleCMD;
    @Getter @Setter
    private static SetCMD setCMD;
    @Getter @Setter
    private static WorldWhitelistCMD worldWhitelistCMD;
    @Getter @Setter
    private static PacifismDBOperator dbOperator;

    public Pacifism() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        instance = this;

        mainConfig = new MainConfig();
        worldConfig = new WorldConfig();

        dbOperator = new PacifismDBOperator(mainConfig.getConnectorSet());

        ticker = new Ticker();

        mainListener = new MainListener();
        Bukkit.getPluginManager().registerEvents(mainListener, this);

        toggleCMD = new ToggleCMD();
        setCMD = new SetCMD();
        worldWhitelistCMD = new WorldWhitelistCMD();
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
        ticker.cancel();

        PlayerManager.getLoadedPlayers().forEach(PacifismPlayer::unload);
    }
}

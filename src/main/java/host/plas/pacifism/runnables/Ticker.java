package host.plas.pacifism.runnables;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;

public class Ticker extends BaseRunnable {
    public Ticker() {
        super(0, 1);
    }

    @Override
    public void run() {
        PlayerManager.getLoadedPlayers().forEach(PacifismPlayer::tick);
    }
}

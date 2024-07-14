package host.plas.pacifism.managers;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.players.PacifismPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class PlayerManager {
    @Getter @Setter
    private static ConcurrentSkipListSet<PacifismPlayer> loadedPlayers = new ConcurrentSkipListSet<>();

    public static void loadPlayer(PacifismPlayer player) {
        loadedPlayers.add(player);
    }

    public static void unloadPlayer(String uuid) {
        loadedPlayers.removeIf(player -> player.getIdentifier().equals(uuid));
    }

    public static Optional<PacifismPlayer> getPlayer(String uuid) {
        for (PacifismPlayer player : loadedPlayers) {
            if (player.getIdentifier().equals(uuid)) {
                return Optional.of(player);
            }
        }

        return Optional.empty();
    }

    public static Optional<PacifismPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    public static PacifismPlayer createNewPlayer(String uuid) {
        return new PacifismPlayer(uuid);
    }

    public static PacifismPlayer getOrGetPlayer(String uuid) {
        Optional<PacifismPlayer> player = getPlayer(uuid);
        if (player.isPresent()) {
            return player.get();
        }

        PacifismPlayer newPlayer = createNewPlayer(uuid);
        newPlayer = newPlayer.augment(Pacifism.getDbOperator().loadPlayer(uuid));

        loadPlayer(newPlayer);

        return newPlayer;
    }

    public static PacifismPlayer getOrGetPlayer(Player player) {
        return getOrGetPlayer(player.getUniqueId().toString());
    }
}

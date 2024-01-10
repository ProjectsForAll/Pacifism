package host.plas.pacifism.players;

import host.plas.pacifism.Pacifism;
import io.streamlined.bukkit.commands.Sender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.quaint.savables.SavableResource;
import tv.quaint.storage.documents.SimpleJsonDocument;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class PVPPlayer extends SavableResource {
    @Getter @Setter
    public static class Serializer extends SimpleJsonDocument {
        public Serializer(String uuid) {
            super(uuid + ".json", getPlayerFolder(), false);
        }

        public static File getPlayerFolder() {
            File mainFolder = Pacifism.getInstance().getDataFolder();
            File playerFolder = new File(mainFolder, "players");

            if (!playerFolder.exists()) {
                playerFolder.mkdirs();
            }

            return playerFolder;
        }

        @Override
        public void onInit() {

        }

        @Override
        public void onSave() {

        }
    }

    private boolean pvpEnabled;
    @Setter
    private int playTicks;
    @Setter
    private boolean toggledByForce;
    @Setter
    private boolean hasToggled;

    public void setPvpEnabled(boolean bool) {
        pvpEnabled = bool;

        hasToggled = true;
    }

    public PVPPlayer(String uuid) {
        super(uuid, new Serializer(uuid));
    }

    @Override
    public void populateDefaults() {
        pvpEnabled = getOrSetDefault("pvp-enabled", false);
        playTicks = getOrSetDefault("play-ticks", 0);
        toggledByForce = getOrSetDefault("toggled-by-force", false);
        hasToggled = getOrSetDefault("has-toggled", false);
    }

    @Override
    public void loadValues() {
        pvpEnabled = getOrSetDefault("pvp-enabled", false);
        playTicks = getOrSetDefault("play-ticks", 0);
        toggledByForce = getOrSetDefault("toggled-by-force", false);
        hasToggled = getOrSetDefault("has-toggled", false);
    }

    @Override
    public void saveAll() {
        set("pvp-enabled", pvpEnabled);
        set("play-ticks", playTicks);
        set("toggled-by-force", toggledByForce);
        set("has-toggled", hasToggled);
    }

    public void togglePVP() {
        setPvpEnabled(! pvpEnabled);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(getUuid()));
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public void tick() {
        if (! isOnline()) return;
        Player player = getPlayer();

        playTicks ++;

        if (! toggledByForce && ! hasToggled) {
            boolean enabled = Pacifism.getMainConfig().getPlayerForceToggleEnabled();
            if (enabled) {
                boolean setAs = Pacifism.getMainConfig().getPlayerForceToggleSetAs();
                int ticks = Pacifism.getMainConfig().getPlayerForceToggleTicks();
                String message = Pacifism.getMainConfig().getPlayerForceToggleMessage();
                boolean sendMessage = Pacifism.getMainConfig().getPlayerForceToggleSendMessage();

                if (playTicks >= ticks) {
                    if (pvpEnabled == setAs) {
                        toggledByForce = true;
                    } else {
                        pvpEnabled = setAs;

                        if (sendMessage) {
                            Sender sender = new Sender(player);
                            sender.sendMessage(message);
                        }

                        toggledByForce = true;
                    }
                }
            }
        }
    }

    @Getter @Setter
    private static ConcurrentSkipListSet<PVPPlayer> players = new ConcurrentSkipListSet<>();

    public static void registerPlayer(PVPPlayer player) {
        players.add(player);
    }

    public static void unregisterPlayer(String uuid) {
        players.removeIf(player -> player.getUuid().equals(uuid));
    }

    public static Optional<PVPPlayer> getPlayer(String uuid) {
        for (PVPPlayer player : players) {
            if (player.getUuid().equals(uuid)) {
                return Optional.of(player);
            }
        }

        return Optional.empty();
    }

    public static Optional<PVPPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    public static PVPPlayer getOrGetPlayer(String uuid) {
        return getPlayer(uuid).orElseGet(() -> {
            PVPPlayer pvpPlayer = new PVPPlayer(uuid);
            registerPlayer(pvpPlayer);
            return pvpPlayer;
        });
    }

    public static PVPPlayer getOrGetPlayer(Player player) {
        return getOrGetPlayer(player.getUniqueId().toString());
    }
}

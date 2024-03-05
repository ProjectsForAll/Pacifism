package host.plas.pacifism.players;

import host.plas.pacifism.Pacifism;
import io.streamlined.bukkit.commands.Sender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.quaint.objects.Identifiable;
import tv.quaint.storage.documents.SimpleJsonDocument;

import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class PacifismPlayer implements Identifiable {
    @Getter @Setter
    public static class Serializer extends SimpleJsonDocument {
        public Serializer(String uuid) {
            super(uuid + ".json", getPlayerFolder(), false);
        }

        public static File getPlayerFolder() {
            File mainFolder = Pacifism.getInstance().getDataFolder();
            return new File(mainFolder, "players");
        }

        public static boolean folderExists() {
            return getPlayerFolder().exists();
        }

        @Override
        public void onInit() {

        }

        @Override
        public void onSave() {

        }
    }
    @Setter
    private String identifier;

    private boolean pvpEnabled;
    @Setter
    private long playTicks;
    @Setter
    private boolean toggledByForce;
    @Setter
    private boolean hasToggled;
    @Setter
    private Date lastPvpUpdate;

    public void setPvpEnabled(boolean bool) {
        boolean old = pvpEnabled;
        pvpEnabled = bool;

        hasToggled = true;
        if (old != bool) lastPvpUpdate = new Date();
    }

    public PacifismPlayer(String uuid) {
        this.identifier = uuid;
        this.pvpEnabled = false;
        this.playTicks = 0;
        this.toggledByForce = false;
        this.hasToggled = false;
        this.lastPvpUpdate = new Date(0L);

        registerPlayer(this);
    }

    public boolean fileExists() {
        if (! Serializer.folderExists()) return false;
        File file = new File(Serializer.getPlayerFolder(), getIdentifier() + ".json");
        return file.exists();
    }

    public void convertOld() {
        if (! fileExists()) return;

        Serializer serializer = new Serializer(getIdentifier());

        pvpEnabled = serializer.getResource().getBoolean("pvp-enabled");
        playTicks = serializer.getResource().getInt("play-ticks");
        toggledByForce = serializer.getResource().getBoolean("toggled-by-force");
        hasToggled = serializer.getResource().getBoolean("has-toggled");

        serializer.delete();

        save();
    }

    public void save() {
        Pacifism.getDbOperator().savePlayer(this);
    }

    public void togglePVP() {
        setPvpEnabled(! pvpEnabled);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(getIdentifier()));
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public boolean canTogglePvp() {
        return getCooldownMillisLeft() <= 0;
    }

    public long getCooldownMillisLeft() {
        // 900 - 1000 + 100 = 0
        // 950 - 1000 + 100 = 50
        // 1000 - 900 - 100 = 0
        // 1000 - 950 - 100 = -50
        return lastPvpUpdate.getTime() - new Date().getTime() + getCooldownToMillis();
    }

    public double getCooldownSecondsLeft() {
        return getCooldownMillisLeft() / 1000d;
    }

    public static long getCooldownToMillis() {
        return Pacifism.getMainConfig().getPlayerToggleCooldownTicks() * 50;
    }

    public static long getMillisToTicks(long millis) {
        return millis / 50L;
    }

    public void tick() {
        if (! isOnline()) return;
        Player player = getPlayer();

        playTicks ++;

        if (! toggledByForce && ! hasToggled) {
            boolean enabled = Pacifism.getMainConfig().getPlayerForceToggleEnabled();
            if (enabled) {
                boolean setAs = Pacifism.getMainConfig().getPlayerForceToggleSetAs();
                long ticks = Pacifism.getMainConfig().getPlayerForceToggleTicks();
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
    private static ConcurrentSkipListSet<PacifismPlayer> players = new ConcurrentSkipListSet<>();

    public static void registerPlayer(PacifismPlayer player) {
        players.add(player);
    }

    public static void unregisterPlayer(String uuid) {
        players.removeIf(player -> player.getIdentifier().equals(uuid));
    }

    public static Optional<PacifismPlayer> getPlayer(String uuid) {
        for (PacifismPlayer player : players) {
            if (player.getIdentifier().equals(uuid)) {
                return Optional.of(player);
            }
        }

        return Optional.empty();
    }

    public static Optional<PacifismPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId().toString());
    }

    public static PacifismPlayer getOrGetPlayer(String uuid) {
        return getPlayer(uuid).orElseGet(() -> {
            Optional<PacifismPlayer> optional = Pacifism.getDbOperator().loadPlayer(uuid);
            if (optional.isPresent()) return optional.get();

            PacifismPlayer pvpPlayer = new PacifismPlayer(uuid);
            registerPlayer(pvpPlayer);
            return pvpPlayer;
        });
    }

    public static PacifismPlayer getOrGetPlayer(Player player) {
        return getOrGetPlayer(player.getUniqueId().toString());
    }
}

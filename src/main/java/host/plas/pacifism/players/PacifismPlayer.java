package host.plas.pacifism.players;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.bou.commands.Sender;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class PacifismPlayer implements Identifiable {
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

        load();
    }

    public void load() {
        PlayerManager.loadPlayer(this);
    }

    public void unload() {
        PlayerManager.unloadPlayer(getIdentifier());
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
        if (! isOnline()) {
            save();
            unload();
            return;
        }
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

    public PacifismPlayer augment(CompletableFuture<Optional<PacifismPlayer>> future) {
        CompletableFuture.runAsync(() -> {
            Optional<PacifismPlayer> optional = future.join();
            if (optional.isEmpty()) return;

            PacifismPlayer player = optional.get();
            this.pvpEnabled = player.pvpEnabled;
            this.playTicks += player.playTicks;
            this.toggledByForce = player.toggledByForce;
            this.hasToggled = player.hasToggled;
            this.lastPvpUpdate = player.lastPvpUpdate;
        });

        return this;
    }
}

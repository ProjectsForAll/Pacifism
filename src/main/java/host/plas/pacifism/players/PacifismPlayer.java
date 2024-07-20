package host.plas.pacifism.players;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.bou.commands.Sender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.quaint.objects.Identifiable;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    @Setter
    private boolean loadedAtLeastOnce;

    public void setPvpEnabledAs(boolean setAs) {
        boolean old = pvpEnabled;
        this.pvpEnabled = setAs;

        this.hasToggled = true;
        if (! setAs && old != setAs) this.lastPvpUpdate = new Date();

        save();
    }

    public PacifismPlayer(String uuid, boolean pvpEnabled, long playTicks, boolean toggledByForce, boolean hasToggled, Date lastPvpUpdate) {
        this.identifier = uuid;
        this.pvpEnabled = pvpEnabled;
        this.playTicks = playTicks;
        this.toggledByForce = toggledByForce;
        this.hasToggled = hasToggled;
        this.lastPvpUpdate = lastPvpUpdate;

        this.loadedAtLeastOnce = false;
    }

    public PacifismPlayer(String uuid) {
        this(uuid, false, 0, false, false, new Date());
    }

    public void load() {
        PlayerManager.loadPlayer(this);
    }

    public void unload() {
        unload(true);
    }

    public void unload(boolean save) {
        PlayerManager.unloadPlayer(getIdentifier(), save);
    }

    public void save() {
        Pacifism.getDbOperator().savePlayer(this);
    }

    public void togglePVP() {
        setPvpEnabledAs(! pvpEnabled);
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
            try {
                Optional<PacifismPlayer> optional = future.join();
                if (optional.isEmpty()) return;
                PacifismPlayer player = optional.get();

                this.pvpEnabled = player.pvpEnabled;
                this.playTicks += player.playTicks;
                this.toggledByForce = player.toggledByForce;
                this.hasToggled = player.hasToggled;
                this.lastPvpUpdate = player.lastPvpUpdate;

                this.loadedAtLeastOnce = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return this;
    }
}

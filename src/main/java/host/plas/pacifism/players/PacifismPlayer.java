package host.plas.pacifism.players;

import host.plas.bou.firestring.FireStringManager;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.bou.commands.Sender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tv.quaint.objects.Identifiable;

import java.util.*;
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
    private long addedGraceTicks;

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
        this.addedGraceTicks = -1;

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
        if (getCooldownToMillis() == 0) return 0;
        if (lastPvpUpdate == null) return getCooldownToMillis();

        // Get millis between now and last update
        long diff = new Date().getTime() - lastPvpUpdate.getTime();

        // Get the left over time
        long returnVal = getCooldownToMillis() - diff;

        // If the return value is less than 0, return 0
        return returnVal < 0 ? 0 : returnVal;
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

        boolean enabled = Pacifism.getMainConfig().getPlayerForceToggleEnabled();
        boolean setAs = Pacifism.getMainConfig().getPlayerForceToggleSetAs();
        long ticks = Pacifism.getMainConfig().getPlayerForceToggleTicks();
        String message = Pacifism.getMainConfig().getPlayerForceToggleMessage();
        boolean sendMessage = Pacifism.getMainConfig().getPlayerForceToggleSendMessage();

        if (enabled) {
            long tl = ticks - playTicks;

            if (addedGraceTicks > -1) {
                tl = addedGraceTicks;
            }

            boolean commandEnabled = Pacifism.getMainConfig().getPlayerForceToggleCountdownEnabled();
            if (commandEnabled) {
                for (Map.Entry<Long, List<String>> entry : Pacifism.getMainConfig().getPlayerForceToggleCountdownCommands().entrySet()) {
                    long time = entry.getKey();
                    if (tl != time) continue;

                    List<String> commands = entry.getValue();
                    for (String command : commands) {
                        command = command.trim();

                        command = command
                                .replace("%player_name%", player.getName())
                                .replace("%player_uuid%", player.getUniqueId().toString())
                                .replace("%player_ticks%", String.valueOf(playTicks))
                                .replace("%player_ticks_left%", String.valueOf(tl))
                                .replace("%set_as%", String.valueOf(setAs))
                        ;

                        FireStringManager.fire(command);
                    }
                }
            }

            if (tl == 0) {
                if (pvpEnabled != setAs) pvpEnabled = setAs;

                if (sendMessage) {
                    Sender sender = new Sender(player);
                    sender.sendMessage(message);
                }

                toggledByForce = true;
            }
        }

        if (addedGraceTicks > -1) {
            addedGraceTicks --;
        }
        playTicks ++;
    }

    public PacifismPlayer augment(CompletableFuture<Optional<PacifismPlayer>> future) {
        CompletableFuture.runAsync(() -> {
            try {
                Optional<PacifismPlayer> optional = future.join();
                if (optional.isEmpty()) return;
                PacifismPlayer player = optional.get();

                this.pvpEnabled = player.pvpEnabled;
                this.playTicks = player.playTicks;
                this.toggledByForce = player.toggledByForce;
                this.hasToggled = player.hasToggled;
                this.lastPvpUpdate = player.lastPvpUpdate;
                this.addedGraceTicks = player.addedGraceTicks;

                this.loadedAtLeastOnce = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return this;
    }
}

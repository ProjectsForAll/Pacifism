package host.plas.pacifism.placeholders;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.config.MainConfig;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter @Setter
public class PacifismExpansion extends PlaceholderExpansion {
    public PacifismExpansion() {
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pacifism";
    }

    @Override
    public @NotNull String getAuthor() {
        try {
            return Pacifism.getInstance().getDescription().getAuthors().get(0);
        } catch (Exception e) {
            return "Drak";
        }
    }

    @Override
    public @NotNull String getVersion() {
        return Pacifism.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String argsString = params.toLowerCase();
        String[] args = params.split("_");

        if (argsString.startsWith("as_")) {
            if (args.length < 2) return null;
            String playerName = args[1];
            OfflinePlayer target = null;
            if (playerName.contains("-")) {
                try {
                    UUID uuid = UUID.fromString(playerName);
                    target = Bukkit.getOfflinePlayer(uuid);
                } catch (Throwable e) {
                    return null;
                }
            } else {
                target = Bukkit.getOfflinePlayer(playerName);
            }

            if (! target.hasPlayedBefore()) return "";

            return handlePlayerPlaceholders(target, args[0]);
        } else {
            return handlePlayerPlaceholders(player, argsString);
        }
    }

    public String handlePlayerPlaceholders(OfflinePlayer player, String input) {
        PacifismPlayer p = PlayerManager.getOrGetPlayer(player.getUniqueId().toString());
        if (p == null) return "";

        long graceTicks = p.getAddedGraceTicks();
        long graceSeconds = Math.floorDiv(graceTicks, 20);

        MainConfig config = Pacifism.getMainConfig();

        switch (input.toLowerCase()) {
            case "gracetime_left_ticks":
                return config.getPlaceheldGracetimeLeftTicksSimple().replace("%gracetime_left_ticks%", String.valueOf(graceTicks));
            case "gracetime_left_ticks_fancy":
                return config.getPlaceheldGracetimeLeftTicksFancy().replace("%gracetime_left_ticks%", String.valueOf(graceTicks));
            case "gracetime_left_seconds":
                return config.getPlaceheldGracetimeLeftSecondsSimple().replace("%gracetime_left_seconds%", String.valueOf(graceSeconds));
            case "gracetime_left_seconds_fancy":
                return config.getPlaceheldGracetimeLeftSecondsFancy().replace("%gracetime_left_seconds%", String.valueOf(graceSeconds));
            case "status_simple":
                if (p.isPvpEnabled()) {
                    return config.getPlaceheldStatusPvpOnSimple().replace("%status_pvp%", "true");
                } else {
                    return config.getPlaceheldStatusPvpOffSimple().replace("%status_pvp%", "false");
                }
            case "status_fancy":
                if (p.isPvpEnabled()) {
                    return config.getPlaceheldStatusPvpOnFancy().replace("%status_pvp%", "true");
                } else {
                    return config.getPlaceheldStatusPvpOffFancy().replace("%status_pvp%", "false");
                }
            default:
                return null;
        }
    }
}

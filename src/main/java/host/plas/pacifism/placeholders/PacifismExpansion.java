package host.plas.pacifism.placeholders;

import host.plas.bou.compat.papi.expansion.BetterExpansion;
import host.plas.bou.compat.papi.expansion.PlaceholderContext;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.config.MainConfig;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter @Setter
public class PacifismExpansion extends BetterExpansion {
    public PacifismExpansion() {
        super(Pacifism.getInstance(), "pacifism",
                () -> Pacifism.getInstance().getDescription().getAuthors().get(0),
                () -> Pacifism.getInstance().getDescription().getVersion()
        );
    }

    @Override
    public @Nullable String replace(PlaceholderContext context) {
        OfflinePlayer player = context.getPlayer();
        String argsString = context.getRawParams().toLowerCase();

        if (argsString.startsWith("as_")) {
            if (context.getArgCount() < 2) return null;
            String playerName = context.getStringArg(0);
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

            return handlePlayerPlaceholders(target, argsString.replace("as_" + playerName + "_", ""));
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

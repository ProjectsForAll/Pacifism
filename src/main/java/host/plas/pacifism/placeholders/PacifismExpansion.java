package host.plas.pacifism.placeholders;

import com.google.re2j.Matcher;
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
import tv.quaint.utils.MatcherUtils;

import java.util.List;
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

        MainConfig config = Pacifism.getMainConfig();

        long graceTicks = p.getAddedGraceTicks();
        if (config.getPlaceheldGracetimeLeftNegativesReplace()) {
            if (graceTicks < 0) graceTicks = config.getPlaceheldGracetimeLeftNegativesReplaceTo();
        }
        double graceSeconds = graceTicks / 20d;
        double graceMinutes = graceSeconds / 60d;
        double graceHours = graceMinutes / 60d;
        double graceDays = graceHours / 24d;
        double graceWeeks = graceDays / 7d;

        String result = "";

        switch (input.toLowerCase()) {
            case "gracetime_left_ticks":
                result = config.getPlaceheldGracetimeLeftTicksSimple();
                break;
            case "gracetime_left_ticks_fancy":
                result = config.getPlaceheldGracetimeLeftTicksFancy();
                break;
            case "gracetime_left_seconds":
                result = config.getPlaceheldGracetimeLeftSecondsSimple();
                break;
            case "gracetime_left_seconds_fancy":
                result = config.getPlaceheldGracetimeLeftSecondsFancy();
                break;
            case "gracetime_left_minutes":
                result = config.getPlaceheldGracetimeLeftMinutesSimple();
                break;
            case "gracetime_left_minutes_fancy":
                result = config.getPlaceheldGracetimeLeftMinutesFancy();
                break;
            case "gracetime_left_hours":
                result = config.getPlaceheldGracetimeLeftHoursSimple();
                break;
            case "gracetime_left_hours_fancy":
                result = config.getPlaceheldGracetimeLeftHoursFancy();
                break;
            case "gracetime_left_days":
                result = config.getPlaceheldGracetimeLeftDaysSimple();
                break;
            case "gracetime_left_days_fancy":
                result = config.getPlaceheldGracetimeLeftDaysFancy();
                break;
            case "gracetime_left_weeks":
                result = config.getPlaceheldGracetimeLeftWeeksSimple();
                break;
            case "gracetime_left_weeks_fancy":
                result = config.getPlaceheldGracetimeLeftWeeksFancy();
                break;
            case "gracetime_left_combined":
                result = "%gracetime_left_combined%";
                break;
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

        return wrap(player, input, result);
    }

    public String wrap(OfflinePlayer player, String input, String value) {
        PacifismPlayer p = PlayerManager.getOrGetPlayer(player.getUniqueId().toString());
        if (p == null) return "";

        MainConfig config = Pacifism.getMainConfig();

        long graceTicks = p.getAddedGraceTicks();
        if (config.getPlaceheldGracetimeLeftNegativesReplace()) {
            if (graceTicks < 0) graceTicks = config.getPlaceheldGracetimeLeftNegativesReplaceTo();
        }
        double graceSeconds = graceTicks / 20d;
        double graceMinutes = graceSeconds / 60d;
        double graceHours = graceMinutes / 60d;
        double graceDays = graceHours / 24d;
        double graceWeeks = graceDays / 7d;

        String result = value;

        if (value.equals("%gracetime_left_combined%")) {
            String weeks = "";
            String days = "";
            String hours = "";
            String minutes = "";
            String seconds = "";
            String ticks = "";

            String weeksFancy = config.getPlaceheldGracetimeLeftCombinedSectionWeeks();
            String daysFancy = config.getPlaceheldGracetimeLeftCombinedSectionDays();
            String hoursFancy = config.getPlaceheldGracetimeLeftCombinedSectionHours();
            String minutesFancy = config.getPlaceheldGracetimeLeftCombinedSectionMinutes();
            String secondsFancy = config.getPlaceheldGracetimeLeftCombinedSectionSeconds();
            String ticksFancy = config.getPlaceheldGracetimeLeftCombinedSectionTicks();

            boolean hasWeeks = graceWeeks > 0;
            boolean hasDays = graceDays > 0;
            boolean hasHours = graceHours > 0;
            boolean hasMinutes = graceMinutes > 0;
            boolean hasSeconds = graceSeconds > 0;
            boolean hasTicks = graceTicks > 0;
            if (hasWeeks) {
                weeks = weeksFancy.replace("%amount%", getValue(graceWeeks, GetType.FLOORED));
            }
            if (hasDays) {
                double d = graceDays;
                if (hasWeeks) d = d % 7;
                days = daysFancy.replace("%amount%", getValue(d, GetType.FLOORED));
            }
            if (hasHours) {
                double h = graceHours;
                if (hasDays) h = h % 24;
                hours = hoursFancy.replace("%amount%", getValue(h, GetType.FLOORED));
            }
            if (hasMinutes) {
                double m = graceMinutes;
                if (hasHours) m = m % 60;
                minutes = minutesFancy.replace("%amount%", getValue(m, GetType.FLOORED));
            }
            if (hasSeconds) {
                double s = graceSeconds;
                if (hasMinutes) s = s % 60;
                seconds = secondsFancy.replace("%amount%", getValue(s, GetType.FLOORED));
            }
            if (hasTicks) {
                double t = graceTicks;
                if (hasSeconds) t = t % 20;
                ticks = ticksFancy.replace("%amount%", getValue(t, GetType.FLOORED));
            }

            String combined = config.getPlaceheldGracetimeLeftCombinedFancy()
                    .replace("%weeks%", weeks)
                    .replace("%days%", days)
                    .replace("%hours%", hours)
                    .replace("%minutes%", minutes)
                    .replace("%seconds%", seconds)
                    .replace("%ticks%", ticks)
                    .replace("%space%", " ")
                    ;

            if (combined.trim().isBlank()) combined = ticksFancy.replace("%amount%", "0");

            result = combined.trim();

            return result;
        }

        result = parseValues(value, graceTicks, graceSeconds, graceMinutes, graceHours, graceDays, graceWeeks);

        Matcher matcher = MatcherUtils.matcherBuilder("%(.*?):(.*?)%", result);
        List<String[]> groups = MatcherUtils.getGroups(matcher, 2);
        if (groups.isEmpty()) return result;

        for (String[] group : groups) {
            String placeholder = group[0].toLowerCase();
            String number = group[1];
            int decimalPlaces = -1;
            try {
                decimalPlaces = Integer.parseInt(number);
            } catch (Throwable e) {
                continue;
            }

            switch (placeholder) {
                case "gracetime_left_ticks":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceTicks, GetType.TRUNCATED, decimalPlaces));
                    break;
                case "gracetime_left_seconds":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceSeconds, GetType.TRUNCATED, decimalPlaces));
                    break;
                case "gracetime_left_minutes":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceMinutes, GetType.TRUNCATED, decimalPlaces));
                    break;
                case "gracetime_left_hours":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceHours, GetType.TRUNCATED, decimalPlaces));
                    break;
                case "gracetime_left_days":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceDays, GetType.TRUNCATED, decimalPlaces));
                    break;
                case "gracetime_left_weeks":
                    result = result.replace("%" + placeholder + ":" + number + "%", getValue(graceWeeks, GetType.TRUNCATED, decimalPlaces));
                    break;
            }
        }

        return result;
    }

    public String parseValues(OfflinePlayer player, String input) {
        PacifismPlayer p = PlayerManager.getOrGetPlayer(player.getUniqueId().toString());

        MainConfig config = Pacifism.getMainConfig();

        long graceTicks = p.getAddedGraceTicks();
        if (config.getPlaceheldGracetimeLeftNegativesReplace()) {
            if (graceTicks < 0) graceTicks = config.getPlaceheldGracetimeLeftNegativesReplaceTo();
        }
        double graceSeconds = graceTicks / 20d;
        double graceMinutes = graceSeconds / 60d;
        double graceHours = graceMinutes / 60d;
        double graceDays = graceHours / 24d;
        double graceWeeks = graceDays / 7d;

        return parseValues(input, graceTicks, graceSeconds, graceMinutes, graceHours, graceDays, graceWeeks);
    }

    public String parseValues(String input,
                              double graceTicks, double graceSeconds, double graceMinutes,
                              double graceHours, double graceDays, double graceWeeks) {
        String result = input;

        result = result
                .replace("%gracetime_left_ticks%", getValue(graceTicks))
                .replace("%gracetime_left_ticks_full%", getValue(graceTicks, GetType.FULL))
                .replace("%gracetime_left_seconds%", getValue(graceSeconds))
                .replace("%gracetime_left_seconds_full%", getValue(graceSeconds, GetType.FULL))
                .replace("%gracetime_left_minutes%", getValue(graceMinutes))
                .replace("%gracetime_left_minutes_full%", getValue(graceMinutes, GetType.FULL))
                .replace("%gracetime_left_hours%", getValue(graceHours))
                .replace("%gracetime_left_hours_full%", getValue(graceHours, GetType.FULL))
                .replace("%gracetime_left_days%", getValue(graceDays))
                .replace("%gracetime_left_days_full%", getValue(graceDays, GetType.FULL))
                .replace("%gracetime_left_weeks%", getValue(graceWeeks))
                .replace("%gracetime_left_weeks_full%", getValue(graceWeeks, GetType.FULL))
        ;

        return result;
    }

    public enum GetType {
        FLOORED,
        CEILED,
        FULL,
        TRUNCATED,
        ;
    }

    public static String getValue(double d) {
        return getValue(d, GetType.FLOORED);
    }

    public static String getValue(double d, GetType type) {
        return getValue(d, type, null);
    }

    public static String getValue(double d, GetType type, @Nullable Integer decimalPlaces) {
        switch (type) {
            case FLOORED:
                return String.valueOf((long) Math.floor(d));
            case CEILED:
                return String.valueOf(Math.round(Math.ceil(d)));
            case TRUNCATED:
                if (decimalPlaces == null) return String.valueOf(d);
                else {
                    String[] split = String.valueOf(d).split("\\.", 2);
                    if (split.length < 2) return String.valueOf(d);
                    if (split[1].length() < decimalPlaces) return String.valueOf(d);
                    return split[0] + "." + split[1].substring(0, decimalPlaces);
                }
            case FULL:
            default:
                return String.valueOf(d);
        }
    }
}

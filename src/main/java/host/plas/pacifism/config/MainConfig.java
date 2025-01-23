package host.plas.pacifism.config;

import host.plas.bou.sql.ConnectorSet;
import host.plas.bou.sql.DatabaseType;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.players.PacifismWhitelist;
import host.plas.pacifism.utils.ConfigUtils;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", Pacifism.getInstance(), true);
    }

    @Override
    public void init() {
        getPlayerForceToggleTicks();
        getPlayerForceToggleSetAs();
        getPlayerForceToggleEnabled();
        getPlayerForceToggleMessage();
        getPlayerForceToggleSendMessage();

        getPlayerForceToggleCountdownEnabled();
        getPlayerForceToggleCountdownCommands();

        getPlayerToggleCooldownEnabled();
        getPlayerToggleCooldownTicks();

        getDatabaseHost();
        getDatabasePort();
        getDatabaseUsername();
        getDatabasePassword();
        getDatabaseTablePrefix();
        getDatabaseName();
        getDatabaseType();
        getSqliteFileName();

        getMaterialWhitelist();
        getEntityWhitelist();

        getPlaceheldGracetimeLeftNegativesReplace();
        getPlaceheldGracetimeLeftNegativesReplaceTo();

        getPlaceheldGracetimeLeftTicksSimple();
        getPlaceheldGracetimeLeftTicksFancy();
        getPlaceheldGracetimeLeftSecondsSimple();
        getPlaceheldGracetimeLeftSecondsFancy();
        getPlaceheldGracetimeLeftMinutesSimple();
        getPlaceheldGracetimeLeftMinutesFancy();
        getPlaceheldGracetimeLeftHoursSimple();
        getPlaceheldGracetimeLeftHoursFancy();
        getPlaceheldGracetimeLeftDaysSimple();
        getPlaceheldGracetimeLeftDaysFancy();
        getPlaceheldGracetimeLeftWeeksSimple();
        getPlaceheldGracetimeLeftWeeksFancy();

        getPlaceheldGracetimeLeftCombinedFancy();
        getPlaceheldGracetimeLeftCombinedSectionOnlyNonZero();
        getPlaceheldGracetimeLeftCombinedSectionWeeks();
        getPlaceheldGracetimeLeftCombinedSectionDays();
        getPlaceheldGracetimeLeftCombinedSectionHours();
        getPlaceheldGracetimeLeftCombinedSectionMinutes();
        getPlaceheldGracetimeLeftCombinedSectionSeconds();
        getPlaceheldGracetimeLeftCombinedSectionTicks();

        getPlaceheldStatusPvpOffSimple();
        getPlaceheldStatusPvpOffFancy();
        getPlaceheldStatusPvpOnSimple();
        getPlaceheldStatusPvpOnFancy();
    }

    public long getPlayerForceToggleTicks() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.after", 20 * 60 * 15); // 15 minutes
    }

    public boolean getPlayerForceToggleSetAs() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.set-as", true); // Default to false
    }

    public boolean getPlayerForceToggleEnabled() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.enabled", true); // Is enabled by default
    }

    public String getPlayerForceToggleMessage() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.message",
                "&7&oYou seem fit to &c&lfight&8&o! &7&oWe have enabled your &c&lPVP&8&o!"); // The message.
    }

    public boolean getPlayerForceToggleSendMessage() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.send-message", true); // The message.
    }

    public boolean getPlayerForceToggleCountdownEnabled() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.countdown.enabled", true);
    }

    public ConcurrentSkipListMap<Long, List<String>> getPlayerForceToggleCountdownCommands() {
        reloadResource();

        ConcurrentSkipListMap<Long, List<String>> messages = new ConcurrentSkipListMap<>();

        for (String key : singleLayerKeySet("player.force-toggle.countdown.commands")) {
            long time = Long.parseLong(key);
            List<String> message = getOrSetDefault("player.force-toggle.countdown.commands." + key, new ArrayList<>());
            messages.put(time, message);
        }

        return messages;
    }

    public boolean getPlayerToggleCooldownEnabled() {
        reloadResource();

        return getOrSetDefault("player.toggle.cool-down.enabled", true);
    }

    public long getPlayerToggleCooldownTicks() {
        reloadResource();

        return getOrSetDefault("player.toggle.cool-down.ticks", 600); // 30 seconds
    }

    public String getDatabaseHost() {
        reloadResource();

        return getOrSetDefault("database.host", "localhost");
    }

    public int getDatabasePort() {
        reloadResource();

        return getOrSetDefault("database.port", 3306);
    }

    public String getDatabaseUsername() {
        reloadResource();

        return getOrSetDefault("database.username", "root");
    }

    public String getDatabasePassword() {
        reloadResource();

        return getOrSetDefault("database.password", "password");
    }

    public String getDatabaseTablePrefix() {
        reloadResource();

        return getOrSetDefault("database.table-prefix", "pacifism_");
    }

    public String getDatabaseName() {
        reloadResource();

        return getOrSetDefault("database.database", "pacifism");
    }

    public DatabaseType getDatabaseType() {
        reloadResource();

        return DatabaseType.valueOf(getOrSetDefault("database.type", DatabaseType.SQLITE.name()).toUpperCase());
    }

    public String getSqliteFileName() {
        reloadResource();

        return getOrSetDefault("database.sqlite-file-name", "pacifism.db");
    }

    public ConnectorSet getConnectorSet() {
        return new ConnectorSet(
                getDatabaseType(),
                getDatabaseHost(),
                getDatabasePort(),
                getDatabaseName(),
                getDatabaseUsername(),
                getDatabasePassword(),
                getDatabaseTablePrefix(),
                getSqliteFileName()
        );
    }

    public PacifismWhitelist getMaterialWhitelist() {
        return ConfigUtils.getPWhitelist(this, "explosions.materials", "explosions.materials.", "list", "is-blacklist");
    }

    public PacifismWhitelist getEntityWhitelist() {
        return ConfigUtils.getPWhitelist(this, "explosions.entities", "explosions.entities.", "list", "is-blacklist");
    }

    public boolean getPlaceheldGracetimeLeftNegativesReplace() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.negatives.replace", true);
    }

    public long getPlaceheldGracetimeLeftNegativesReplaceTo() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.negatives.replace-to", 0L);
    }

    public String getPlaceheldGracetimeLeftTicksSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.ticks.simple", "%gracetime_left_ticks%");
    }

    public String getPlaceheldGracetimeLeftTicksFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.ticks.fancy", "&a%gracetime_left_ticks% &fticks");
    }

    public String getPlaceheldGracetimeLeftSecondsSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.seconds.simple", "%gracetime_left_seconds%");
    }

    public String getPlaceheldGracetimeLeftSecondsFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.seconds.fancy", "&a%gracetime_left_seconds% &fseconds");
    }

    public String getPlaceheldGracetimeLeftMinutesSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.minutes.simple", "%gracetime_left_minutes%");
    }

    public String getPlaceheldGracetimeLeftMinutesFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.minutes.fancy", "&a%gracetime_left_minutes% &fminutes");
    }

    public String getPlaceheldGracetimeLeftHoursSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.hours.simple", "%gracetime_left_hours%");
    }

    public String getPlaceheldGracetimeLeftHoursFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.hours.fancy", "&a%gracetime_left_hours% &fhours");
    }

    public String getPlaceheldGracetimeLeftDaysSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.days.simple", "%gracetime_left_days%");
    }

    public String getPlaceheldGracetimeLeftDaysFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.days.fancy", "&a%gracetime_left_days% &fdays");
    }

    public String getPlaceheldGracetimeLeftWeeksSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.weeks.simple", "%gracetime_left_weeks%");
    }

    public String getPlaceheldGracetimeLeftWeeksFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.weeks.fancy", "&a%gracetime_left_weeks% &fweeks");
    }

    public String getPlaceheldGracetimeLeftCombinedFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.fancy", "%weeks% %days% %hours% %minutes% %seconds% %ticks%");
    }

    public boolean getPlaceheldGracetimeLeftCombinedSectionOnlyNonZero() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.only-show-if-not-zero", true);
    }

    public String getPlaceheldGracetimeLeftCombinedSectionWeeks() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.weeks", "&a%amount% &fweeks");
    }

    public String getPlaceheldGracetimeLeftCombinedSectionDays() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.days", "&a%amount% &fdays");
    }

    public String getPlaceheldGracetimeLeftCombinedSectionHours() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.hours", "&a%amount% &fhours");
    }

    public String getPlaceheldGracetimeLeftCombinedSectionMinutes() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.minutes", "&a%amount% &fminutes");
    }

    public String getPlaceheldGracetimeLeftCombinedSectionSeconds() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.seconds", "&a%amount% &fseconds");
    }

    public String getPlaceheldGracetimeLeftCombinedSectionTicks() {
        reloadResource();

        return getOrSetDefault("placeholders.gracetime.left.combined.sections.ticks", "&a%amount% &fticks");
    }

    public String getPlaceheldStatusPvpOffSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.status.pvp-off.simple", "false");
    }

    public String getPlaceheldStatusPvpOffFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.status.pvp-off.fancy", "&c&lOFF");
    }

    public String getPlaceheldStatusPvpOnSimple() {
        reloadResource();

        return getOrSetDefault("placeholders.status.pvp-on.simple", "true");
    }

    public String getPlaceheldStatusPvpOnFancy() {
        reloadResource();

        return getOrSetDefault("placeholders.status.pvp-on.fancy", "&c&lON");
    }
}

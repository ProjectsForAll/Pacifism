package host.plas.pacifism.config;

import host.plas.bou.sql.ConnectorSet;
import host.plas.bou.sql.DatabaseType;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.players.PacifismWhitelist;
import host.plas.pacifism.utils.ConfigUtils;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

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
}

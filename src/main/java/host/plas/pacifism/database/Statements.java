package host.plas.pacifism.database;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Statements {
    @Getter
    public enum MySQL {
        CREATE_DATABASE("CREATE DATABASE IF NOT EXISTS `%database%`;"),
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table_prefix%players` (" +
                "Uuid VARCHAR(36) PRIMARY KEY," +
                "PvpEnabled BOOLEAN," +
                "PlayTicks INT," +
                "ForceToggled BOOLEAN," +
                "HasToggled BOOLEAN," +
                "LastUpdate INT" +
                ");"),
        PUSH_PLAYER("INSERT INTO `%table_prefix%players` (" +
                "Uuid, PvpEnabled, PlayTicks, ForceToggled, HasToggled, LastUpdate" +
                ") VALUES (" +
                "'%uuid%', %pvp_enabled%, %play_ticks%, %force_toggle%, %has_toggled%, %last_update%" +
                ") ON DUPLICATE KEY UPDATE " +
                "PvpEnabled = %pvp_enabled%, PlayTicks = %play_ticks%, ForceToggled = %force_toggle%, HasToggled = %has_toggled%, LastUpdate = %last_update%" +
                ";"),
        PULL_PLAYER("SELECT * FROM `%table_prefix%players` WHERE Uuid = '%uuid%';"),
        ;

        private final String statement;

        MySQL(String statement) {
            this.statement = statement;
        }
    }

    @Getter
    public enum SQLite {
        CREATE_DATABASE(""),
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table_prefix%players` (" +
                "Uuid TEXT PRIMARY KEY," +
                "PvpEnabled BOOLEAN," +
                "PlayTicks INT," +
                "ForceToggled BOOLEAN," +
                "HasToggled BOOLEAN," +
                "LastUpdate INT" +
                ");"),
        PUSH_PLAYER("INSERT OR REPLACE INTO `%table_prefix%players` (" +
                "Uuid, PvpEnabled, PlayTicks, ForceToggled, HasToggled, LastUpdate" +
                ") VALUES (" +
                "'%uuid%', %pvp_enabled%, %play_ticks%, %force_toggle%, %has_toggled%, %last_update%" +
                ");"),
        PULL_PLAYER("SELECT * FROM `%table_prefix%players` WHERE Uuid = '%uuid%';"),
        ;

        private final String statement;

        SQLite(String statement) {
            this.statement = statement;
        }
    }

    public enum StatementType {
        CREATE_DATABASE,
        CREATE_TABLE,
        PUSH_PLAYER,
        PULL_PLAYER,
        ;
    }

    public static String getStatement(StatementType type, ConnectorSet connectorSet) {
        switch (connectorSet.getType()) {
            case MYSQL:
                return MySQL.valueOf(type.name()).getStatement()
                        .replace("%database%", connectorSet.getDatabase())
                        .replace("%table_prefix%", connectorSet.getTablePrefix());
            case SQLITE:
                return SQLite.valueOf(type.name()).getStatement()
                        .replace("%table_prefix%", connectorSet.getTablePrefix())
                        .replace("%table_prefix%", connectorSet.getTablePrefix());
            default:
                return "";
        }
    }
}

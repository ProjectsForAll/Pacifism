package host.plas.pacifism.database;

import host.plas.bou.sql.ConnectorSet;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Statements {
    @Getter
    public enum Alter {
        V_1_10_0("ALTER TABLE `%table_prefix%players` ADD COLUMN AddedGraceTime BIGINT;"),
        ;

        private final String statement;

        Alter(String statement) {
            this.statement = statement;
        }
    }

    @Getter
    public enum MySQL {
        CREATE_DATABASE("CREATE DATABASE IF NOT EXISTS `%database%`;"),
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table_prefix%players` ( " +
                "Uuid VARCHAR(36) PRIMARY KEY, " +
                "PvpEnabled BOOLEAN, " +
                "PlayTicks BIGINT, " +
                "ForceToggled BOOLEAN, " +
                "HasToggled BOOLEAN, " +
                "LastUpdate BIGINT, " +
                "AddedGraceTime BIGINT " +
                ");"),
        PUSH_PLAYER("INSERT INTO `%table_prefix%players` ( " +
                "Uuid, PvpEnabled, PlayTicks, ForceToggled, HasToggled, LastUpdate, AddedGraceTime " +
                ") VALUES ( " +
                "?, ?, ?, ?, ?, ?, ? " +
                ") ON DUPLICATE KEY UPDATE " +
                "PvpEnabled = ?, PlayTicks = ?, ForceToggled = ?, HasToggled = ?, LastUpdate = ?, AddedGraceTime = ?" +
                ";"),
        PULL_PLAYER("SELECT * FROM `%table_prefix%players` WHERE Uuid = ?;"),
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
                "Uuid TEXT PRIMARY KEY, " +
                "PvpEnabled BOOLEAN, " +
                "PlayTicks BIGINT, " +
                "ForceToggled BOOLEAN, " +
                "HasToggled BOOLEAN, " +
                "LastUpdate BIGINT, " +
                "AddedGraceTime BIGINT " +
                ");"),
        PUSH_PLAYER("INSERT OR REPLACE INTO `%table_prefix%players` ( " +
                "Uuid, PvpEnabled, PlayTicks, ForceToggled, HasToggled, LastUpdate, AddedGraceTime " +
                ") VALUES ( " +
                "?, ?, ?, ?, ?, ?, ? " +
                ");"),
        PULL_PLAYER("SELECT * FROM `%table_prefix%players` WHERE Uuid = ?;"),
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
        ALTER_TABLE,
        ;
    }

    public static String getStatement(StatementType type, ConnectorSet connectorSet) {
        if (type == StatementType.ALTER_TABLE) {
            return Alter.V_1_10_0.getStatement()
                    .replace("%table_prefix%", connectorSet.getTablePrefix());
        }

        switch (connectorSet.getType()) {
            case MYSQL:
                return MySQL.valueOf(type.name()).getStatement()
                        .replace("%database%", connectorSet.getDatabase())
                        .replace("%table_prefix%", connectorSet.getTablePrefix());
            case SQLITE:
                return SQLite.valueOf(type.name()).getStatement()
                        .replace("%table_prefix%", connectorSet.getTablePrefix());
            default:
                return "";
        }
    }
}

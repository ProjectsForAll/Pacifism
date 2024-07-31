package host.plas.pacifism.database;

import host.plas.bou.sql.ConnectorSet;
import host.plas.bou.sql.DBOperator;
import host.plas.bou.sql.DatabaseType;
import host.plas.pacifism.Pacifism;
import host.plas.pacifism.players.PacifismPlayer;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class PacifismDBOperator extends DBOperator {
    public PacifismDBOperator(ConnectorSet set) {
        super(set, Pacifism.getInstance());
    }

    @Override
    public void ensureDatabase() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_DATABASE, this.getConnectorSet());
        if (s1 == null) return;
        if (s1.isBlank() || s1.isEmpty()) return;

        this.execute(s1, stmt -> {});
    }

    @Override
    public void ensureTables() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_TABLE, this.getConnectorSet());
        if (s1 == null) return;
        if (s1.isBlank() || s1.isEmpty()) return;

        this.execute(s1, stmt -> {});
    }

    @Override
    public void ensureUsable() {
        this.ensureFile();
        this.ensureDatabase();
        this.ensureTables();
    }

    public void savePlayer(PacifismPlayer player) {
        savePlayer(player, true);
    }

    public void savePlayer(PacifismPlayer player, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> savePlayerAsync(player).join());
        } else {
            savePlayerAsync(player).join();
        }
    }

    public CompletableFuture<Boolean> savePlayerAsync(PacifismPlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PUSH_PLAYER, this.getConnectorSet());
            if (s1 == null) return false;
            if (s1.isBlank() || s1.isEmpty()) return false;

            this.execute(s1, stmt -> {
                try {
                    stmt.setString(1, player.getIdentifier());
                    stmt.setBoolean(2, player.isPvpEnabled());
                    stmt.setLong(3, player.getPlayTicks());
                    stmt.setBoolean(4, player.isToggledByForce());
                    stmt.setBoolean(5, player.isHasToggled());
                    stmt.setLong(6, player.getLastPvpUpdate().getTime());

                    if (this.getType() == DatabaseType.MYSQL) {
                        stmt.setBoolean(7, player.isPvpEnabled());
                        stmt.setLong(8, player.getPlayTicks());
                        stmt.setBoolean(9, player.isToggledByForce());
                        stmt.setBoolean(10, player.isHasToggled());
                        stmt.setLong(11, player.getLastPvpUpdate().getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return true;
        });
    }

    public CompletableFuture<Optional<PacifismPlayer>> loadPlayer(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PULL_PLAYER, this.getConnectorSet());
            if (s1.isBlank() || s1.isEmpty()) return Optional.empty();

            AtomicReference<Optional<PacifismPlayer>> atomicReference = new AtomicReference<>(Optional.empty());
            this.executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, set -> {
                try {
                    if (set.next()) {
                        try {
                            PacifismPlayer player = new PacifismPlayer(uuid);

                            boolean pvpEnabled = set.getBoolean("PvpEnabled");
                            long playTicks = set.getLong("PlayTicks");
                            boolean forceToggle = set.getBoolean("ForceToggled");
                            boolean hasToggled = set.getBoolean("HasToggled");
                            long lastUpdate = set.getLong("LastUpdate");

                            player.setPvpEnabledAs(pvpEnabled);
                            player.setPlayTicks(playTicks);
                            player.setToggledByForce(forceToggle);
                            player.setHasToggled(hasToggled);
                            player.setLastPvpUpdate(new Date(lastUpdate));

                            atomicReference.set(Optional.of(player));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return atomicReference.get();
        });
    }
}

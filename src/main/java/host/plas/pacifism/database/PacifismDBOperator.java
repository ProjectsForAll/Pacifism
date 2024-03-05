package host.plas.pacifism.database;

import host.plas.pacifism.players.PacifismPlayer;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class PacifismDBOperator extends DBOperator {
    public PacifismDBOperator(ConnectorSet set) {
        super(set, "Pacifism");
    }

    public void ensureDatabase() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_DATABASE, this.getConnectorSet());
        if (s1 == null) return;
        if (s1.isBlank() || s1.isEmpty()) return;

        this.execute(s1);
    }

    public void ensureTable() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_TABLE, this.getConnectorSet());
        if (s1 == null) return;
        if (s1.isBlank() || s1.isEmpty()) return;

        this.execute(s1);
    }

    public void ensureUsable() {
        this.ensureFile();
        this.ensureDatabase();
        this.ensureTable();
    }

    public void savePlayer(PacifismPlayer player) {
        ensureUsable();

        String s1 = Statements.getStatement(Statements.StatementType.PUSH_PLAYER, this.getConnectorSet());
        if (s1 == null) return;
        if (s1.isBlank() || s1.isEmpty()) return;

        s1 = s1.replace("%uuid%", player.getIdentifier());
        s1 = s1.replace("%pvp_enabled%", String.valueOf(player.isPvpEnabled()));
        s1 = s1.replace("%play_ticks%", String.valueOf(player.getPlayTicks()));
        s1 = s1.replace("%force_toggle%", String.valueOf(player.isToggledByForce()));
        s1 = s1.replace("%has_toggled%", String.valueOf(player.isHasToggled()));
        s1 = s1.replace("%last_update%", String.valueOf(player.getLastPvpUpdate().getTime()));

        this.execute(s1);
    }

    public Optional<PacifismPlayer> loadPlayer(String uuid) {
        ensureUsable();

        String s1 = Statements.getStatement(Statements.StatementType.PULL_PLAYER, this.getConnectorSet());
        if (s1 == null) return Optional.empty();
        if (s1.isBlank() || s1.isEmpty()) return Optional.empty();

        s1 = s1.replace("%uuid%", uuid);

        AtomicReference<Optional<PacifismPlayer>> atomicReference = new AtomicReference<>(Optional.empty());
        this.executeQuery(s1, set -> {
            if (set == null) {
                atomicReference.set(Optional.empty());
                return;
            }

            try {
                if (set.next()) {
                    PacifismPlayer player = new PacifismPlayer(uuid);
                    player.setPvpEnabled(set.getBoolean("PvpEnabled"));
                    player.setPlayTicks(set.getInt("PlayTicks"));
                    player.setToggledByForce(set.getBoolean("ForceToggled"));
                    player.setHasToggled(set.getBoolean("HasToggled"));
                    player.setLastPvpUpdate(new Date(set.getLong("LastUpdate")));

                    atomicReference.set(Optional.of(player));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            atomicReference.set(Optional.empty());
        });

        return atomicReference.get();
    }
}

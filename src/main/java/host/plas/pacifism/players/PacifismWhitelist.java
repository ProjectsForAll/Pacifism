package host.plas.pacifism.players;

import host.plas.bou.configs.bits.ConfigurableWhitelist;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.utils.WorldUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@Getter @Setter
public class PacifismWhitelist extends ConfigurableWhitelist<String> {
    private double radius;

    public PacifismWhitelist(String identifier) {
        super(identifier);

        this.radius = 0.0;
    }

    public boolean isInRadius(Location from, Location to) {
        return from.distance(to) <= getRadius();
    }

    public boolean withInRadius(Predicate<Entity> predicate, Location from) {
        return from.getWorld().getNearbyEntities(from, getRadius(), getRadius(), getRadius()).stream().anyMatch(predicate);
    }

    public boolean isPacifistInRadius(Location from) {
        return withInRadius(e -> {
            if (e instanceof Player) {
                PacifismPlayer player = PlayerManager.getOrGetPlayer((Player) e);
                return ! player.isPvpEnabled();
            }
            return false;
        }, from);
    }

    public boolean canPacify(Location from, String thing) {
        return isPacifistInRadius(from) && contains(thing) && WorldUtils.canWorldPacify(from);
    }
}

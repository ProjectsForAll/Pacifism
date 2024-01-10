package host.plas.tpvp.events;

import host.plas.tpvp.players.PVPPlayer;
import io.streamlined.bukkit.commands.Sender;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MainListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (! (damager instanceof Player)) return;
        if (! (damaged instanceof Player)) return;

        Player damagerPlayer = (Player) damager;
        Player damagedPlayer = (Player) damaged;

        PVPPlayer damagerPVPPlayer = PVPPlayer.getOrGetPlayer(damagerPlayer);
        PVPPlayer damagedPVPPlayer = PVPPlayer.getOrGetPlayer(damagedPlayer);

        if (! damagerPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cYou have PVP disabled! You cannot hurt other players.");
            return;
        }

        if (! damagedPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cThat player has PVP disabled! You cannot hurt them.");
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamagePlayerProjectile(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (! (damager instanceof Projectile)) return;
        if (! (damaged instanceof Player)) return;

        Projectile damagerProjectile = (Projectile) damager;
        Player damagedPlayer = (Player) damaged;

        ProjectileSource source = damagerProjectile.getShooter();
        if (! (source instanceof Player)) return;

        Player damagerPlayer = (Player) source;

        PVPPlayer damagerPVPPlayer = PVPPlayer.getOrGetPlayer(damagerPlayer);
        PVPPlayer damagedPVPPlayer = PVPPlayer.getOrGetPlayer(damagedPlayer);

        if (! damagerPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cYou have PVP disabled! You cannot hurt other players.");
            return;
        }

        if (! damagedPVPPlayer.isPvpEnabled()) {
            event.setCancelled(true);

            Sender damagerSender = new Sender(damagerPlayer);
            damagerSender.sendMessage("&cThat player has PVP disabled! You cannot hurt them.");
            return;
        }
    }

//    @EventHandler(ignoreCancelled = true)
//    public void onPlayerDamagePlayerExplode(EntityDamageEvent event) {
//        Entity damaged = event.getEntity();
//        EntityDamageEvent.DamageCause cause = event.getCause();
//
//        if (! (damaged instanceof Player)) return;
//        if (cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
//    }
}

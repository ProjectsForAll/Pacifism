package host.plas.pacifism.events;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.config.WorldConfig;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import host.plas.bou.commands.Sender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MainListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();

        if (! (damager instanceof Player)) return;
        if (! (damaged instanceof Player)) return;

        WorldConfig worldConfig = Pacifism.getWorldConfig();
        if (! worldConfig.canCheckInWorld(damaged.getWorld())) return;

        Player damagerPlayer = (Player) damager;
        Player damagedPlayer = (Player) damaged;

        PacifismPlayer damagerPVPPlayer = PlayerManager.getOrGetPlayer(damagerPlayer);
        PacifismPlayer damagedPVPPlayer = PlayerManager.getOrGetPlayer(damagedPlayer);

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

        WorldConfig worldConfig = Pacifism.getWorldConfig();
        if (! worldConfig.canCheckInWorld(damaged.getWorld())) return;

        PacifismPlayer damagerPVPPlayer = PlayerManager.getOrGetPlayer(damagerPlayer);
        PacifismPlayer damagedPVPPlayer = PlayerManager.getOrGetPlayer(damagedPlayer);

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

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        PacifismPlayer pvpPlayer = PlayerManager.getOrGetPlayer(player);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PacifismPlayer pvpPlayer = PlayerManager.getOrGetPlayer(player);
        pvpPlayer.unload();
    }
}

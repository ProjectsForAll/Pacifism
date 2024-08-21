package host.plas.pacifism.events;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.managers.PlayerManager;
import host.plas.pacifism.players.PacifismPlayer;
import host.plas.pacifism.players.PacifismWhitelist;
import host.plas.pacifism.utils.WorldUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
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

        if (! WorldUtils.canWorldPacify(damaged.getWorld())) return;

        Player damagerPlayer = (Player) damager;
        Player damagedPlayer = (Player) damaged;

        if (WorldUtils.check(damagerPlayer, damagedPlayer, event)) return;

        // do something else
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

        if (! WorldUtils.canWorldPacify(damaged.getWorld())) return;

        if (WorldUtils.check(damagerPlayer, damagedPlayer, event)) return;

        // do something else
    }

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

    @EventHandler
    public void onEntityExplode(ExplosionPrimeEvent event) {
        if (event.isCancelled()) return;

        Entity entity = event.getEntity();

        PacifismWhitelist whitelist = Pacifism.getMainConfig().getEntityWhitelist();
        if (whitelist.canPacify(entity.getLocation(), entity.getType().name())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();

        PacifismWhitelist whitelist = Pacifism.getMainConfig().getMaterialWhitelist();
        if (whitelist.canPacify(block.getLocation(), block.getType().name())) {
            event.setCancelled(true);
        }
    }
}

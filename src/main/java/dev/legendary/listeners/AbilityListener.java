package dev.legendary.listeners;

import dev.legendary.LegendaryPlugin;
import dev.legendary.items.LegendaryItems;
import dev.legendary.managers.CooldownManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class AbilityListener implements Listener {

    private final LegendaryPlugin plugin;
    private final CooldownManager cd;

    // Soulreaper: tracks if next hit is empowered
    private final Set<UUID> soulRendActive = new HashSet<>();
    // Stormbreaker: tracks players with Storm Aura active
    private final Set<UUID> stormAuraActive = new HashSet<>();

    public AbilityListener(LegendaryPlugin plugin) {
        this.plugin = plugin;
        this.cd = plugin.getCooldownManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only main hand, right-click actions
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        String id = LegendaryItems.getLegendaryId(item);
        if (id == null) return;

        event.setCancelled(true); // prevent block interactions / eating etc.

        boolean shift = player.isSneaking();

        switch (id) {
            case "VOIDBANE"     -> { if (shift) darkShroud(player); else voidCollapse(player); }
            case "EMBERHEART"   -> { if (shift) phoenixStep(player); else magmaBurst(player); }
            case "GLACIUS"      -> { if (shift) blizzard(player); else glacialPrison(player); }
            case "STORMBREAKER" -> { if (shift) stormAura(player); else thunderDash(player); }
            case "SOULREAPER"   -> { if (shift) reapersGaze(player); else soulRend(player); }
        }
    }

    // =========================================================================
    // VOIDBANE
    // =========================================================================

    /** RMB — Void Collapse: pull all enemies within 6 blocks and deal 6 damage */
    private void voidCollapse(Player player) {
        String key = "voidbane_collapse";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Void Collapse: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.DARK_PURPLE));
            return;
        }
        cd.setCooldown(player, key, 12);

        Location center = player.getLocation();
        center.getWorld().spawnParticle(Particle.PORTAL, center, 80, 2, 2, 2, 0.3);
        center.getWorld().playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);

        for (Entity e : player.getNearbyEntities(6, 6, 6)) {
            if (!(e instanceof LivingEntity le) || e == player) continue;
            Vector pull = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(1.2);
            e.setVelocity(pull);
            le.damage(6.0, player);
        }
        player.sendActionBar(Component.text("◈ Void Collapse!", NamedTextColor.DARK_PURPLE));
    }

    /** Shift+RMB — Dark Shroud: Invisibility + Speed II for 5 seconds */
    private void darkShroud(Player player) {
        String key = "voidbane_shroud";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Dark Shroud: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.DARK_PURPLE));
            return;
        }
        cd.setCooldown(player, key, 20);

        int ticks = 100; // 5 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 0, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1, false, true, true));
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 30, 0.3, 0.3, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.8f, 1.5f);
        player.sendActionBar(Component.text("◈ Dark Shroud activated!", NamedTextColor.DARK_PURPLE));
    }

    // =========================================================================
    // EMBERHEART
    // =========================================================================

    /** RMB — Magma Burst: deal true fire damage to all in 4 blocks (bypasses fire res) */
    private void magmaBurst(Player player) {
        String key = "emberheart_burst";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Magma Burst: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.RED));
            return;
        }
        cd.setCooldown(player, key, 10);

        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 60, 1.5, 1, 1.5, 0.15);
        loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 0.7f);

        for (Entity e : player.getNearbyEntities(4, 4, 4)) {
            if (!(e instanceof LivingEntity le) || e == player) continue;
            // Tag damage + true HP removal to bypass fire resistance
            le.damage(4.0, player);
            applyTrueFireDamage(le, 6.0);
        }
        player.sendActionBar(Component.text("◈ Magma Burst!", NamedTextColor.RED));
    }

    /** Shift+RMB — Phoenix Step: dash 8 blocks forward, leave fire trail */
    private void phoenixStep(Player player) {
        String key = "emberheart_step";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Phoenix Step: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.RED));
            return;
        }
        cd.setCooldown(player, key, 15);

        Vector dir = player.getLocation().getDirection().normalize();
        Location start = player.getLocation().clone();

        // Teleport forward 8 blocks in steps, placing fire along the path
        new BukkitRunnable() {
            int step = 0;
            final int maxSteps = 8;

            @Override
            public void run() {
                if (step >= maxSteps) {
                    cancel();
                    return;
                }
                Location next = start.clone().add(dir.clone().multiply(step));
                next.getWorld().spawnParticle(Particle.FLAME, next, 8, 0.2, 0.2, 0.2, 0.05);
                // Place fire on ground block
                Location ground = next.clone();
                ground.setY(ground.getBlockY());
                if (ground.getBlock().isPassable()) {
                    ground.getBlock().setType(Material.FIRE);
                }
                step++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Actually teleport the player to endpoint
        Location dest = start.clone().add(dir.multiply(8));
        dest.setPitch(player.getLocation().getPitch());
        dest.setYaw(player.getLocation().getYaw());
        player.teleport(dest);
        player.getWorld().playSound(dest, Sound.ENTITY_BLAZE_SHOOT, 1f, 1.3f);
        player.sendActionBar(Component.text("◈ Phoenix Step!", NamedTextColor.RED));
    }

    /** Deals damage directly to HP — bypasses Fire Resistance potion effect */
    private void applyTrueFireDamage(LivingEntity entity, double amount) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!entity.isDead()) {
                double newHp = Math.max(0, entity.getHealth() - amount);
                entity.setHealth(newHp);
                entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,
                        entity.getLocation().add(0, 1, 0), 12, 0.3, 0.4, 0.3, 0.05);
            }
        }, 1L);
    }

    // =========================================================================
    // GLACIUS
    // =========================================================================

    /** RMB — Glacial Prison: Slowness IV + Mining Fatigue on looked-at target */
    private void glacialPrison(Player player) {
        String key = "glacius_prison";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Glacial Prison: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.AQUA));
            return;
        }

        // Find the nearest entity the player is looking at within 10 blocks
        LivingEntity target = getTargetEntity(player, 10);
        if (target == null) {
            player.sendActionBar(Component.text("No target in range.", NamedTextColor.GRAY));
            return;
        }

        cd.setCooldown(player, key, 12);
        int ticks = 80; // 4 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 3, false, true, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, ticks, 2, false, true, true));
        target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
        player.sendActionBar(Component.text("◈ Glacial Prison!", NamedTextColor.AQUA));
    }

    /** Shift+RMB — Blizzard: AoE Slowness III + Blindness for 3 seconds */
    private void blizzard(Player player) {
        String key = "glacius_blizzard";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Blizzard: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.AQUA));
            return;
        }
        cd.setCooldown(player, key, 18);

        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 120, 3, 2, 3, 0.2);
        loc.getWorld().playSound(loc, Sound.WEATHER_RAIN, 1f, 1.5f);

        int ticks = 60; // 3 seconds
        for (Entity e : player.getNearbyEntities(6, 6, 6)) {
            if (!(e instanceof LivingEntity le) || e == player) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 2, false, true, true));
            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ticks, 0, false, true, true));
        }
        player.sendActionBar(Component.text("◈ Blizzard!", NamedTextColor.AQUA));
    }

    // =========================================================================
    // STORMBREAKER
    // =========================================================================

    /** RMB — Thunder Dash: dash forward, lightning at start position */
    private void thunderDash(Player player) {
        String key = "storm_dash";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Thunder Dash: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.YELLOW));
            return;
        }
        cd.setCooldown(player, key, 12);

        Location origin = player.getLocation().clone();
        Vector dir = player.getLocation().getDirection().normalize().multiply(10);
        player.setVelocity(dir);

        // Strike lightning at origin after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            origin.getWorld().strikeLightning(origin);
            origin.getWorld().playSound(origin, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
        }, 2L);

        player.sendActionBar(Component.text("◈ Thunder Dash!", NamedTextColor.YELLOW));
    }

    /** Shift+RMB — Storm Aura: 6 seconds of random lightning strikes on enemies in 8 blocks */
    private void stormAura(Player player) {
        String key = "storm_aura";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Storm Aura: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.YELLOW));
            return;
        }
        if (stormAuraActive.contains(player.getUniqueId())) {
            player.sendActionBar(Component.text("Storm Aura already active!", NamedTextColor.YELLOW));
            return;
        }
        cd.setCooldown(player, key, 25);
        stormAuraActive.add(player.getUniqueId());
        player.sendActionBar(Component.text("◈ Storm Aura active!", NamedTextColor.YELLOW));

        new BukkitRunnable() {
            int elapsed = 0; // in ticks
            final int duration = 120; // 6 seconds = 120 ticks
            final int interval = 30;  // strike every 1.5 seconds

            @Override
            public void run() {
                if (elapsed >= duration || !player.isOnline()) {
                    stormAuraActive.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (elapsed % interval == 0) {
                    List<Entity> nearby = new ArrayList<>(player.getNearbyEntities(8, 8, 8));
                    nearby.removeIf(e -> !(e instanceof LivingEntity) || e == player);
                    if (!nearby.isEmpty()) {
                        Entity target = nearby.get(new Random().nextInt(nearby.size()));
                        target.getWorld().strikeLightning(target.getLocation());
                    }
                }
                elapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    // =========================================================================
    // SOULREAPER
    // =========================================================================

    /** RMB — Soul Rend: empower next hit (double damage + Wither II) */
    private void soulRend(Player player) {
        String key = "soul_rend";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Soul Rend: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.DARK_GRAY));
            return;
        }
        if (soulRendActive.contains(player.getUniqueId())) {
            player.sendActionBar(Component.text("Soul Rend already charged!", NamedTextColor.GRAY));
            return;
        }
        cd.setCooldown(player, key, 15);
        soulRendActive.add(player.getUniqueId());
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 20, 0.3, 0.5, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.8f, 1.5f);
        player.sendActionBar(Component.text("◈ Soul Rend charged — next hit empowered!", NamedTextColor.DARK_GRAY));
    }

    /** Shift+RMB — Reaper's Gaze: glow + Weakness II on all in 10 blocks */
    private void reapersGaze(Player player) {
        String key = "soul_gaze";
        if (cd.isOnCooldown(player, key)) {
            player.sendActionBar(Component.text("⏳ Reaper's Gaze: " + cd.getRemainingSeconds(player, key) + "s", NamedTextColor.DARK_GRAY));
            return;
        }
        cd.setCooldown(player, key, 20);

        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 40, 1, 1, 1, 0.05);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 0.8f, 0.6f);

        int ticks = 100; // 5 seconds
        for (Entity e : player.getNearbyEntities(10, 10, 10)) {
            if (!(e instanceof LivingEntity le) || e == player) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, ticks, 0, false, false, true));
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 1, false, true, true));
        }
        player.sendActionBar(Component.text("◈ Reaper's Gaze!", NamedTextColor.DARK_GRAY));
    }

    // =========================================================================
    // Soul Rend — consumed on next hit via EntityDamageByEntity
    // =========================================================================

    @EventHandler
    public void onSoulRendHit(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!soulRendActive.contains(player.getUniqueId())) return;

        var item = player.getInventory().getItemInMainHand();
        if (!"SOULREAPER".equals(LegendaryItems.getLegendaryId(item))) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        soulRendActive.remove(player.getUniqueId());

        // Double the damage
        event.setDamage(event.getDamage() * 2);

        // Apply Wither II for 5 seconds
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1, false, true, true));
        target.getWorld().spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 30, 0.4, 0.5, 0.4, 0.05);
        player.sendActionBar(Component.text("💀 Soul Rend consumed!", NamedTextColor.DARK_GRAY));
    }

    // =========================================================================
    // Utility
    // =========================================================================

    /** Returns the nearest LivingEntity the player is looking at within maxDistance */
    private LivingEntity getTargetEntity(Player player, double maxDistance) {
        List<Entity> nearby = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        LivingEntity closest = null;
        double closestDot = 0.6; // only entities roughly in front

        Vector eyeDir = player.getLocation().getDirection();
        Location eye = player.getEyeLocation();

        for (Entity e : nearby) {
            if (!(e instanceof LivingEntity le) || e == player) continue;
            Vector toEntity = e.getLocation().add(0, e.getHeight() / 2, 0)
                    .subtract(eye).toVector().normalize();
            double dot = eyeDir.dot(toEntity);
            if (dot > closestDot) {
                closestDot = dot;
                closest = le;
            }
        }
        return closest;
    }

    public Set<UUID> getSoulRendActive() {
        return soulRendActive;
    }
}

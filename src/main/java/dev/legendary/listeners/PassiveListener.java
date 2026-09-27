package dev.legendary.listeners;

import dev.legendary.LegendaryPlugin;
import dev.legendary.items.LegendaryItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PassiveListener implements Listener {

    private final LegendaryPlugin plugin;

    // Emberheart: hit counter per player
    private final Map<UUID, Integer> emberHitCounter = new HashMap<>();

    public PassiveListener(LegendaryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack hand = player.getInventory().getItemInMainHand();
        String id = LegendaryItems.getLegendaryId(hand);
        if (id == null) return;

        switch (id) {
            case "VOIDBANE"     -> handleVoidbanePassive(target);
            case "EMBERHEART"   -> handleEmberheartPassive(player, target, event);
            case "GLACIUS"      -> handleGlaciusPassive(target);
            case "STORMBREAKER" -> handleStormbreakerPassive(player, target);
            case "SOULREAPER"   -> handleSoulreaperPassive(player, target, event);
        }
    }

    // ── Voidbane: Wither I on every hit ──────────────────────────────────────

    private void handleVoidbanePassive(LivingEntity target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0, false, true, true)); // 3s
    }

    // ── Emberheart: every 5 hits — true fire AoE bypassing fire resistance ───

    private void handleEmberheartPassive(Player player, LivingEntity target, EntityDamageByEntityEvent event) {
        UUID uid = player.getUniqueId();
        int count = emberHitCounter.getOrDefault(uid, 0) + 1;

        if (count >= 5) {
            emberHitCounter.put(uid, 0);
            // Deal direct HP damage to all nearby entities — bypasses fire resistance
            Location loc = target.getLocation();
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 40, 0.5, 0.5, 0.5, 0.1);
            loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 0.8f);

            for (Entity nearby : target.getNearbyEntities(3, 3, 3)) {
                if (nearby instanceof LivingEntity le && nearby != player) {
                    // damage() with null source = untyped damage — bypasses fire res
                    le.damage(6.0, player);
                    // Force damage bypassing fire resistance via extra direct health remove
                    applyTrueFireDamage(le, 4.0);
                }
            }
            // Also hit the primary target with true fire
            applyTrueFireDamage(target, 4.0);
            player.sendActionBar(Component.text("🔥 Ember Burst!", NamedTextColor.RED));
        } else {
            emberHitCounter.put(uid, count);
        }
    }

    /**
     * Deals damage that cannot be negated by Fire Resistance.
     * We subtract HP directly after the event resolves to bypass the fire-damage
     * check that Minecraft applies when a potion effect of FIRE_RESISTANCE is active.
     */
    private void applyTrueFireDamage(LivingEntity entity, double amount) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!entity.isDead()) {
                double newHealth = Math.max(0, entity.getHealth() - amount);
                entity.setHealth(newHealth);
                entity.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,
                        entity.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
            }
        }, 1L);
    }

    // ── Glacius: Slowness II on every hit ────────────────────────────────────

    private void handleGlaciusPassive(LivingEntity target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1, false, true, true)); // 2s
    }

    // ── Stormbreaker: lightning strike on EVERY hit ───────────────────────────

    private void handleStormbreakerPassive(Player player, LivingEntity target) {
        Location loc = target.getLocation();
        // Cosmetic lightning bolt — no fire, no damage (the sword handles damage normally)
        loc.getWorld().strikeLightningEffect(loc);
        // Extra real bolt that deals damage on top
        loc.getWorld().strikeLightning(loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 1.2f);
        player.sendActionBar(Component.text("⚡ Storm Strike!", NamedTextColor.YELLOW));
    }

    // ── Soulreaper: on kill Regen I (3s) ─────────────────────────────────────
    // Kill detection is in onEntityDeath below; here we track the attacker

    private void handleSoulreaperPassive(Player player, LivingEntity target, EntityDamageByEntityEvent event) {
        // Nothing needed per-hit; kill regen is in onEntityDeath
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        ItemStack hand = killer.getInventory().getItemInMainHand();
        String id = LegendaryItems.getLegendaryId(hand);
        if (!"SOULREAPER".equals(id)) return;

        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0, false, true, true)); // 3s
        killer.sendActionBar(Component.text("💀 Soul Consumed!", NamedTextColor.DARK_GRAY));
    }
}

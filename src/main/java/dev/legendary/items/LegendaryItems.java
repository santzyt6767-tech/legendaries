package dev.legendary.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LegendaryItems {

    public static final NamespacedKey LEGENDARY_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(LegendaryItems.class), "legendary_id");

    // ─── Shared enchant helper ───────────────────────────────────────────────

    /** Maxed survival enchants for swords — no Fire Aspect */
    private static void applySwordEnchants(ItemMeta meta) {
        meta.addEnchant(Enchantment.SHARPNESS,        10, true);
        meta.addEnchant(Enchantment.KNOCKBACK,          2, true);
        meta.addEnchant(Enchantment.LOOTING,            3, true);
        meta.addEnchant(Enchantment.SWEEPING_EDGE,      3, true);
        meta.addEnchant(Enchantment.UNBREAKING,        10, true);
        meta.addEnchant(Enchantment.MENDING,            1, true);
    }

    /** Maxed enchants for axes — no Fire Aspect */
    private static void applyAxeEnchants(ItemMeta meta) {
        meta.addEnchant(Enchantment.SHARPNESS,        10, true);
        meta.addEnchant(Enchantment.EFFICIENCY,         5, true);
        meta.addEnchant(Enchantment.FORTUNE,            3, true);
        meta.addEnchant(Enchantment.UNBREAKING,        10, true);
        meta.addEnchant(Enchantment.MENDING,            1, true);
    }

    /** Maxed enchants for Stormbreaker (sword base, spear theme) */
    private static void applySpearEnchants(ItemMeta meta) {
        meta.addEnchant(Enchantment.SHARPNESS,        10, true);
        meta.addEnchant(Enchantment.KNOCKBACK,          2, true);
        meta.addEnchant(Enchantment.LOOTING,            3, true);
        meta.addEnchant(Enchantment.UNBREAKING,        10, true);
        meta.addEnchant(Enchantment.MENDING,            1, true);
    }

    // ─── VOIDBANE ─────────────────────────────────────────────────────────────

    public static ItemStack createVoidbane() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("✦ Voidbane", NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("\"Forged in the heart of a dead star.\"", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("⚡ PASSIVE", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  Each hit inflicts Wither I (3s)", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Void Collapse", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [RMB] Pull all enemies within 6 blocks", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                Component.text("  toward you and deal damage. CD: 12s", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Dark Shroud", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [SHIFT+RMB] Invisibility + Speed II (5s)", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                Component.text("  CD: 20s", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("★ LEGENDARY SWORD", NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)
        ));

        applySwordEnchants(meta);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.STRING, "VOIDBANE");
        item.setItemMeta(meta);
        return item;
    }

    // ─── EMBERHEART ───────────────────────────────────────────────────────────

    public static ItemStack createEmberheart() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("✦ Emberheart", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("\"The flame never dies — it waits.\"", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("⚡ PASSIVE", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  Every 5 hits: soul fire burst (bypasses fire res)", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Magma Burst", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [RMB] Direct fire damage in 4 blocks", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                Component.text("  (bypasses fire resistance). CD: 10s", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Phoenix Step", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [SHIFT+RMB] Dash 8 blocks forward,", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                Component.text("  leaving a fire trail. CD: 15s", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("★ LEGENDARY SWORD", NamedTextColor.DARK_RED).decoration(TextDecoration.ITALIC, false)
        ));

        // No Fire Aspect — fire damage is handled manually to bypass resistance
        applySwordEnchants(meta);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.STRING, "EMBERHEART");
        item.setItemMeta(meta);
        return item;
    }

    // ─── GLACIUS ──────────────────────────────────────────────────────────────

    public static ItemStack createGlacius() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("✦ Glacius", NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("\"One touch — and time stops.\"", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("⚡ PASSIVE", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  Each hit inflicts Slowness II (2s)", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Glacial Prison", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [RMB] Freeze target: Slowness IV", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                Component.text("  + Mining Fatigue (4s). CD: 12s", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Blizzard", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [SHIFT+RMB] AoE Slowness III +", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                Component.text("  Blindness on all nearby (3s). CD: 18s", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("★ LEGENDARY SWORD", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)
        ));

        applySwordEnchants(meta);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.STRING, "GLACIUS");
        item.setItemMeta(meta);
        return item;
    }

    // ─── STORMBREAKER (sword base — non-throwable spear) ──────────────────────

    public static ItemStack createStormbreaker() {
        // Using NETHERITE_SWORD so it cannot be thrown like a trident.
        // Lightning-on-hit is handled entirely in PassiveListener.
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("✦ Stormbreaker", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("\"Lightning doesn't ask permission.\"", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("⚡ PASSIVE", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  Every hit summons lightning on the target", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Thunder Dash", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [RMB] Dash forward + lightning at origin", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.text("  CD: 12s", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Storm Aura", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [SHIFT+RMB] 6s aura: lightning strikes", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.text("  random enemies in 8 blocks. CD: 25s", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("★ LEGENDARY SPEAR", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
        ));

        applySpearEnchants(meta);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.STRING, "STORMBREAKER");
        item.setItemMeta(meta);
        return item;
    }

    // ─── SOULREAPER ───────────────────────────────────────────────────────────

    public static ItemStack createSoulreaper() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("✦ Soulreaper", NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true));

        meta.lore(List.of(
                Component.text("\"Every death makes it stronger.\"", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true),
                Component.empty(),
                Component.text("⚡ PASSIVE", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  On kill: Regeneration I (3s)", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Soul Rend", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [RMB] Next hit deals double damage", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("  + Wither II (5s). CD: 15s", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("◈ Reaper's Gaze", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true),
                Component.text("  [SHIFT+RMB] All enemies in 10 blocks", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("  glow + Weakness II (5s). CD: 20s", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("★ LEGENDARY AXE", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false)
        ));

        applyAxeEnchants(meta);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.STRING, "SOULREAPER");
        item.setItemMeta(meta);
        return item;
    }

    // ─── Utility ──────────────────────────────────────────────────────────────

    public static String getLegendaryId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(LEGENDARY_KEY, PersistentDataType.STRING);
    }
}

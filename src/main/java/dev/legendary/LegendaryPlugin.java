package dev.legendary;

import dev.legendary.commands.LegendaryCommand;
import dev.legendary.commands.WeaponCommand;
import dev.legendary.items.LegendaryType;
import dev.legendary.listeners.AbilityListener;
import dev.legendary.listeners.PassiveListener;
import dev.legendary.managers.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public class LegendaryPlugin extends JavaPlugin {

    private static LegendaryPlugin instance;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        cooldownManager = new CooldownManager();

        // /legendary <name>
        getCommand("legendary").setExecutor(new LegendaryCommand());

        // Individual weapon commands: /voidbane, /emberheart, etc.
        getCommand("voidbane").setExecutor(new WeaponCommand(LegendaryType.VOIDBANE));
        getCommand("emberheart").setExecutor(new WeaponCommand(LegendaryType.EMBERHEART));
        getCommand("glacius").setExecutor(new WeaponCommand(LegendaryType.GLACIUS));
        getCommand("stormbreaker").setExecutor(new WeaponCommand(LegendaryType.STORMBREAKER));
        getCommand("soulreaper").setExecutor(new WeaponCommand(LegendaryType.SOULREAPER));

        // Listeners
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new PassiveListener(this), this);

        getLogger().info("LegendaryWeapons loaded! 5 legendaries ready.");
    }

    @Override
    public void onDisable() {
        getLogger().info("LegendaryWeapons disabled.");
    }

    public static LegendaryPlugin getInstance() {
        return instance;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}

package dev.legendary.commands;

import dev.legendary.items.LegendaryItems;
import dev.legendary.items.LegendaryType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Handles /voidbane, /emberheart, /glacius, /stormbreaker, /soulreaper
 * Each is registered separately in plugin.yml but all route here.
 */
public class WeaponCommand implements CommandExecutor {

    private final LegendaryType type;

    public WeaponCommand(LegendaryType type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("legendary.get")) {
            player.sendMessage(Component.text("You don't have permission to claim legendary weapons.", NamedTextColor.RED));
            return true;
        }

        ItemStack weapon = switch (type) {
            case VOIDBANE     -> LegendaryItems.createVoidbane();
            case EMBERHEART   -> LegendaryItems.createEmberheart();
            case GLACIUS      -> LegendaryItems.createGlacius();
            case STORMBREAKER -> LegendaryItems.createStormbreaker();
            case SOULREAPER   -> LegendaryItems.createSoulreaper();
        };

        player.getInventory().addItem(weapon);
        player.sendMessage(Component.text("✦ You claimed ", NamedTextColor.GOLD)
                .append(Component.text(type.name(), NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.GOLD)));
        return true;
    }
}

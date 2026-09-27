package dev.legendary.commands;

import dev.legendary.items.LegendaryItems;
import dev.legendary.items.LegendaryType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LegendaryCommand implements CommandExecutor, TabCompleter {

    private static final List<String> WEAPON_NAMES = Arrays.stream(LegendaryType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

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

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        LegendaryType type = LegendaryType.fromString(args[0]);
        if (type == null) {
            player.sendMessage(Component.text("Unknown legendary: ", NamedTextColor.RED)
                    .append(Component.text(args[0], NamedTextColor.YELLOW))
                    .append(Component.text(". Use /legendary for the list.", NamedTextColor.RED)));
            return true;
        }

        ItemStack weapon = switch (type) {
            case VOIDBANE      -> LegendaryItems.createVoidbane();
            case EMBERHEART    -> LegendaryItems.createEmberheart();
            case GLACIUS       -> LegendaryItems.createGlacius();
            case STORMBREAKER  -> LegendaryItems.createStormbreaker();
            case SOULREAPER    -> LegendaryItems.createSoulreaper();
        };

        player.getInventory().addItem(weapon);
        player.sendMessage(Component.text("✦ You claimed ", NamedTextColor.GOLD)
                .append(Component.text(type.name(), NamedTextColor.YELLOW))
                .append(Component.text("!", NamedTextColor.GOLD)));
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("━━━ Legendary Weapons ━━━", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/legendary <name>", NamedTextColor.YELLOW)
                .append(Component.text(" — claim a legendary weapon", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("Available weapons:", NamedTextColor.GRAY));
        for (String name : WEAPON_NAMES) {
            player.sendMessage(Component.text("  • ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(name, NamedTextColor.YELLOW)));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return WEAPON_NAMES.stream()
                    .filter(n -> n.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

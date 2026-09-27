package dev.legendary.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // Map: playerUUID -> (ability key -> last used timestamp)
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(Player player, String ability) {
        long now = System.currentTimeMillis();
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (!playerCooldowns.containsKey(ability)) return false;
        return now < playerCooldowns.get(ability);
    }

    public long getRemainingSeconds(Player player, String ability) {
        long now = System.currentTimeMillis();
        Map<String, Long> playerCooldowns = cooldowns.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (!playerCooldowns.containsKey(ability)) return 0;
        long remaining = playerCooldowns.get(ability) - now;
        return remaining > 0 ? (remaining / 1000) + 1 : 0;
    }

    public void setCooldown(Player player, String ability, int seconds) {
        cooldowns
            .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
            .put(ability, System.currentTimeMillis() + (long) seconds * 1000);
    }

    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}

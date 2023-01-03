package dev.cabotmc.itemsgiver;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class TotemListener implements Listener {
    public HashMap<UUID, Location> safeLocations = new HashMap<>();
    public HashMap<UUID, Long> lastSaveTimes = new HashMap<UUID, Long>();
    @EventHandler
    public void totem(PlayerDeathEvent e) {
        if (e.getEntity().getLocation().getY() > 0) return;
        EquipmentSlot heldTotem;
        var inv = e.getPlayer().getInventory();
        if (inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) {
            heldTotem = EquipmentSlot.HAND;
        } else if (inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) {
            heldTotem = EquipmentSlot.OFF_HAND;
        } else {
            heldTotem = null;
        }
        if (heldTotem == null) {
            return; // not holding a totem;
        }
        var p = e.getEntity();
        Location tpLoc;
        if (safeLocations.containsKey(p.getUniqueId())) {
            tpLoc = safeLocations.get(p.getUniqueId());
        } else {
            tpLoc = Bukkit.getWorld("world").getSpawnLocation();
            p.sendMessage(Component.text("Couldn't find a safe location, teleporting you to the world spawn..."));
        }

        e.setCancelled(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemsGiver.instance, () -> {
            p.setVelocity(new Vector(0, 0, 0));
            p.setFallDistance(0);
            p.teleport(tpLoc.add(0, 2, 0));
            p.setHealth(Math.min(p.getHealth(), 4));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 10 * 20, 1));
            p.getInventory().setItem(heldTotem, null);
            p.playEffect(EntityEffect.TOTEM_RESURRECT);
        });
    }
    @EventHandler
    public void move(PlayerMoveEvent e) {
        var lastTime = lastSaveTimes.getOrDefault(e.getPlayer().getUniqueId(), 0L);
        var now = Instant.now().toEpochMilli();
        if (now - lastTime > 1000) {
            // only save a location every second
            var testLoc = e.getFrom().toBlockLocation();
            if (!testLoc.subtract(0, 1, 0).getBlock().getType().isSolid()) {
                // not supported by a block
                return;
            }
            lastSaveTimes.put(e.getPlayer().getUniqueId(), now);
            safeLocations.put(e.getPlayer().getUniqueId(), e.getFrom().toCenterLocation());
        }
    }
}

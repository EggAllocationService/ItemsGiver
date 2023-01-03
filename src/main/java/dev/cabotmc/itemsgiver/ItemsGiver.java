package dev.cabotmc.itemsgiver;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class ItemsGiver extends JavaPlugin implements Listener {
    public static BossBar progressBar = BossBar.bossBar(Component.text("Get an item every ten seconds!"), 0f, BossBar.Color.RED, BossBar.Overlay.NOTCHED_10);
    List<Material> possibleItems;
    public static NamespacedKey HAS_GONE_END_TAG = new NamespacedKey("cabot", "has_entered_end");
    public static NamespacedKey GIVE_ITEMS_TAG = new NamespacedKey("cabot", "give_items");
    public static ItemsGiver instance;
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::updateBar, 0L, 20L);
        possibleItems = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .toList();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new TotemListener(), this);
        getCommand("toggleitems").setExecutor(new ToggleGivingCommand());
    }

    @Override
    public void onDisable() {

    }
    @EventHandler
    public void join(PlayerJoinEvent e) {
        if (!e.getPlayer().getPersistentDataContainer().has(GIVE_ITEMS_TAG)) {
            e.getPlayer().getPersistentDataContainer().set(GIVE_ITEMS_TAG, PersistentDataType.BYTE, (byte) 1);
        }
        if (e.getPlayer().getPersistentDataContainer().getOrDefault(GIVE_ITEMS_TAG, PersistentDataType.BYTE, (byte) 1) == 1) {
            e.getPlayer().showBossBar(progressBar);
        }
        Team team = this.getServer().getScoreboardManager().getMainScoreboard().getTeam("players");
        if (team == null) {
            team = this.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("players");
            team.setAllowFriendlyFire(false);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        }
        team.addEntity(e.getPlayer());
    }
    public void updateBar() {
        var newProgress = progressBar.progress() + 0.1f;
        if (newProgress > 1) newProgress = 1;
        progressBar.progress(newProgress);
        if (progressBar.progress() >= 1) {
            progressBar.progress(0);
            secondPassed();
        }
    }
    @EventHandler
    public void endTravel(PlayerPortalEvent e) {
        if (e.getTo().getWorld().getEnvironment() == World.Environment.THE_END && e.getFrom().getWorld().getEnvironment() != World.Environment.THE_END) {
            e.getPlayer().getPersistentDataContainer().set(HAS_GONE_END_TAG, PersistentDataType.BYTE, (byte) 1);

        }
    }


    public void secondPassed() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.SURVIVAL) continue;
            if (player.getPersistentDataContainer().getOrDefault(GIVE_ITEMS_TAG, PersistentDataType.BYTE, (byte) 1) == 0) {
                continue;
            }
            int pos = this.possibleItems.size() - 1;
            pos = (int)Math.floor(Math.random() * (double)pos);
            Material material = this.possibleItems.get(pos);
            ItemStack item = new ItemStack(material);
            var results = player.getInventory().addItem(item);
            if (!results.isEmpty()) {
                player.getWorld().dropItem(player.getLocation(), item);
            }
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.8f, 1.0f);
        }
    }
}
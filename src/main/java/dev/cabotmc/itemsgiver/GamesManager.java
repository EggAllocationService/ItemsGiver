package dev.cabotmc.itemsgiver;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GamesManager implements Listener {
    static final NamespacedKey SPECTATOR_KEY = new NamespacedKey("cabot", "spectating");
    enum GameState {
        WAITING,
        PLAYING
    }
    static int readyCount = 0;
    static GameState state = GameState.WAITING;



    @EventHandler
    public void hit() {

    }
    @EventHandler
    public void tick(ServerTickStartEvent e) {
        if (state == GameState.WAITING && Bukkit.getOnlinePlayers().size() > 1 && readyCount == Bukkit.getOnlinePlayers().size()) {

            startGame();
        }
    }
    @EventHandler
    public void join(PlayerJoinEvent e) {

        if (state == GameState.PLAYING) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else {
            e.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
        var loc = Bukkit.getWorld("world").getSpawnLocation().set(0, 68, 0);
        e.getPlayer().teleport(loc);
        Bukkit.getWorld("world").setBlockData(0, 64, 0, Material.BEDROCK.createBlockData());

    }

    public static void startGame() {
        state = GameState.PLAYING;
        Bukkit.getServer().sendMessage(Component.text("Sending "));
        for (var p : Bukkit.getOnlinePlayers()) {
            var targetX = ThreadLocalRandom.current().nextInt(25, 100) * (Math.random() > 0.5 ? -1 : 1);
            var targetZ = ThreadLocalRandom.current().nextInt(25, 100) * (Math.random() > 0.5 ? -1 : 1);
            p.getWorld().loadChunk(targetX / 16, targetZ / 16, true);
            p.getWorld().setBlockData(targetX, 64, targetZ, Material.BEDROCK.createBlockData());
            var targetloc = p.getLocation().set(targetX, 66, targetZ);
            p.teleport(targetloc);
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setSaturatedRegenRate(Integer.MAX_VALUE);
            for (int i = 0; i < 9; i++) {
                p.getInventory().addItem(new ItemStack(Material.WHITE_WOOL, 64));
            }
            p.getInventory().setItemInOffHand(new ItemStack((Material.TOTEM_OF_UNDYING)));
        }
        var mainWorld = Bukkit.getWorld("world");
        // fill middle with air
        for (int x = -10; x < 11; x++) {
            for (int y = -10; y < 11; y++) {
                for (int z = -10; z < 11; z++) {
                    mainWorld.setBlockData(x, y, z, Material.AIR.createBlockData());
                }
            }
        }

        // make 5x5 bedrock platform
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                mainWorld.setBlockData(x, 64, z, Material.BEDROCK.createBlockData());
            }
        }
        Bukkit.getServer().sendMessage(Component.text("Border closing in 5 minutes"));
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemsGiver.instance, () -> {
            Bukkit.getWorld("world").getWorldBorder().setSize(3, TimeUnit.SECONDS, 300);
            Bukkit.getServer().sendMessage(Component.text("Border is now closing"));
        }, 20 * 300);

    }
    @EventHandler(ignoreCancelled = true)
    public void death(PlayerDeathEvent e) {
        e.setCancelled(true);
        for (var i: e.getPlayer().getInventory()) {
            if (i == null || i.getType() == Material.AIR) {
                continue;
            }
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i);
        }
        for (var i: e.getPlayer().getInventory().getArmorContents()) {
            if (i == null || i.getType() == Material.AIR) {
                continue;
            }
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i);
        }
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        var lightningLoc = e.getPlayer().getLocation();
        if (lightningLoc.getY() < 0) {
            lightningLoc.setY(1);
        }
        e.getPlayer().getWorld().spigot().strikeLightningEffect(lightningLoc, false);
        e.getPlayer().setHealth(20);
        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setSaturation(20);
        e.getPlayer().setSaturatedRegenRate(Integer.MAX_VALUE);

        e.getPlayer().setGameMode(GameMode.SPECTATOR);
        if (e.getPlayer().getLocation().getY() < 0) {
            e.getPlayer().teleport(e.getPlayer().getLocation().set(0, 64, 0));
        }
        e.getPlayer().showTitle(Title.title(
                Component.text("You Died", Style.style(style -> {
                    style.color(TextColor.color(0xeb4034));
                    style.decorate(TextDecoration.BOLD);
                })),
                Component.text("")
        ));
        e.getPlayer().setTotalExperience(0);
        Bukkit.getServer().sendMessage(e.deathMessage());

    }
    @EventHandler
    public void place(BlockPlaceEvent e) {
        if (e.getBlock().getLocation().getY() > 90) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (e.getTo().getY() < 0 & e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            e.getPlayer().setVelocity(new Vector(0, 0.1, 0));
            e.getPlayer().teleport(
                    e.getPlayer().getLocation().set(0, 68, 0)
            );
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        var p = (Player) e.getEntity();
        if (p.getGameMode() == GameMode.ADVENTURE) {
            e.setCancelled(true);
        }

    }
    public void enableSpectating(Player p) {
        p.getPersistentDataContainer().set(SPECTATOR_KEY, PersistentDataType.BYTE, (byte) 1);
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (!isSpectator(t)) {
                t.hidePlayer(ItemsGiver.instance, p);
            }
        }
        ItemsGiver.spectatorTeam.addEntity(p);

    }
    static boolean isSpectator(Player p ) {
        return p.getGameMode() == GameMode.SPECTATOR || p.getPersistentDataContainer().has(SPECTATOR_KEY);
    }

}

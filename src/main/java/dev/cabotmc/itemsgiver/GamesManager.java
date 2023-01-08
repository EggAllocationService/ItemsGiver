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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

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
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (state == GameState.PLAYING) {

        }
    }

    public static void startGame() {
        state = GameState.PLAYING;
        Bukkit.getServer().sendMessage(Component.text("Sending "));
        for (var p : Bukkit.getOnlinePlayers()) {
            var targetX = ThreadLocalRandom.current().nextInt(50, 150) * (Math.random() > 0.5 ? -1 : 1);
            var targetZ = ThreadLocalRandom.current().nextInt(50, 150) * (Math.random() > 0.5 ? -1 : 1);
            p.getWorld().loadChunk(targetX / 16, targetZ / 16, true);
            p.getWorld().setBlockData(targetX, 64, targetZ, Material.BEDROCK.createBlockData());
            var targetloc = p.getLocation().set(targetX, 66, targetZ);
            p.teleport(targetloc);
            p.setGameMode(GameMode.SURVIVAL);
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

    }
    @EventHandler
    public void death(PlayerDeathEvent e) {
        e.setCancelled(true);
        for (var i: e.getPlayer().getInventory()) {
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i);
        }
        for (var i: e.getPlayer().getInventory().getArmorContents()) {
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), i);
        }
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        e.getPlayer().getWorld().spigot().strikeLightningEffect(e.getPlayer().getLocation(), false);
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

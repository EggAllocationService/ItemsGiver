package dev.cabotmc.itemsgiver;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

        }
    }
    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (state == GameState.PLAYING) {

        }
    }

    public void startGame() {
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
        return p.getPersistentDataContainer().has(SPECTATOR_KEY);
    }

}

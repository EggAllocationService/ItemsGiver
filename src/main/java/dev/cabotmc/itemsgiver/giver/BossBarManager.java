package dev.cabotmc.itemsgiver.giver;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BossBarManager {
    static HashMap<UUID, PlayerPreferences> prefs = new HashMap<>();
    public static PlayerPreferences getPrefs(Player p) {
        if (!prefs.containsKey(p.getUniqueId())) {
            prefs.put(p.getUniqueId(), new PlayerPreferences());
        }
        return prefs.get(p.getUniqueId());
    }


    public static class PlayerPreferences {
        int currentTime = 0;
        int maxTime = 5;
        BossBar playerBar = BossBar.bossBar(Component.text("Get a new item every 5 seconds!"), 0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        public boolean tick() {
            currentTime++;
            playerBar.progress(progress());
            if (currentTime >= maxTime) {
                currentTime = 0;
                return true;
            } else {
                return false;
            }
        }
        public float progress() {
            var calc = currentTime / ((float) maxTime);
            if (calc > 1) {
                calc = 1f;
            }
            return calc;
        }
        public void setMaxTime(int max) {
            maxTime = max;
            playerBar.name(Component.text("Get a new item every " + max + " seconds!"));
        }
        public BossBar getBossBar() {
            return playerBar;
        }
    }
}

package dev.cabotmc.itemsgiver;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ToggleGivingCommand implements CommandExecutor  {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (! (sender instanceof Player)) {
            return false;
        }
        var p = (Player) sender;
        if (!p.getPersistentDataContainer().has(ItemsGiver.HAS_GONE_END_TAG)) {
            p.sendMessage(Component.text("You need to go to the end first!"));
            return true;
        }
        var current = p.getPersistentDataContainer().get(ItemsGiver.GIVE_ITEMS_TAG, PersistentDataType.BYTE);
        current = (byte) (current ^ 0x1);
        p.getPersistentDataContainer().set(ItemsGiver.GIVE_ITEMS_TAG, PersistentDataType.BYTE, current);
        if (current == 1) {
            p.showBossBar(ItemsGiver.progressBar);
        } else {
            p.hideBossBar(ItemsGiver.progressBar);
        }
        return true;
    }
}

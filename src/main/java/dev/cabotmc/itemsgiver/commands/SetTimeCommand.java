package dev.cabotmc.itemsgiver.commands;

import dev.cabotmc.itemsgiver.giver.BossBarManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class SetTimeCommand {

    public static void execute(CommandSender sender, Object[] args) {
        if (!(sender instanceof Player)) return;
        var player = (Player) sender;
        int time = (Integer) args[0];
        if (now() - lastRunTimes.getOrDefault(player.getUniqueId(), 0L) < 5000) {
            player.sendMessage(Component.text("You need to wait at least 5 seconds before running this command!", NamedTextColor.RED));
            return;
        }
        BossBarManager.getPrefs(player).setMaxTime(time);
        player.sendMessage(Component.text("Set your item delay to " + time + " seconds", NamedTextColor.GREEN));
        lastRunTimes.put(player.getUniqueId(), now());
    }
    public static HashMap<UUID,Long> lastRunTimes = new HashMap<>();
    static long now() {
        return Instant.now().toEpochMilli();
    }
    public static void register() {
        new CommandAPICommand("itemdelay")
                .withArguments(new IntegerArgument("time", 1, 30))
                .executes(SetTimeCommand::execute)
                .register();
    }
}

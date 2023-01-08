package dev.cabotmc.itemsgiver.commands;

import dev.cabotmc.itemsgiver.giver.BossBarManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SetTimeCommand {

    public static void execute(CommandSender sender, Object[] args) {
        if (!(sender instanceof Player)) return;
        var player = (Player) sender;
        int time = (Integer) args[0];
        BossBarManager.getPrefs(player).setMaxTime(time);
    }
    public static HashMap<UUID,Long> lastruntimes = new HashMap<>();

    public static void register() {
        new CommandAPICommand("itemdelay")
                .withArguments(new IntegerArgument("time", 1, 30))
                .executes(SetTimeCommand::execute)
                .register();
    }
}

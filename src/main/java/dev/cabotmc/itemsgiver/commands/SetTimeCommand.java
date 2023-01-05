package dev.cabotmc.itemsgiver.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTimeCommand {

    public static void execute(CommandSender sender, Object[] args) {
        if (!(sender instanceof Player)) return;
        var player = (Player) sender;
        int time = (Integer) args[0];


    }

    public static void register() {
        new CommandAPICommand("itemdelay")
                .withArguments(new IntegerArgument("time", 1, 30))
                .executes(SetTimeCommand::execute)
                .register();
    }
}

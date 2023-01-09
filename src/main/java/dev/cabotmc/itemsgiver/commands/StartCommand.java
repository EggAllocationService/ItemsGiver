package dev.cabotmc.itemsgiver.commands;

import dev.cabotmc.itemsgiver.GamesManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand {

    public static void execute(CommandSender sender, Object[] args) {
        if (!(sender instanceof Player)) return;
        GamesManager.startGame();
    }

    public static void register() {
        new CommandAPICommand("start")
                .withPermission("skyuhc.start")
                .executes(StartCommand::execute)
                .register();
    }
}

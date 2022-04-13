package org.salexperrucci.duels.commands;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.Arena;
import org.salexperrucci.duels.utiliy.Colorize;
import org.salexperrucci.duels.utiliy.Message;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class DuelsCommand implements CommandExecutor {

    private final DuelsPlugin plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            Colorize.sendMessage(commandSender, Message.MUST_BE_PLAYER_ERROR);
            return true;
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            Colorize.sendMessage(player, "&c/duels [join|quit]");
            return true;
        }

        if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("j")) {
            Optional<Arena> optionalArena = plugin.getArenaManager().findOpenArena();
            if (!optionalArena.isPresent()) {
                Colorize.sendMessage(player, "&cNo arenas are open. Try again in a bit.");
                return true;
            }

            if (plugin.getArenaManager().findPlayerArena(player).isPresent()) {
                Colorize.sendMessage(player, "&cYou're already in an arena.");
                return true;
            }

            optionalArena.get().addPlayer(player, plugin);
        } else if (args[0].equalsIgnoreCase("quit") || args[0].equalsIgnoreCase("q")) {
            Optional<Arena> playerArena = plugin.getArenaManager().findPlayerArena(player);
            if (!playerArena.isPresent()) {
                Colorize.sendMessage(player, "&cYou're not in an arena.");
                return true;
            }

            Arena arena = playerArena.get();
            arena.removePlayer(player, plugin);
            Colorize.sendMessage(player, "&cYou left the arena.");
        } else {
            Colorize.sendMessage(player, "&c/duels [join|quit]");
            return true;
        }

        return true;
    }

}

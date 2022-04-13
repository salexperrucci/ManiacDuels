package org.salexperrucci.duels.commands;

import org.salexperrucci.duels.DuelsPlugin;
import org.salexperrucci.duels.arena.Arena;
import org.salexperrucci.duels.arena.TemporaryArena;
import org.salexperrucci.duels.utiliy.Colorize;
import org.salexperrucci.duels.utiliy.Message;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@AllArgsConstructor
public class ArenaCommand implements CommandExecutor {

    private final DuelsPlugin plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            Colorize.sendMessage(commandSender, Message.MUST_BE_PLAYER_ERROR);
            return true;
        }

        Player player = (Player) commandSender;
        if (!player.hasPermission("duels.admin")) {
            Colorize.sendMessage(player, Message.NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            Colorize.sendMessage(commandSender, "&c/arena [setup|delete|list]");
            return true;
        }

        if (args[0].equalsIgnoreCase("setup")) {
            String arenaName = arenaNameFromArgs(args);
            if (arenaName.isEmpty()) {
                Colorize.sendMessage(commandSender, "&cArena name can't be empty.");
                return true;
            }

            Optional<Arena> optionalArena = plugin.getArenaManager().findArena(arenaName);
            TemporaryArena temporaryArena = optionalArena.map(TemporaryArena::new).orElseGet(() -> new TemporaryArena(arenaName));

            plugin.getArenaManager().getArenaSetupManager().addToSetup(player, temporaryArena);
        } else if (args[0].equalsIgnoreCase("delete")) {
            String arenaName = arenaNameFromArgs(args);
            Optional<Arena> optionalArena = plugin.getArenaManager().findArena(arenaName);
            if (!optionalArena.isPresent()) {
                Colorize.sendMessage(player, "&cNo arena with that name exists.");
                return true;
            }

            plugin.getArenaManager().deleteArena(optionalArena.get());
            Colorize.sendMessage(player, "&cDeleted " + arenaName + ".");
        } else if (args[0].equalsIgnoreCase("list")) {
            if (plugin.getArenaManager().getArenas().size() == 0) {
                Colorize.sendMessage(player, "&cNo arenas have been setup.");
                return true;
            }

            plugin.getArenaManager().getArenas().forEach(arena -> Colorize.sendMessage(player, "&a" + arena.getDisplayName() + " - " + arena.getArenaState().getClass()));
        } else {
            Colorize.sendMessage(player, "&c/arena [setup|delete|list]");
        }

        return true;
    }

    private String arenaNameFromArgs(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (i == 0) continue;

            stringBuilder.append(arg);
            if (i != args.length - 1) {
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

}

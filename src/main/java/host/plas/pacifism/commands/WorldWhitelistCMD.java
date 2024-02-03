package host.plas.pacifism.commands;

import host.plas.pacifism.Pacifism;
import io.streamlined.bukkit.commands.CommandContext;
import io.streamlined.bukkit.commands.SimplifiedCommand;
import org.bukkit.Bukkit;
import org.bukkit.generator.WorldInfo;

import java.util.concurrent.ConcurrentSkipListSet;

public class WorldWhitelistCMD extends SimplifiedCommand {
    public WorldWhitelistCMD() {
        super("worldwhitelist", Pacifism.getInstance());
    }

    @Override
    public boolean command(CommandContext commandContext) {
        if (commandContext.getArgs().isEmpty()) {
            commandContext.sendMessage("Usage: /worldwhitelist <add|remove|list|set-as> [world]");
            return false;
        }

        String action = commandContext.getStringArg(0);
        action = action.toLowerCase();

        switch (action) {
            case "add":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("Usage: /worldwhitelist add <world>");
                    return false;
                }

                String world = commandContext.getStringArg(1);
                Pacifism.getWorldConfig().addWorld(world);
                commandContext.sendMessage("Added world " + world + " to the whitelist.");
                return true;
            case "remove":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("Usage: /worldwhitelist remove <world>");
                    return false;
                }

                world = commandContext.getStringArg(1);
                Pacifism.getWorldConfig().removeWorld(world);
                commandContext.sendMessage("Removed world " + world + " from the whitelist.");
                return true;
            case "list":
                ConcurrentSkipListSet<String> worlds = Pacifism.getWorldConfig().getWorlds();
                commandContext.sendMessage("Worlds in the whitelist: " + worlds);
                return true;
            case "set-as":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("Usage: /worldwhitelist set-as <whitelist|blacklist>");
                    return false;
                }

                String type = commandContext.getStringArg(1);
                type = type.toLowerCase();

                if (type.equals("whitelist")) {
                    Pacifism.getWorldConfig().setWhitelist(true);
                    commandContext.sendMessage("Set the world list type to whitelist.");
                    return true;
                } else if (type.equals("blacklist")) {
                    Pacifism.getWorldConfig().setWhitelist(false);
                    commandContext.sendMessage("Set the world list type to blacklist.");
                    return true;
                } else {
                    commandContext.sendMessage("Usage: /worldwhitelist set-as <whitelist|blacklist>");
                    return false;
                }
            default:
                commandContext.sendMessage("Usage: /worldwhitelist <add|remove|list|set-as> [world]");
                return false;
        }

//        return false;
    }

    @Override
    public ConcurrentSkipListSet<String> tabComplete(CommandContext commandContext) {
        ConcurrentSkipListSet<String> completions = new ConcurrentSkipListSet<>();

        if (commandContext.getArgs().size() == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("list");
            completions.add("set-as");
        }
        if (commandContext.getArgs().size() == 2) {
            if (commandContext.getStringArg(0).equalsIgnoreCase("set-as")) {
                completions.add("whitelist");
                completions.add("blacklist");
            }
            if (commandContext.getStringArg(0).equalsIgnoreCase("remove")) {
                ConcurrentSkipListSet<String> worlds = Pacifism.getWorldConfig().getWorlds();
                completions.addAll(worlds);
            }
            if (commandContext.getStringArg(0).equalsIgnoreCase("add")) {
                completions.addAll(Bukkit.getWorlds().stream().map(WorldInfo::getName).toList());
            }
        }

        return completions;
    }
}

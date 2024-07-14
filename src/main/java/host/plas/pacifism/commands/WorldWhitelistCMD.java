package host.plas.pacifism.commands;

import host.plas.pacifism.Pacifism;
import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
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
            commandContext.sendMessage("&cUsage: /worldwhitelist <add|remove|list|set-as> [world]");
            return false;
        }

        String action = commandContext.getStringArg(0);
        action = action.toLowerCase();

        switch (action) {
            case "add":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("&cUsage: /worldwhitelist add <world>");
                    return false;
                }

                String world = commandContext.getStringArg(1);
                Pacifism.getWorldConfig().addWorld(world);
                commandContext.sendMessage("&eAdded world &c" + world + " &eto the world list&8.");
                return true;
            case "remove":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("&cUsage: /worldwhitelist remove <world>");
                    return false;
                }

                world = commandContext.getStringArg(1);
                Pacifism.getWorldConfig().removeWorld(world);
                commandContext.sendMessage("&eRemoved world &c" + world + " &efrom the world list&8.");
                return true;
            case "list":
                ConcurrentSkipListSet<String> worlds = Pacifism.getWorldConfig().getWorlds();
                StringBuilder builder = new StringBuilder();
                for (String w : worlds) {
                    builder.append("&a").append(w).append("&7, ");
                }
                if (! builder.isEmpty()) {
                    builder.delete(builder.length() - 4, builder.length());
                }

                commandContext.sendMessage("&eWorlds in the whitelist&8: " + builder);
                return true;
            case "set-as":
                if (commandContext.getArgs().size() < 2) {
                    commandContext.sendMessage("&cUsage: /worldwhitelist set-as <whitelist|blacklist>");
                    return false;
                }

                String type = commandContext.getStringArg(1);
                type = type.toLowerCase();

                if (type.equals("whitelist")) {
                    Pacifism.getWorldConfig().setWhitelist(true);
                    commandContext.sendMessage("&eSet the world list type to &awhitelist&8.");
                    return true;
                } else if (type.equals("blacklist")) {
                    Pacifism.getWorldConfig().setWhitelist(false);
                    commandContext.sendMessage("&eSet the world list type to &cblacklist&8.");
                    return true;
                } else {
                    commandContext.sendMessage("&cUsage: /worldwhitelist set-as <whitelist|blacklist>");
                    return false;
                }
            default:
                commandContext.sendMessage("&cUsage: /worldwhitelist <add|remove|list|set-as> [world]");
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

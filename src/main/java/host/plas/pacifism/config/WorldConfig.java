package host.plas.pacifism.config;

import host.plas.pacifism.Pacifism;
import org.bukkit.World;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

public class WorldConfig extends SimpleConfiguration {
    public WorldConfig() {
        super("worlds.yml", Pacifism.getInstance(), false);
    }

    @Override
    public void init() {
        getWorlds();
        isWhitelist();
    }

    public ConcurrentSkipListSet<String> getWorlds() {
        reloadResource();

        return new ConcurrentSkipListSet<>(getOrSetDefault("list", new ArrayList<>()));
    }

    public void setWorlds(ConcurrentSkipListSet<String> worlds) {
        reloadResource();

        write("list", new ArrayList<>(worlds));
    }

    public void addWorld(String world) {
        ConcurrentSkipListSet<String> worlds = getWorlds();

        worlds.add(world);

        setWorlds(worlds);
    }

    public void removeWorld(String world) {
        ConcurrentSkipListSet<String> worlds = getWorlds();

        worlds.remove(world);

        setWorlds(worlds);
    }

    public boolean canCheckInWorld(World world) {
        return
                (isWhitelist() && getWorlds().contains(world.getName()))
                ||
                (!isWhitelist() && !getWorlds().contains(world.getName()))
                ;
    }

    public boolean isInList(String world) {
        return getWorlds().contains(world);
    }

    public boolean isWhitelist() {
        reloadResource();

        return getOrSetDefault("whitelist", false);
    }

    public void setWhitelist(boolean whitelist) {
        reloadResource();

        write("whitelist", whitelist);
    }
}

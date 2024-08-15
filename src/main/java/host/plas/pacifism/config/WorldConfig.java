package host.plas.pacifism.config;

import host.plas.pacifism.Pacifism;
import host.plas.pacifism.utils.ConfigUtils;
import org.bukkit.World;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

public class WorldConfig extends SimpleConfiguration {
    public WorldConfig() {
        super("worlds.yml", Pacifism.getInstance(), true);
    }

    @Override
    public void init() {
        getWorlds();
        isWhitelist();
    }

    public StringWhitelist get() {
        return ConfigUtils.getWhitelist(this, "world", "", "list", "whitelist", true);
    }

    public ConcurrentSkipListSet<String> getWorlds() {
        return get().getWhitelist();
    }

    public void setWorlds(ConcurrentSkipListSet<String> worlds) {
        StringWhitelist g = get();
        g.setWhitelist(worlds);

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

    public boolean canPacifyInWorld(World world) {
        return canPacifyInWorld(world.getName());
    }

    public boolean canPacifyInWorld(String world) {
        return get().contains(world);
    }

    public boolean isInList(String world) {
        return getWorlds().contains(world);
    }

    public boolean isWhitelist() {
        return ! get().isBlacklist();
    }

    public void setWhitelist(boolean whitelist) {
        StringWhitelist g = get();
        g.setBlacklist(! whitelist);

        write("whitelist", whitelist);
    }
}

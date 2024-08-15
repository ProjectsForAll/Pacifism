package host.plas.pacifism.utils;

import host.plas.pacifism.config.StringWhitelist;
import host.plas.pacifism.players.PacifismWhitelist;
import tv.quaint.storage.resources.StorageResource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ConfigUtils {

    public static PacifismWhitelist getPWhitelist(StorageResource<?> resource, String identifier, String prefix, String list, String isBlacklist) {
        resource.reloadResource();

        List<String> gottenList = resource.getOrSetDefault(prefix + list, new ArrayList<>(List.of()));
        boolean gottenBlacklist = resource.getOrSetDefault(prefix + isBlacklist, false);
        double radius = resource.getOrSetDefault(prefix + "radius", 0.0);

        PacifismWhitelist w = new PacifismWhitelist(identifier);
        w.setWhitelist(new ConcurrentSkipListSet<>(gottenList));
        w.setBlacklist(gottenBlacklist);
        w.setRadius(radius);

        return w;
    }

    public static StringWhitelist getWhitelist(StorageResource<?> resource, String identifier, String prefix, String list, String isBlacklist) {
        return getWhitelist(resource, identifier, prefix, list, isBlacklist, false);
    }

    public static StringWhitelist getWhitelist(StorageResource<?> resource, String identifier, String prefix, String list, String isBlacklist, boolean whitelistIsOpposite) {
        resource.reloadResource();

        List<String> gottenList = resource.getOrSetDefault(prefix + list, new ArrayList<>(List.of()));
        boolean gottenBlacklist = resource.getOrSetDefault(prefix + isBlacklist, false);

        if (whitelistIsOpposite) {
            gottenBlacklist = ! gottenBlacklist;
        }

        StringWhitelist w = new StringWhitelist(identifier);
        w.setWhitelist(new ConcurrentSkipListSet<>(gottenList));
        w.setBlacklist(gottenBlacklist);

        return w;
    }
}

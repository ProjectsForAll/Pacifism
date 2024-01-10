package host.plas.pacifism.config;

import host.plas.pacifism.Pacifism;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", Pacifism.getInstance(), false);
    }

    @Override
    public void init() {
        getPlayerForceToggleTicks();
        getPlayerForceToggleSetAs();
        getPlayerForceToggleEnabled();
        getPlayerForceToggleMessage();
        getPlayerForceToggleSendMessage();
    }

    public int getPlayerForceToggleTicks() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.after", 20 * 60 * 15); // 15 minutes
    }

    public boolean getPlayerForceToggleSetAs() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.set-as", true); // Default to false
    }

    public boolean getPlayerForceToggleEnabled() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.enabled", true); // Is enabled by default
    }

    public String getPlayerForceToggleMessage() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.message",
                "&7&oYou seem fit to &c&lfight&8&o! &7&oWe have enabled your &c&lPVP&8&o!"); // The message.
    }

    public boolean getPlayerForceToggleSendMessage() {
        reloadResource();

        return getOrSetDefault("player.force-toggle.send-message", true); // The message.
    }
}

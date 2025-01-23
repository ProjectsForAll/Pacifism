package host.plas.pacifism.config;

import host.plas.pacifism.Pacifism;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MessageConfig extends SimpleConfiguration {
    public MessageConfig() {
        super("messages.yml", Pacifism.getInstance(), true);
    }

    @Override
    public void init() {
        getStatusEnabled();
        getStatusEnabled();

        getToggleSelfSelf();
        getToggleSelfUponEnable();
        getToggleSelfUponDisable();
        getToggleOtherSelf();
        getToggleOtherOther();
        getToggleOtherUponEnable();
        getToggleOtherUponDisable();

        getSetGraceTimeSuccessSelfSelf();
        getSetGraceTimeSuccessOtherSelf();
        getSetGraceTimeSuccessOtherOther();

        getSetSelfSelf();
        getSetSelfUponEnable();
        getSetSelfUponDisable();
        getSetOtherSelf();
        getSetOtherOther();
        getSetOtherUponEnable();
        getSetOtherUponDisable();

        getPvpDisabledSelf();
        getPvpDisabledOther();
    }

    public String getStatusEnabled() {
        reloadResource();

        return getOrSetDefault("status.enabled", "&aenabled");
    }

    public String getStatusDisabled() {
        reloadResource();

        return getOrSetDefault("status.disabled", "&cdisabled");
    }






    public String getToggleSelfSelf() {
        reloadResource();

        return getOrSetDefault("toggle.success.self.self", "&eYou have %status% &eyour PVP&8!");
    }

    public String getToggleSelfUponEnable() {
        reloadResource();

        return getOrSetDefault("toggle.success.self.upon.enable", "&7(You will be able to take damage from other players.)");
    }

    public String getToggleSelfUponDisable() {
        reloadResource();

        return getOrSetDefault("toggle.success.self.upon.disable", "&7(You will not be able to take damage from other players.)");
    }

    public String getToggleOtherSelf() {
        reloadResource();

        return getOrSetDefault("toggle.success.other.self", "&eYou have %status% &b%player_name%&e's PVP&8!");
    }

    public String getToggleOtherOther() {
        reloadResource();

        return getOrSetDefault("toggle.success.other.other", "&eYour PVP has been %status% &eby &b%player_name%&8!");
    }

    public String getToggleOtherUponEnable() {
        reloadResource();

        return getOrSetDefault("toggle.success.other.upon.enable", "&7(They will be able to take damage from other players.)");
    }

    public String getToggleOtherUponDisable() {
        reloadResource();

        return getOrSetDefault("toggle.success.other.upon.disable", "&7(They will not be able to take damage from other players.)");
    }

    public String getToggleCannotSelfSelf() {
        reloadResource();

        return getOrSetDefault("toggle.cannot.self.self", "&cYou cannot toggle your PVP&8!");
    }

    public String getToggleCannotSelfTimeLeft() {
        reloadResource();

        return getOrSetDefault("toggle.cannot.self.time-left", "&cYou have &f%time_seconds% &cseconds left before you can toggle your PVP again&8!");
    }

    public String getToggleCannotOtherSelf() {
        reloadResource();

        return getOrSetDefault("toggle.cannot.other.self", "&cYou cannot toggle &b%player_name%&c's PVP&8!");
    }

    public String getToggleCannotOtherTimeLeft() {
        reloadResource();

        return getOrSetDefault("toggle.cannot.other.time-left",  "&cThey have &f%time_seconds% &cseconds left before they can toggle their PVP again&8!");
    }






    public String getSetGraceTimeSuccessSelfSelf() {
        reloadResource();

        return getOrSetDefault("set-grace-time.success.self.self", "&eYou have set your grace-time to &b%time_seconds% &eseconds&8!");
    }

    public String getSetGraceTimeSuccessOtherSelf() {
        reloadResource();

        return getOrSetDefault("set-grace-time.success.other.self", "&eYou have set &b%player_name%&e's grace-time to &b%time_seconds% &eseconds&8!");
    }

    public String getSetGraceTimeSuccessOtherOther() {
        reloadResource();

        return getOrSetDefault("set-grace-time.success.other.other", "&eYour grace-time has been set to &b%time_seconds% &eseconds&8!");
    }





    public String getSetSelfSelf() {
        reloadResource();

        return getOrSetDefault("set.success.self.self", "&eYou have %status% &eyour PVP&8!");
    }

    public String getSetSelfUponEnable() {
        reloadResource();

        return getOrSetDefault("set.success.self.upon.enable", "&7(You will be able to take damage from other players.)");
    }

    public String getSetSelfUponDisable() {
        reloadResource();

        return getOrSetDefault("set.success.self.upon.disable", "&7(You will not be able to take damage from other players.)");
    }

    public String getSetOtherSelf() {
        reloadResource();

        return getOrSetDefault("set.success.other.self", "&eYou have %status% &b%player_name%&e's PVP&8!");
    }

    public String getSetOtherOther() {
        reloadResource();

        return getOrSetDefault("set.success.other.other", "&eYour PVP has been %status% &eby &b%player_name%&8!");
    }

    public String getSetOtherUponEnable() {
        reloadResource();

        return getOrSetDefault("set.success.other.upon.enable", "&7(They will be able to take damage from other players.)");
    }

    public String getSetOtherUponDisable() {
        reloadResource();

        return getOrSetDefault("set.success.other.upon.disable", "&7(They will not be able to take damage from other players.)");
    }

    public String getSetCannotSelfSelf() {
        reloadResource();

        return getOrSetDefault("set.cannot.self.self", "&cYou cannot toggle your PVP&8!");
    }

    public String getSetCannotSelfTimeLeft() {
        reloadResource();

        return getOrSetDefault("set.cannot.self.time-left", "&cYou have &f%time_seconds% &cseconds left before you can toggle your PVP again&8!");
    }

    public String getSetCannotOtherSelf() {
        reloadResource();

        return getOrSetDefault("set.cannot.other.self", "&cYou cannot toggle &b%player_name%&c's PVP&8!");
    }

    public String getSetCannotOtherTimeLeft() {
        reloadResource();

        return getOrSetDefault("set.cannot.other.time-left",  "&cThey have &f%time_seconds% &cseconds left before they can toggle their PVP again&8!");
    }







    public String getPvpDisabledSelf() {
        reloadResource();

        return getOrSetDefault("pvp.disabled.self", "&cYou have PVP disabled! You cannot hurt other players.");
    }

    public String getPvpDisabledOther() {
        reloadResource();

        return getOrSetDefault("pvp.disabled.other", "&cThat player has PVP disabled! You cannot hurt them.");
    }
}

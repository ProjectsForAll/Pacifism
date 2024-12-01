package host.plas.pacifism.placeholders;

import host.plas.bou.compat.CompatManager;
import host.plas.bou.compat.papi.PAPIHeld;
import host.plas.pacifism.Pacifism;
import lombok.Getter;
import lombok.Setter;

public class PAPIManager {
    @Getter @Setter
    private static PacifismExpansion expansion;

    public static void init() {
        if (isEnabled()) {
        }
    }

    public static boolean isEnabled() {
        return CompatManager.isEnabled(CompatManager.PAPI_IDENTIFIER);
    }

    public static PAPIHeld getHolder() {
        return (PAPIHeld) CompatManager.getHolder(CompatManager.PAPI_IDENTIFIER);
    }

    public static void register() {
        if (! isEnabled()) {
            Pacifism.getInstance().logInfo("Tried to register PAPI placeholders in a non-PAPI environment.");
            return;
        }

        setExpansion(new PacifismExpansion());
    }
}

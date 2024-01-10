package host.plas.pacifism.events;

import io.streamlined.bukkit.instances.BaseRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter @Setter
public class WatchedBlock extends BaseRunnable {
    private Block block;
    private Player interactingPlayer;
    private long ticksLeft;

    public WatchedBlock(Block block, Player interactingPlayer, long ticksLeft) {
        super(10 * 20, 1);
        this.block = block;
        this.interactingPlayer = interactingPlayer;
        this.ticksLeft = ticksLeft;

        register();
    }

    public WatchedBlock(Block block, Player interactingPlayer) {
        this(block, interactingPlayer, 20L * 10L);
    }

    public String getBlockString() {
        return getBlockString(block);
    }

    public void register() {
        addWatchedBlock(this);
    }

    public void unregister() {
        removeWatchedBlock(this);
    }

    public static String getBlockString(Block block) {
        return block.getWorld().getName() + ";" + block.getX() + "," + block.getY() + "," + block.getZ() + ";";
    }

    @Override
    public void execute() {
        unregister();

        cancel();
    }

    @Override
    public int compareTo(@NotNull BaseRunnable o) {
        if (! (o instanceof WatchedBlock)) return super.compareTo(o);

        WatchedBlock watchedBlock = (WatchedBlock) o;

        return getBlockString().compareTo(watchedBlock.getBlockString());
    }

    @Getter @Setter
    private static ConcurrentSkipListSet<WatchedBlock> watchedBlocks = new ConcurrentSkipListSet<>();

    public static void addWatchedBlock(WatchedBlock watchedBlock) {
        watchedBlocks.add(watchedBlock);
    }

    public static void removeWatchedBlock(WatchedBlock watchedBlock) {
        watchedBlocks.remove(watchedBlock);
    }

    public static boolean isWatchedBlock(Block block) {
        return watchedBlocks.stream().anyMatch(watchedBlock -> watchedBlock.getBlockString().equals(getBlockString(block)));
    }

    public static Optional<WatchedBlock> getWatchedBlock(Block block) {
        return watchedBlocks.stream().filter(watchedBlock -> watchedBlock.getBlockString().equals(getBlockString(block))).findFirst();
    }

    public static WatchedBlock getOrGetWatchedBlock(Block block, Player interactingPlayer) {
        Optional<WatchedBlock> optionalWatchedBlock = getWatchedBlock(block);
        if (optionalWatchedBlock.isEmpty()) {
            WatchedBlock watchedBlock = new WatchedBlock(block, interactingPlayer);
            addWatchedBlock(watchedBlock);
            return watchedBlock;
        }

        return optionalWatchedBlock.get();
    }
}

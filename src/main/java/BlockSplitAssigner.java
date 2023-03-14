import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface BlockSplitAssigner{
    Optional<BlockSourceSplit> getNext(@Nullable String var1);

    void addSplits(Collection<BlockSourceSplit> var1);

    Collection<BlockSourceSplit> remainingSplits();

    @FunctionalInterface
    public interface Provider extends Serializable {
        BlockSplitAssigner create(Collection<BlockSourceSplit> var1);
    }
}

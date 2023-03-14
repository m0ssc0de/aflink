import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.ArrayList;

public class SimpleBlockSplitAssigner implements BlockSplitAssigner{
    private final ArrayList<BlockSourceSplit> splits;

    public SimpleBlockSplitAssigner(Collection<BlockSourceSplit> splits) {
        this.splits = new ArrayList<>(splits);
    }

    @Override
    public Optional<BlockSourceSplit> getNext(@Nullable String var1) {
        int size = this.splits.size();
        return size == 0 ? Optional.empty() : Optional.of(this.splits.remove(size - 1));
    }

    @Override
    public void addSplits(Collection<BlockSourceSplit> var1) {
        this.splits.addAll(var1);
    }

    @Override
    public Collection<BlockSourceSplit> remainingSplits() {
        return this.splits;
    }

    public String toString() {
        return "SimpleBlockSplitAssigner " + this.splits;
    }
}

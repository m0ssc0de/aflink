import javassist.compiler.ast.Pair;

import java.math.BigInteger;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import org.apache.flink.util.Preconditions;

import javax.annotation.Nullable;


public class BlockSourceSplitChk<SplitT extends BlockSourceSplit> {
    private final Collection<SplitT> splits;
    private final Collection<Pair> alreadyProcessedBlocks;
    @Nullable
    byte[] serializedFormCache;

    protected BlockSourceSplitChk(Collection<SplitT> splits, Collection<Pair> alreadyProcessedBlocks) {
        this.splits = Collections.unmodifiableCollection(splits);
        this.alreadyProcessedBlocks = Collections.unmodifiableCollection(alreadyProcessedBlocks);
    }

    public Collection<SplitT> getSplits() { return this.splits; }

    public Collection<Pair> getAlreadyProcessedBlocks() { return this.alreadyProcessedBlocks; }

    public String toString() {
        return "BlockSourceSplitChk:\n\t\t Pending Splits: " + this.splits + '\n' + "\t\t Processed Paths: " + this.alreadyProcessedBlocks + '\n';
    }

    public static <T extends BlockSourceSplit> BlockSourceSplitChk<T> fromCollectionSnapshot(Collection<T> splits) {
        Preconditions.checkNotNull(splits);
        Collection<T> copy = new ArrayList(splits);
        return new BlockSourceSplitChk(copy, Collections.emptySet());
    }

    public static <T extends BlockSourceSplit> BlockSourceSplitChk<T> fromCollectionSnapshot(Collection<T> splits, Collection<Pair> alreadyProcessedBlocks) {
        Preconditions.checkNotNull(splits);
        Collection<T> copy = new ArrayList(splits);
        Collection<T> pairsCopy = new ArrayList(alreadyProcessedBlocks);
        return new BlockSourceSplitChk(copy, pairsCopy);
    }

    static <T extends BlockSourceSplit> BlockSourceSplitChk<T> reusingCollection(Collection<T> splits, Collection<Pair> alreadyProcessedPairs) {
        return new BlockSourceSplitChk(splits, alreadyProcessedPairs);
    }
}

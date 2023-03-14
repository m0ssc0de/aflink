import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.*;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.connector.file.src.ContinuousEnumerationSettings;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.util.FlinkRuntimeException;
import org.apache.flink.util.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class BlockSource<T, SplitT extends BlockSourceSplit> implements Source<T, SplitT, BlockSourceSplitChk<SplitT>>, ResultTypeQueryable<T> {
    private final BlockEnumerator.Provider enumeratorFactory;
    private final BlockSplitAssigner.Provider assignerFactory;
    private final BlockBulkFormat<T, SplitT> readerFormat;
    private final ContinuousEnumerationSettings continuousEnumerationSettings;


    protected BlockSource(BlockEnumerator.Provider blockEnumerator, BlockSplitAssigner.Provider splitAssigner, BlockBulkFormat<T, SplitT> readerFormat, @Nullable ContinuousEnumerationSettings continuousEnumerationSettings) {
        this.enumeratorFactory = Preconditions.checkNotNull(blockEnumerator);
        this.assignerFactory = Preconditions.checkNotNull(splitAssigner);
        this.readerFormat = Preconditions.checkNotNull(readerFormat);
        this.continuousEnumerationSettings = continuousEnumerationSettings;
    }

    public BlockSplitAssigner.Provider getAssignerFactory() {
        return this.assignerFactory;
    }

    @javax.annotation.Nullable
    public ContinuousEnumerationSettings getContinuousEnumerationSettings() {
        return this.continuousEnumerationSettings;
    }

    @Override
    public Boundedness getBoundedness() {
        return this.continuousEnumerationSettings == null ? Boundedness.BOUNDED : Boundedness.CONTINUOUS_UNBOUNDED;
    }

    @Override
    public SourceReader<T, SplitT> createReader(SourceReaderContext readerContext) throws Exception {
        return new BlockSourceReader(readerContext, this.readerFormat, readerContext.getConfiguration());
    }

    @Override
    public SplitEnumerator<SplitT, BlockSourceSplitChk<SplitT>> createEnumerator(SplitEnumeratorContext<SplitT> enumContext) throws Exception {
        BlockEnumerator enumerator = this.enumeratorFactory.create();

        Collection splits;
        try {
            splits = enumerator.enumerateSplits(0, enumContext.currentParallelism());
        } catch (IOException var5) {
            throw new FlinkRuntimeException("Could not enumerate block splits", var5);
        }
        return this.createSplitEnumerator(enumContext, enumerator, splits, (Collection)null);
    }

    @Override
    public SplitEnumerator<SplitT, BlockSourceSplitChk<SplitT>> restoreEnumerator(SplitEnumeratorContext<SplitT> splitEnumeratorContext, BlockSourceSplitChk<SplitT> splitTBlockSourceSplitChk) throws Exception {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<SplitT> getSplitSerializer() {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<BlockSourceSplitChk<SplitT>> getEnumeratorCheckpointSerializer() {
        return null;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return null;
    }

    private SplitEnumerator<SplitT, BlockSourceSplitChk<SplitT>> createSplitEnumerator(SplitEnumeratorContext<SplitT> context, BlockEnumerator enumerator, Collection<BlockSourceSplit> splits, @Nullable Collection<Path> alreadyProcessedPaths) {
        BlockSplitAssigner splitAssigner = this.assignerFactory.create(splits);
        if (this.continuousEnumerationSettings == null) {
            return null;
        } else {
            if (alreadyProcessedPaths == null) {
                alreadyProcessedPaths = splitsToPaths(splits);
            }
            return (SplitEnumerator<SplitT, BlockSourceSplitChk<SplitT>>)new ContinuousBlockSplitEnumerator((SplitEnumeratorContext<BlockSourceSplit>) context, enumerator, splitAssigner, null, null, this.continuousEnumerationSettings.getDiscoveryInterval().toMillis());
        }
    }
    private static Collection<Path> splitsToPaths(Collection<BlockSourceSplit> splits) {
        return (Collection)splits.stream().map(BlockSourceSplit::path).collect(Collectors.toCollection(HashSet::new));
    }

    public static final class BlockSourceBuilder<T, SplitT extends BlockSourceSplit, SELF extends BlockSourceBuilder<T, SplitT, SELF>> {

    }
}

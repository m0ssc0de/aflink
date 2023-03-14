import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.base.source.reader.RecordEmitter;
import org.apache.flink.connector.base.source.reader.SingleThreadMultiplexSourceReaderBase;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;

public final class BlockSourceReader<T, SplitT extends BlockSourceSplit> extends SingleThreadMultiplexSourceReaderBase<RecordAndPosition<T>, T, SplitT, BlockSourceSplitState<SplitT>> {

//    public BlockSourceReader(SourceReaderContext readerContext, )
//    public BlockSourceReader(Supplier<SplitReader<RecordAndPosition<T>, SplitT>> splitReaderSupplier, RecordEmitter<RecordAndPosition<T>, T, BlockSourceSplitState<SplitT>> recordEmitter, Configuration config, SourceReaderContext context) {
//        super(splitReaderSupplier, recordEmitter, config, context);
//    }
    public BlockSourceReader(SourceReaderContext readerContext, BlockBulkFormat<T, SplitT> readerFormat, Configuration config) {
        super(
                () -> {
                    return new BlockSourceSplitReader(config, readerFormat);
                },
                new BlockSourceRecordEmitter(),
                config,
                readerContext
        );
    }

    @Override
    protected void onSplitFinished(Map<String, BlockSourceSplitState<SplitT>> map) {
        this.context.sendSplitRequest();
    }

    @Override
    protected BlockSourceSplitState<SplitT> initializedState(SplitT splitT) {
        return new BlockSourceSplitState(splitT);
    }

    @Override
    protected SplitT toSplitType(String s, BlockSourceSplitState<SplitT> splitTBlockSourceSplitState) {
        return splitTBlockSourceSplitState.toBlockSourceSplit();
    }
}

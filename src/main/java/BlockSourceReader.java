import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.connector.base.source.reader.SingleThreadMultiplexSourceReaderBase;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

import java.util.Map;

//public final class BlockSourceReader<T, SplitT extends BlockSourceSplit> extends SingleThreadMultiplexSourceReaderBase<RecordAndPosition<T>, T, SplitT, BlockSourceSplitState<SplitT>> {
////    public  BlockSourceReader(SourceReaderContext readerContext, BulkFormat<T, SplitT> readerFor)
//    @Override
//    protected void onSplitFinished(Map<String, BlockSourceSplitState<SplitT>> map) {
//
//    }
//
//    @Override
//    protected BlockSourceSplitState<SplitT> initializedState(SplitT splitT) {
//        return null;
//    }
//
//    @Override
//    protected SplitT toSplitType(String s, BlockSourceSplitState<SplitT> splitTBlockSourceSplitState) {
//        return null;
//    }
//}

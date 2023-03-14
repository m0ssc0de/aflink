import org.apache.flink.api.connector.source.SourceOutput;
import org.apache.flink.connector.base.source.reader.RecordEmitter;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

public class BlockSourceRecordEmitter<T, SplitT extends BlockSourceSplit> implements RecordEmitter<RecordAndPosition<T>, T, BlockSourceSplitState<SplitT>> {
    BlockSourceRecordEmitter() {}

    public void emitRecord(RecordAndPosition<T> element, SourceOutput<T> output, BlockSourceSplitState<SplitT> splitState) {
        output.collect(element.getRecord());
        splitState.setPosition(element.getOffset(), element.getRecordSkipCount());
    }
}

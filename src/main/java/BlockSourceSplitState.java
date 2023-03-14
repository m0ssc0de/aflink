import org.apache.flink.connector.file.src.util.CheckpointedPosition;
import org.apache.flink.util.FlinkRuntimeException;
import org.apache.flink.util.Preconditions;

import java.util.Optional;

public class BlockSourceSplitState<SplitT extends BlockSourceSplit> {
    private final BlockSourceSplit split;
    private long offset;
    private long recordsToSkipAfterOffset;

    public BlockSourceSplitState(SplitT split) {
        this.split = Preconditions.checkNotNull(split);
        Optional<CheckpointedPosition> readerPosition = split.getReaderPosition();
        if (readerPosition.isPresent()) {
            this.offset = ((CheckpointedPosition)readerPosition.get()).getOffset();
            this.recordsToSkipAfterOffset = ((CheckpointedPosition)readerPosition.get()).getRecordsAfterOffset();
        } else {
            this.offset = -1L;
            this.recordsToSkipAfterOffset = 0L;
        }
    }

//    public SplitT toFileSourceSplit() {
    public SplitT toBlockSourceSplit() {
        CheckpointedPosition position = this.offset == -1L && this.recordsToSkipAfterOffset == 0L ? null : new CheckpointedPosition(this.offset, this.recordsToSkipAfterOffset);
        BlockSourceSplit updatedSplit = this.split.updateWithCheckpointedPosition(position);
        if (updatedSplit == null) {
            throw new FlinkRuntimeException("Split returned 'null' in updateWithCheckpointedPosition(): " + this.split);
        } else if (updatedSplit.getClass() != this.split.getClass()) {
            throw new FlinkRuntimeException(String.format("Split returned different type in updateWithCheckpointedPosition(). Split type is %s, returned type is %s", this.split.getClass().getName(), updatedSplit.getClass().getName()));
        } else {
            return (SplitT) updatedSplit;
        }
    }

    public void setPosition(long offset, long recordsToSkipAfterOffset) {
        this.offset = offset;
        this.recordsToSkipAfterOffset = recordsToSkipAfterOffset;
    }
}

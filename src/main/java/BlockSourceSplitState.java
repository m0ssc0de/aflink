import org.apache.flink.connector.file.src.util.CheckpointedPosition;
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
}

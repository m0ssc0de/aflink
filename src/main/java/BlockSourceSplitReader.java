import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsAddition;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.util.CheckpointedPosition;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

import javax.annotation.Nullable;
import java.io.IOException;
import org.apache.flink.configuration.Configuration;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


final class BlockSourceSplitReader<T, SplitT extends BlockSourceSplit> implements SplitReader<RecordAndPosition<T>, SplitT> {
    private static final Logger LOG = LoggerFactory.getLogger(BlockSourceSplitReader.class);
    private final Configuration config;
    private final BlockBulkFormat<T, SplitT> readerFactory;
    private final Queue<SplitT> splits;
    @Nullable
    private BlockBulkFormat.Reader<T> currentReader;
    @Nullable
    private String currentSplitId;

//    @Nullable
//    private String curr

    public BlockSourceSplitReader(Configuration config, BlockBulkFormat<T, SplitT> readerFactory) {
        this.config = config;
        this.readerFactory = readerFactory;
        this.splits = new ArrayDeque<>();
    }

    @Override
    public RecordsWithSplitIds<RecordAndPosition<T>> fetch() throws IOException {
        this.checkSplitOrStartNext();
        BlockBulkFormat.RecordIterator<T> nextBatch = this.currentReader.readBatch();
        return nextBatch == null ? this.finishSplit() : BlockRecords.forRecords(this.currentSplitId, nextBatch);
    }

    public void handleSplitsChanges(SplitsChange<SplitT> splitChange) {
        if (!(splitChange instanceof SplitsAddition)) {
            throw new UnsupportedOperationException(String.format("The SplitChange type of %s is not supported.", splitChange.getClass()));
        } else {
            LOG.debug("Handling split change {}", splitChange);
            this.splits.addAll(splitChange.splits());
        }
    }

    @Override
    public void wakeUp() {

    }

    @Override
    public void close() throws Exception {
        if (this.currentReader != null) {
            this.currentReader.close();
        }

    }

    private void checkSplitOrStartNext() throws IOException {
        if (this.currentReader == null) {
            SplitT nextSplit = this.splits.poll();
            if (nextSplit == null) {
                throw new IOException("Cannot fetch from another split - no split remaining");
            } else {
                this.currentSplitId = nextSplit.splitId();
                Optional<CheckpointedPosition> position = nextSplit.getReaderPosition();
                this.currentReader = position.isPresent() ? this.readerFactory.restoreReader(this.config, nextSplit) : this.readerFactory.createReader(this.config, nextSplit);
            }
        }
    }

    private BlockRecords<T> finishSplit() throws IOException {
        if (this.currentReader != null) {
            this.currentReader.close();
            this.currentReader = null;
        }

        BlockRecords<T> finishRecords = BlockRecords.finishedSplit(this.currentSplitId);
        this.currentSplitId = null;
        return finishRecords;
    }
}

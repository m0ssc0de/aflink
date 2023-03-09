import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.base.source.reader.splitreader.SplitReader;
import org.apache.flink.connector.base.source.reader.splitreader.SplitsChange;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

import java.io.IOException;
import java.lang.module.Configuration;
import java.util.ArrayDeque;
import java.util.Queue;

final class BlockSourceSplitReader<T, SplitT extends FileSourceSplit> implements SplitReader<RecordAndPosition<T>, SplitT> {
    private final Configuration config;
    private final Queue<SplitT> splits;

    public BlockSourceSplitReader(Configuration config) {
        this.config = config;
        this.splits = new ArrayDeque<>();
    }

    @Override
    public RecordsWithSplitIds<RecordAndPosition<T>> fetch() throws IOException {
        return null;
    }

    @Override
    public void handleSplitsChanges(SplitsChange<SplitT> splitsChange) {

    }

    @Override
    public void wakeUp() {

    }

    @Override
    public void close() throws Exception {

    }
}

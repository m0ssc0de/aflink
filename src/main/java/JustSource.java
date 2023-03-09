import org.apache.flink.api.connector.source.*;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.PendingSplitsCheckpoint;
import org.apache.flink.core.io.SimpleVersionedSerializer;

public class JustSource<T, SplitT extends FileSourceSplit> implements Source<T, SplitT, PendingSplitsCheckpoint<SplitT>> {
    @Override
    public Boundedness getBoundedness() {
        return null;
    }

    @Override
    public SourceReader<T, SplitT> createReader(SourceReaderContext sourceReaderContext) throws Exception {
        return null;
    }

    @Override
    public SplitEnumerator<SplitT, PendingSplitsCheckpoint<SplitT>> createEnumerator(SplitEnumeratorContext<SplitT> splitEnumeratorContext) throws Exception {
        return null;
    }

    @Override
    public SplitEnumerator<SplitT, PendingSplitsCheckpoint<SplitT>> restoreEnumerator(SplitEnumeratorContext<SplitT> splitEnumeratorContext, PendingSplitsCheckpoint<SplitT> splitTPendingSplitsCheckpoint) throws Exception {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<SplitT> getSplitSerializer() {
        return null;
    }

    @Override
    public SimpleVersionedSerializer<PendingSplitsCheckpoint<SplitT>> getEnumeratorCheckpointSerializer() {
        return null;
    }
}

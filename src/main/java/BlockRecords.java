import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.file.src.util.RecordAndPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BlockRecords<T> implements RecordsWithSplitIds<RecordAndPosition<T>> {
    @Nullable
    @Override
    public String nextSplit() {
        return null;
    }

    @Nullable
    @Override
    public RecordAndPosition<T> nextRecordFromSplit() {
        return null;
    }

    @Override
    public Set<String> finishedSplits() {
        return null;
    }

    @Override
    public void recycle() {
        RecordsWithSplitIds.super.recycle();
    }
}

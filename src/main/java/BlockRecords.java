import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.connector.file.src.impl.FileRecords;
import org.apache.flink.connector.file.src.reader.BulkFormat;
import org.apache.flink.connector.file.src.util.RecordAndPosition;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

public class BlockRecords<T> implements RecordsWithSplitIds<RecordAndPosition<T>> {
    @Nullable
    private  String splitId;
    @Nullable
    private BlockBulkFormat.RecordIterator<T> recordsForSplitCurrent;
    @Nullable
    private final BlockBulkFormat.RecordIterator<T> recordsForSplit;
    private final Set<String> finishedSplits;


    private BlockRecords(@Nullable String splitId, @Nullable BlockBulkFormat.RecordIterator<T> recordsForSplit, Set<String> finishedSplits) {
        this.splitId = splitId;
        this.recordsForSplit = recordsForSplit;
        this.finishedSplits = finishedSplits;
    }
    @Nullable
    @Override
    public String nextSplit() {
        String nextSplit = this.splitId;
        this.splitId = null;
        return nextSplit;
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

    public static <T> BlockRecords<T> forRecords(String splitId, BlockBulkFormat.RecordIterator<T> recordsForSplit) {
        return new BlockRecords(splitId, recordsForSplit, Collections.emptySet());
    }


    public static <T> BlockRecords<T> finishedSplit(String splitId) {
        return new BlockRecords((String)null, (BlockBulkFormat.RecordIterator)null, Collections.singleton(splitId));
    }
}

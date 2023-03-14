import org.apache.flink.connector.file.src.util.MutableRecordAndPosition;
import org.apache.flink.connector.file.src.util.RecordAndPosition;
import org.jetbrains.annotations.Nullable;
import java.util.Iterator;

public class BlockRecordIterator<E> implements BlockBulkFormat.RecordIterator{
    private final Iterator<E> records;
    private final MutableRecordAndPosition<E> recordAndPosition;

    public BlockRecordIterator(Iterator<E> records) {
        this.records = records;
        this.recordAndPosition = new MutableRecordAndPosition();
        this.recordAndPosition.setPosition(0, 0);
    }

    @Nullable
    @Override
    public RecordAndPosition next() {
        if (this.records.hasNext()) {
            this.recordAndPosition.setNext(this.records.next());
            return this.recordAndPosition;
        } else {
            return null;
        }
    }

    @Override
    public void releaseBatch() {

    }
}

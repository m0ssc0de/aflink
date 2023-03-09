import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.file.src.util.CheckpointedPosition;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;

//public class BlockSourceSplit implements SourceSplit, Serializable {
public class BlockSourceSplit implements SourceSplit {
    private final String id;
    private final BigInteger blockStart;
    private final BigInteger blockEnd;
    @Nullable
    private final CheckpointedPosition readerPosition;


    public BlockSourceSplit(String id, BigInteger blockStart, BigInteger blockEnd) {
        this(id, blockStart, blockEnd, (CheckpointedPosition)null);
    }

    public BlockSourceSplit(String id, BigInteger blockStart, BigInteger blockEnd, @Nullable CheckpointedPosition readerPosition) {
        this.id = id;
        this.blockStart = blockStart;
        this.blockEnd = blockEnd;
        this.readerPosition = readerPosition;
    }

    @Override
    public String splitId() {
        return this.id;
    }

    public Optional<CheckpointedPosition> getReaderPosition() {
        return Optional.ofNullable(this.readerPosition);
    }

    public String toString() {
        return String.format("BlockSourceSplit: [%s, %s] ID=%s position=%s", this.blockStart, this.blockEnd, this.id, this.readerPosition);
    }
}

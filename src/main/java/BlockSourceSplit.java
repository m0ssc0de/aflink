import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.connector.file.src.util.CheckpointedPosition;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;
import org.apache.flink.core.fs.Path;

//public class BlockSourceSplit implements SourceSplit, Serializable {
public class BlockSourceSplit implements SourceSplit {
    private final Path filePath;
    private final String id;
    private final BigInteger blockStart;
    private final BigInteger blockEnd;
    @Nullable
    private final CheckpointedPosition readerPosition;

    public Path path() { return this.filePath; }

    public BlockSourceSplit(String id, Path filePath, BigInteger blockStart, BigInteger blockEnd) {
        this(id, filePath, blockStart, blockEnd, (CheckpointedPosition)null);
    }

    public BlockSourceSplit(String id, Path filePath, BigInteger blockStart, BigInteger blockEnd, @Nullable CheckpointedPosition readerPosition) {
        this.id = id;
        this.filePath = filePath;
        this.blockStart = blockStart;
        this.blockEnd = blockEnd;
        this.readerPosition = readerPosition;
    }

    public BigInteger start() {
        return this.blockStart;
    }
    public BigInteger end() {
        return this.blockEnd;
    }

    @Override
    public String splitId() {
        return this.id;
    }

    public Optional<CheckpointedPosition> getReaderPosition() {
        return Optional.ofNullable(this.readerPosition);
    }

    public BlockSourceSplit updateWithCheckpointedPosition(@Nullable CheckpointedPosition position) {
        return new BlockSourceSplit(this.id, this.filePath, this.blockStart, this.blockEnd, position);
    }

    public String toString() {
        return String.format("BlockSourceSplit: [%s, %s] ID=%s position=%s", this.blockStart, this.blockEnd, this.id, this.readerPosition);
    }
}

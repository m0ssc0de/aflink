import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.connector.file.src.reader.BulkFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.util.RecordAndPosition;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.io.Closeable;
import org.apache.flink.configuration.Configuration;

public interface BlockBulkFormat<T, SplitT extends BlockSourceSplit> extends Serializable, ResultTypeQueryable<T> {
    Reader<T> createReader(Configuration var1, SplitT var2) throws IOException;
    Reader<T> restoreReader(Configuration var1, SplitT var2) throws IOException;

    boolean isSplittable();

    TypeInformation<T> getProducedType();

    public interface RecordIterator<T> {
        @Nullable
        RecordAndPosition<T> next();

        void releaseBatch();
    }

    public interface Reader<T> extends Closeable {
        @Nullable
        BlockBulkFormat.RecordIterator<T> readBatch() throws IOException;

        void close() throws IOException;
    }
}

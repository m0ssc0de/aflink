import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.src.reader.FileRecordFormat;
import org.apache.flink.connector.file.src.util.CheckpointedPosition;
import org.apache.flink.core.fs.Path;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public interface BlockRecordFormat<T> extends Serializable, ResultTypeQueryable<T> {
    ConfigOption<Integer> RECORDS_PER_FETCH = ConfigOptions.key("source.file.records.fetch-size").intType().defaultValue(128).withDescription("The number of records to hand over from the I/O thread to file reader in one unit.");

    Reader<T> createReader(Configuration var1, BigInteger start, BigInteger end) throws IOException;

    Reader<T> restoreReader(Configuration var1, BigInteger start, BigInteger end) throws IOException;

    boolean isSplittable();

    TypeInformation<T> getProducedType();

    public interface Reader<T> extends Closeable {
//        @Nullable
        T read() throws IOException;

        void close() throws IOException;

        @Nullable
        default CheckpointedPosition getCheckpointedPosition() {
            return null;
        }
    }
}

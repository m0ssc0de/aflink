import org.apache.flink.annotation.Public;
import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.jetbrains.annotations.Nullable;
import org.apache.flink.configuration.Configuration;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

public interface BlockStreamFormat<T> extends Serializable, ResultTypeQueryable<T> {

    Reader<T> createReader(Configuration var1, BlockDataInputStream var2, long var3, long var5) throws IOException;

    Reader<T> restoreReader(Configuration var1, BlockDataInputStream var2, long var3, long var5, long var7) throws IOException;

    boolean isSplittable();

    TypeInformation<T> getProducedType();

    @PublicEvolving
    public interface Reader<T> extends Closeable {
        @Nullable
        T read() throws IOException;

        void close() throws IOException;
    }
}

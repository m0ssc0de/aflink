import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.Preconditions;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.flink.configuration.Configuration;
import org.jetbrains.annotations.Nullable;

public class BlockRecordFormatAdapter<T> implements BlockBulkFormat<T, BlockSourceSplit> {
    private final BlockRecordFormat<T> blockFormat;

    public BlockRecordFormatAdapter(BlockRecordFormat<T> blockFormat) {
        this.blockFormat = (BlockRecordFormat<T>) Preconditions.checkNotNull(blockFormat);
    }

    @Override
    public Reader<T> createReader(Configuration config, BlockSourceSplit split) throws IOException {
        BlockRecordFormat.Reader<T> reader = this.blockFormat.createReader(config, split.start(), split.end());
        try {
            return null;
        } catch (Throwable var) {
            IOUtils.closeQuietly(reader);
            ExceptionUtils.rethrowIOException(var);
            return null;
        }
//        return (BlockBulkFormat.Reader)Util.do
//        return null;
    }

    @Override
    public Reader<T> restoreReader(Configuration var1, BlockSourceSplit var2) throws IOException {
        return null;
    }

    @Override
    public boolean isSplittable() {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return null;
    }

    public static final class Reader<T> implements BlockBulkFormat.Reader<T> {
        private final BlockRecordFormat.Reader<T> reader;
        private final BigInteger start;
        private final BigInteger end;

        Reader(BlockRecordFormat.Reader<T> reader, BigInteger start, BigInteger end) {
            Preconditions.checkArgument(end.compareTo(start) != -1, "split end must >= start");
            this.reader = (BlockRecordFormat.Reader)Preconditions.checkNotNull(reader);
            this.start = start;
            this.end = end;
        }

        @Nullable
        @Override
        public BlockBulkFormat.RecordIterator<T> readBatch() throws IOException {
            ArrayList<T> result = new ArrayList<>(10);
            Object next = this.reader.read();
            result.add((T) next);
            if (result.isEmpty()) {
                return null;
            }
            BlockBulkFormat.RecordIterator<T> iter = new BlockRecordIterator(result.iterator());
            return iter;
        }

        @Override
        public void close() throws IOException {

        }
    }

}

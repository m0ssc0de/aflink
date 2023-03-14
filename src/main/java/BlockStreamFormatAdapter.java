import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BlockStreamFormatAdapter<T> implements BlockBulkFormat<T, BlockSourceSplit> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(BlockStreamFormatAdapter.class);
    private final BlockStreamFormat<T> streamFormat;

    public BlockStreamFormatAdapter(BlockStreamFormat<T> streamFormat) {
        this.streamFormat = Preconditions.checkNotNull(streamFormat);
    }

    @Override
    public Reader<T> createReader(Configuration config, BlockSourceSplit split) throws IOException {

        return null;
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

    private static final class TrackingBlockDataInputStream extends BlockDataInputStream {
        private final BlockDataInputStream stream;

        TrackingBlockDataInputStream(BlockDataInputStream stream, long fileLength, int batchSize) {
            this.stream = stream;
        }

        @Override
        public void seek(long desired) throws IOException {
            this.stream.seek(desired);
        }

        @Override
        public long getPos() throws IOException {
            return this.stream.getPos();
        }

        /**
         * Reads the next byte of data from the input stream. The value byte is
         * returned as an <code>int</code> in the range <code>0</code> to
         * <code>255</code>. If no byte is available because the end of the stream
         * has been reached, the value <code>-1</code> is returned. This method
         * blocks until input data is available, the end of the stream is detected,
         * or an exception is thrown.
         *
         * <p> A subclass must provide an implementation of this method.
         *
         * @return the next byte of data, or <code>-1</code> if the end of the
         * stream is reached.
         * @throws IOException if an I/O error occurs.
         */
        @Override
        public int read() throws IOException {
            return 0;
        }
    }
}

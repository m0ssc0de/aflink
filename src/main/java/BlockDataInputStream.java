import java.io.IOException;
import java.io.InputStream;

public abstract class BlockDataInputStream extends InputStream {
    public BlockDataInputStream() {
    }

    public abstract void seek(long var1) throws IOException;

    public abstract long getPos() throws IOException;
}

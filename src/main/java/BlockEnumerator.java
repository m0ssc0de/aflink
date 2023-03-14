import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

public interface BlockEnumerator {
    Collection<BlockSourceSplit> enumerateSplits(int var1, int var2) throws IOException;

    @FunctionalInterface
    public interface Provider extends Serializable {
        BlockEnumerator create();
    }
}

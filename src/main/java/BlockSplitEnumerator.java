import org.apache.flink.api.connector.source.SourceEvent;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.util.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BlockSplitEnumerator implements SplitEnumerator<BlockSourceSplit, BlockSourceSplitChk<BlockSourceSplit>> {
    private static final Logger LOG = LoggerFactory.getLogger(BlockSplitEnumerator.class);
    private final SplitEnumeratorContext<BlockSourceSplit> context;
    private final BlockSplitAssigner splitAssigner;

    public BlockSplitEnumerator(SplitEnumeratorContext<BlockSourceSplit> context, BlockSplitAssigner splitAssigner) {
        this.context = (SplitEnumeratorContext<BlockSourceSplit>) Preconditions.checkNotNull(context);
        this.splitAssigner = (BlockSplitAssigner)Preconditions.checkNotNull(splitAssigner);
    }

    @Override
    public void start() {

    }

    @Override
    public void handleSplitRequest(int subtask, @Nullable String hostname) {
        if (this.context.registeredReaders().containsKey(subtask)) {
            if (LOG.isInfoEnabled()) {
                String hostInfo = hostname == null ? "(no host locality info)" : "(on host '" + hostname + "')";
                LOG.info("Subtask {} {} is requesting a file source split", subtask, hostInfo);
            }

            Optional<BlockSourceSplit> nextSplit = this.splitAssigner.getNext(hostname);
            if (nextSplit.isPresent()) {
                BlockSourceSplit split = (BlockSourceSplit)nextSplit.get();
                this.context.assignSplit(split, subtask);
                LOG.info("Assigned split to subtask {} : {}", subtask, split);
            } else {
                this.context.signalNoMoreSplits(subtask);
                LOG.info("No more splits available for subtask {}", subtask);
            }
        }

    }
    public void handleSourceEvent(int subtaskId, SourceEvent sourceEvent) {
        LOG.error("Received unrecognized event: {}", sourceEvent);
    }


    @Override
    public void addSplitsBack(List<BlockSourceSplit> splits, int i) {
        LOG.debug("Block Source Enumerator adds splits back: {}", splits);
        this.splitAssigner.addSplits(splits);
    }

    @Override
    public void addReader(int i) {

    }

    @Override
    public BlockSourceSplitChk<BlockSourceSplit> snapshotState(long checkpointId) throws Exception {
        return BlockSourceSplitChk.fromCollectionSnapshot(this.splitAssigner.remainingSplits());
    }

    @Override
    public void close() throws IOException {

    }
}

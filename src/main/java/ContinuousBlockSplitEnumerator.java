import org.apache.flink.api.connector.source.SourceSplit;
import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;
import org.apache.flink.connector.file.src.FileSourceSplit;
import org.apache.flink.connector.file.src.assigners.FileSplitAssigner;
import org.apache.flink.connector.file.src.enumerate.FileEnumerator;
import org.apache.flink.connector.file.src.impl.ContinuousFileSplitEnumerator;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ContinuousBlockSplitEnumerator<SplitT extends BlockSourceSplit> implements SplitEnumerator<SplitT, BlockSourceSplitChk<SplitT>>{

    private static final Logger LOG = LoggerFactory.getLogger(ContinuousFileSplitEnumerator.class);
    private final SplitEnumeratorContext<SplitT> context;
    private final BlockSplitAssigner splitAssigner;
    private final BlockEnumerator enumerator;
    private final HashSet<Path> pathsAlreadyProcessed;
    private final LinkedHashMap<Integer, String> readersAwaitingSplit;
    private final Path[] paths;
    private final long discoveryInterval;

    public ContinuousBlockSplitEnumerator(SplitEnumeratorContext<BlockSourceSplit> context, BlockEnumerator enumerator, BlockSplitAssigner splitAssigner, Path[] paths, Collection<Path> alreadyDiscoveredPaths, long discoveryInterval) {
        Preconditions.checkArgument(discoveryInterval > 0L);
        this.context = (SplitEnumeratorContext)Preconditions.checkNotNull(context);
        this.enumerator = Preconditions.checkNotNull(enumerator);
        this.splitAssigner = Preconditions.checkNotNull(splitAssigner);
        this.paths = paths;
        this.discoveryInterval = discoveryInterval;
        this.pathsAlreadyProcessed = new HashSet(alreadyDiscoveredPaths);
        this.readersAwaitingSplit = new LinkedHashMap();
    }


    @Override
    public void start() {
        this.context.callAsync(
                () -> {
                    return this.enumerator.enumerateSplits(666, 1);
                },
                this::processDiscoveredSplits,
                this.discoveryInterval, this.discoveryInterval
        );
    }

    @Override
    public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {
        this.readersAwaitingSplit.put(subtaskId, requesterHostname);
        this.assignSplits();
    }

    @Override
    public void addSplitsBack(List<SplitT> splits, int i) {
        LOG.debug("Block Source Enumerator adds splits back: {}", splits);
        this.splitAssigner.addSplits((Collection<BlockSourceSplit>) splits);
    }

    @Override
    public void addReader(int i) {

    }

    @Override
    public BlockSourceSplitChk snapshotState(long l) throws Exception {
        return null;
    }

    private void processDiscoveredSplits(Collection<BlockSourceSplit> splits, Throwable error) {
        if (error != null) {
            LOG.error("Failed to enumerate files", error);
        } else {
            Collection<BlockSourceSplit> newSplits = (Collection)splits.stream().filter((split) -> {
                return this.pathsAlreadyProcessed.add(split.path());
            }).collect(Collectors.toList());
            this.splitAssigner.addSplits(newSplits);
            this.assignSplits();
        }
    }

    private void assignSplits() {
        Iterator<Map.Entry<Integer, String>> awaitingReader = this.readersAwaitingSplit.entrySet().iterator();

        while(awaitingReader.hasNext()) {
            Map.Entry<Integer, String> nextAwaiting = (Map.Entry)awaitingReader.next();
            if (!this.context.registeredReaders().containsKey(nextAwaiting.getKey())) {
                awaitingReader.remove();
            } else {
                String hostname = (String)nextAwaiting.getValue();
                int awaitingSubtask = (Integer)nextAwaiting.getKey();
                Optional<BlockSourceSplit> nextSplit = this.splitAssigner.getNext(hostname);
                if (!nextSplit.isPresent()) {
                    break;
                }

                this.context.assignSplit((SplitT) nextSplit.get(), awaitingSubtask);
                awaitingReader.remove();
            }
        }

    }


    @Override
    public void close() throws IOException {

    }
}

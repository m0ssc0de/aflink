import org.apache.flink.util.Preconditions;

import java.io.Serializable;
import java.time.Duration;

public class ContinuousEnumerationSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Duration discoveryInterval;
    public ContinuousEnumerationSettings(Duration discoveryInterval) {
        this.discoveryInterval = (Duration) Preconditions.checkNotNull(discoveryInterval);
    }

    public Duration getDiscoveryInterval() { return this.discoveryInterval; }

    public String toString() {
        return "ContinuousEnumerationSettings{discoveryInterval=" + this.discoveryInterval + '}';
    }
}

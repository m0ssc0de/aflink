import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.file.src.FileSource
import org.apache.flink.connector.file.src.FileSourceSplit
import org.apache.flink.connector.file.src.reader.TextLineFormat
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import java.time.Duration


object MyFlinkApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val js = JustSource<String, FileSourceSplit>()

        val env = StreamExecutionEnvironment.getExecutionEnvironment()
        // Your Flink application logic here
        var tf = TextLineFormat()
        var source = FileSource.forRecordStreamFormat(tf, Path("/tmp/aaa")).monitorContinuously(Duration.ofMillis(5))
                .build();
        var d = env.fromSource(source, WatermarkStrategy.noWatermarks(),  "MySourceName")
        d.print("ddd")


         env.execute("My Flink App")
    }
}

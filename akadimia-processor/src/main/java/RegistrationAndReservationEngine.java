import com.akadimia.flow.pipeline.ReservationRequestPipeline;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RegistrationAndReservationEngine {

    public static void main(String[] args) {
        ReservationRequestPipeline reservationRequestPipeline
                = new ReservationRequestPipeline("localhost:9092", "akadimia", "Mathematics");

        reservationRequestPipeline.process();


    }
}

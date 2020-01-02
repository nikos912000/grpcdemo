import com.nkatirtzis.grpcdemo.Reviews;
import io.grpc.Status;

public final class GrpcSyncResponse {
    private final Status status;
    private final Reviews.Response body;

    public GrpcSyncResponse(Status status, Reviews.Response body) {
        this.status = status;
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public Reviews.Response getBody() {
        return body;
    }
}

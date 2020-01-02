import com.google.common.util.concurrent.ListenableFuture;
import com.nkatirtzis.grpcdemo.Reviews;
import io.grpc.Status;

public final class GrpcAsyncResponse {
    private final Status status;
    private final ListenableFuture<Reviews.Response> body;

    public GrpcAsyncResponse(final Status status, final ListenableFuture<Reviews.Response> body) {
        this.status = status;
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public ListenableFuture<Reviews.Response> getBody() {
        return body;
    }
}

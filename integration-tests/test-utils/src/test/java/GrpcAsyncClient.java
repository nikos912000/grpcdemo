import static io.grpc.Status.DEADLINE_EXCEEDED;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.UNKNOWN;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.google.common.util.concurrent.ListenableFuture;
import com.nkatirtzis.grpcdemo.Reviews;
import com.nkatirtzis.grpcdemo.ReviewsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class GrpcAsyncClient {
    private ReviewsServiceGrpc.ReviewsServiceFutureStub futureStub;
    private final ManagedChannel channel;

    public GrpcAsyncClient(String host, int port, long deadlineMs, Map<String, String> headers, SimpleMeterRegistry meterRegistry) {
        var grpcTracing = GrpcTracing.create(Tracing.newBuilder().build());

        channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(grpcTracing.newClientInterceptor(), new GrpcClientMonitoringInterceptor(meterRegistry))
            .build();
        futureStub = ReviewsServiceGrpc.newFutureStub(channel).withDeadlineAfter(deadlineMs, TimeUnit.SECONDS);

        // add headers
        Metadata metadata = GrpcUtils.getMetadata(headers);

        futureStub = MetadataUtils.attachHeaders(futureStub, metadata);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public GrpcAsyncResponse getReviews(int id) {
        var request = Reviews.Request.newBuilder()
            .setId(id)
            .build();

        ListenableFuture<Reviews.Response> body = null;
        Status status;
        try {
            body = futureStub.getReviews(request);
            status = Status.OK;
        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
            case NOT_FOUND:
                status = NOT_FOUND;
                break;
            case INTERNAL:
                status = INTERNAL;
                break;
            case DEADLINE_EXCEEDED:
                status = DEADLINE_EXCEEDED;
                break;
            default:
                status = UNKNOWN;
            }
        }
        return new GrpcAsyncResponse(status, body);
    }
}


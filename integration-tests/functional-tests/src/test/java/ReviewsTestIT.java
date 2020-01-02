import static java.lang.Thread.sleep;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.nkatirtzis.grpcdemo.HttpHeaders.REQUEST_ID;
import static com.nkatirtzis.grpcdemo.HttpHeaders.SESSION_ID;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.nkatirtzis.grpcdemo.Application;
import com.nkatirtzis.grpcdemo.Reviews;
import io.grpc.Status;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@ContextConfiguration(initializers = {ReviewsTestIT.Initializer.class})
class ReviewsTestIT {
    @Autowired
    private SimpleMeterRegistry meterRegistry;
    @LocalServerPort
    int port;

    private static final String grpcServerHost = "localhost";
    private static final int grpcServerPort = 6565;
    private static final long rpcDeadlineMs = 3000;

    private static final int REVIEW_ID = 123;
    private static final String REVIEW_RESPONSE = "review 123 text";
    private static final String SESSIONID = "123-456";
    private static final String REQUESTID = "456-789";
    private static final Map headers = Map.of(SESSION_ID.getHeader(), SESSIONID, REQUEST_ID.getHeader(), REQUESTID);

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(
            ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "grpc.port=6565",
                "spring.zipkin.enabled=false"
            ).applyTo(configurableApplicationContext);
        }
    }

    @Test
    void syncClientShouldReturnReview() throws InterruptedException {
        GrpcSyncClient grpcClient = new GrpcSyncClient(grpcServerHost, grpcServerPort, rpcDeadlineMs, headers, meterRegistry);
        GrpcSyncResponse grpcResponse = grpcClient.getReviews(REVIEW_ID);

        assertEquals(Status.OK, grpcResponse.getStatus());
        Assertions.assertEquals(REVIEW_RESPONSE, grpcResponse.getBody().getText());
        grpcClient.shutdown();
        sleep(5000000);
    }

    @Test
    void asyncClientShouldReturnReview() throws ExecutionException, InterruptedException {
        GrpcAsyncClient grpcClient = new GrpcAsyncClient(grpcServerHost, grpcServerPort, rpcDeadlineMs, headers, meterRegistry);
        GrpcAsyncResponse grpcResponse = grpcClient.getReviews(REVIEW_ID);

        assertEquals(Status.OK, grpcResponse.getStatus());

        ListenableFuture<Reviews.Response> future = grpcResponse.getBody();
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(Reviews.Response result) {
                Assertions.assertEquals(REVIEW_RESPONSE, result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, MoreExecutors.directExecutor());

        future.get();
        grpcClient.shutdown();
    }
}

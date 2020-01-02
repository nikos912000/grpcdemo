import java.util.concurrent.TimeUnit;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Interceptor that captures gRPC metrics.
 */
public class GrpcClientMonitoringInterceptor implements ClientInterceptor {
    private final Timer timer;
    private final Counter successCounter;
    private final Counter failedCounter;

    public GrpcClientMonitoringInterceptor(SimpleMeterRegistry meterRegistry) {
        timer = meterRegistry.timer("grpc.overall");
        successCounter = meterRegistry.counter("grpc.success");
        failedCounter = meterRegistry.counter("grpc.failed");
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> method, final CallOptions callOptions, final Channel next) {

        long start = System.currentTimeMillis();
        final ClientCall<ReqT, RespT> call = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                delegate().start(
                    new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        @Override
                        public void onClose(io.grpc.Status status, Metadata trailers) {
                            timer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                            if (status.isOk()) {
                                successCounter.increment();
                            } else {
                                failedCounter.increment();
                            }

                            super.onClose(status, trailers);
                        }
                    },
                    headers);
            }
        };
    }
}

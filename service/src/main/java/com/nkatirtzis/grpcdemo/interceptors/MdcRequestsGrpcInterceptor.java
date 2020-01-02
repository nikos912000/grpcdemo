package com.nkatirtzis.grpcdemo.interceptors;

import javax.inject.Inject;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import brave.SpanCustomizer;
import com.nkatirtzis.grpcdemo.HttpHeaders;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Implementation of {@link ServerInterceptor} that sets the request id and user agent on a thread local for logging purposes.
 */
@Component
public class MdcRequestsGrpcInterceptor implements ServerInterceptor {
    private final SpanCustomizer spanCustomizer;

    @Inject
    public MdcRequestsGrpcInterceptor(SpanCustomizer spanCustomizer) {
        this.spanCustomizer = spanCustomizer;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String requestId = metadata.get(Metadata.Key.of(HttpHeaders.REQUEST_ID.getHeader(), Metadata.ASCII_STRING_MARSHALLER));
        String sessionId = metadata.get(Metadata.Key.of(HttpHeaders.SESSION_ID.getHeader(), Metadata.ASCII_STRING_MARSHALLER));
        String userAgent = metadata.get(Metadata.Key.of(HttpHeaders.USER_AGENT.getHeader(), Metadata.ASCII_STRING_MARSHALLER));

        InterceptorsUtils.populateMdc(spanCustomizer, requestId, sessionId, userAgent);

        ServerCall.Listener<ReqT> delegate = serverCallHandler.startCall(serverCall, metadata);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {
            @Override
            public void onComplete() {
                try {
                    super.onComplete();
                } finally {
                    MDC.clear();
                }
            }
        };
    }
}

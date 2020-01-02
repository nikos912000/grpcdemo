package com.nkatirtzis.grpcdemo.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Implementation of {@link ServerInterceptor} that provides access log functionality.
 */
@Component
public class LoggingGrpcInterceptor implements ServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingGrpcInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
        Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
        Context context = Context.current();
        long start = System.currentTimeMillis();
        ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                super.close(status, trailers);
                LOGGER.info("method=" + call.getMethodDescriptor().getFullMethodName()
                    + " status=" + status.getCode()
                    + " responseTime=" + (System.currentTimeMillis() - start)
                    + " logType=access");
            }
        };

        return Contexts.interceptCall(context, wrappedCall, metadata, next);
    }
}

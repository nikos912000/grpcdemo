package com.nkatirtzis.grpcdemo.interceptors;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * Implementation of {@link ServerInterceptor} that translates and augments gRPC exceptions.
 */
@Component
public class GrpcExceptionInterceptor implements ServerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcExceptionInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
        Metadata metadata, ServerCallHandler<ReqT, RespT> next) {
        ServerCall.Listener<ReqT> delegate = next.startCall(call, metadata);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Exception e) {
                    call.close(exceptionToStatusCode(e), new Metadata());
                }
            }
        };
    }

    private Status exceptionToStatusCode(Exception e) {
        if (e instanceof HttpClientErrorException) {
            LOGGER.warn("operation=handleBadRequests, error={}", e.getMessage());

            return Status.INTERNAL.withCause(e).augmentDescription("Bad Request: " + e.getMessage());
        }
        if (e instanceof ClientAbortException) {
            LOGGER.warn("operation=handleClientExceptions, error={}", e.getMessage());

            return Status.DEADLINE_EXCEEDED.withCause(e).augmentDescription("Remote client aborted: " + e.getMessage());
        }

        LOGGER.error("operation=handleOthers", e);
        return Status.UNKNOWN.withCause(e).augmentDescription("Error occurred while calling gRPC service:" + e.getMessage());
    }
}

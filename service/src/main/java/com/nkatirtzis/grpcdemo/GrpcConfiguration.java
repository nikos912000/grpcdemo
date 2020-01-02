package com.nkatirtzis.grpcdemo;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.nkatirtzis.grpcdemo.interceptors.GrpcExceptionInterceptor;
import com.nkatirtzis.grpcdemo.interceptors.LoggingGrpcInterceptor;
import com.nkatirtzis.grpcdemo.interceptors.MdcRequestsGrpcInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;

/**
 * gRPC Spring configuration.
 */
@Configuration
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcConfiguration.class);

    @Autowired
    private Tracing tracing;
    @Autowired
    private GrpcServerProperties grpcServerProperties;
    @Autowired
    private ReviewsService reviewsService;
    @Autowired
    private GrpcExceptionInterceptor exceptionInterceptor;
    @Autowired
    private MdcRequestsGrpcInterceptor mdcRequestsInterceptor;
    @Autowired
    private LoggingGrpcInterceptor loggingGrpcInterceptor;

    @Bean
    public Server grpcServerRunner(HealthStatusManager healthStatusManager) throws IOException {
        var grpcTracing = GrpcTracing.create(tracing);

        var serverBuilder = ServerBuilder.forPort(grpcServerProperties.getPort());

        serverBuilder.addService(healthStatusManager.getHealthService());
        healthStatusManager.setStatus("grpcdemo", HealthCheckResponse.ServingStatus.SERVING);
        serverBuilder.addService(ServerInterceptors.intercept(reviewsService, grpcTracing.newServerInterceptor(), exceptionInterceptor,
            mdcRequestsInterceptor, loggingGrpcInterceptor));
        serverBuilder.addService(ProtoReflectionService.newInstance());

        Server server = serverBuilder.build().start();
        LOGGER.info("gRPC server started, listening on " + grpcServerProperties.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            LOGGER.info("gRPC server shut down");
        }));

        return server;
    }

    @Bean
    public HealthStatusManager healthStatusManager() {
        return new HealthStatusManager();
    }
}

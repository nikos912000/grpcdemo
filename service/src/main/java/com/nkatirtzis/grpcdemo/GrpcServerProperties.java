package com.nkatirtzis.grpcdemo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for the gRPC server.
 */
@ConfigurationProperties("grpc")
public class GrpcServerProperties {
    private int port;

    private boolean enabled;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

package com.nkatirtzis.grpcdemo;

/**
 * Enum that represents http header names and their keys.
 */
public enum HttpHeaders {
    SESSION_ID("x-session-id"),
    REQUEST_ID("x-request-id"),
    USER_AGENT("user-agent");

    private final String header;

    HttpHeaders(final String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}

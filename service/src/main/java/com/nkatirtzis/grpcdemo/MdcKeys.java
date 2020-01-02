package com.nkatirtzis.grpcdemo;

/**
 * Enum that represents mdc keys used by this application.
 */
public enum MdcKeys {

    SESSION_ID("sessionId"),
    REQUEST_ID("requestId"),
    USER_AGENT("userAgent");

    private final String mdcKey;

    MdcKeys(final String mdcKey) {
        this.mdcKey = mdcKey;
    }

    public String getMdcKey() {
        return mdcKey;
    }
}

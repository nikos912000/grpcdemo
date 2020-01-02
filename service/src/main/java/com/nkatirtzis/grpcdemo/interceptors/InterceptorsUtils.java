package com.nkatirtzis.grpcdemo.interceptors;

import java.util.UUID;

import org.slf4j.MDC;

import brave.SpanCustomizer;
import com.nkatirtzis.grpcdemo.MdcKeys;

/**
 * Util class with logic shared between interceptors.
 */
public final class InterceptorsUtils {
    private InterceptorsUtils() {
    }

    public static void populateMdc(SpanCustomizer spanCustomizer, String requestId, String sessionId, String userAgent) {
        MDC.put(MdcKeys.USER_AGENT.getMdcKey(), userAgent);
        MDC.put(MdcKeys.SESSION_ID.getMdcKey(), sessionId);
        MDC.put(MdcKeys.REQUEST_ID.getMdcKey(), requestId != null && !requestId.isEmpty() ? requestId : UUID.randomUUID().toString());

        if (requestId != null) {
            spanCustomizer.tag("requestId", requestId);
        }
    }
}

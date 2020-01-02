package com.nkatirtzis.grpcdemo.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.util.StringUtils.isEmpty;

import static com.nkatirtzis.grpcdemo.interceptors.InterceptorsUtils.populateMdc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

import brave.SpanCustomizer;
import com.nkatirtzis.grpcdemo.MdcKeys;

class InterceptorsUtilsTest {
    private static final String REQUEST_ID = "123-456";
    private static final String USER_AGENT = "SomeApp/dev app/dev";
    private static final String SESSION_ID = "111-222";

    @Mock
    private SpanCustomizer spanCustomizer;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void shouldSetRequestIdBasedOnHeaders() {
        // check we added the tag
        populateMdc(spanCustomizer, REQUEST_ID, null, null);
        assertEquals(MDC.get(MdcKeys.REQUEST_ID.getMdcKey()), "123-456");
    }

    @Test
    public void shouldSetUserAgentBasedOnHeaders() {
        // check we added the tag
        populateMdc(spanCustomizer, null, null, USER_AGENT);
        assertEquals(MDC.get(MdcKeys.USER_AGENT.getMdcKey()), "SomeApp/dev app/dev");
    }

    @Test
    public void shouldSetSessionIdBasedOnHeaders() {
        // check we added the tag
        populateMdc(spanCustomizer, null, SESSION_ID, null);
        assertEquals(MDC.get(MdcKeys.SESSION_ID.getMdcKey()), "111-222");
    }

    @Test
    public void shouldGenerateRandomUUIDWhenHeaderFieldIsMissing() {
        // check we added the tag
        populateMdc(spanCustomizer, null, null, null);
        assertFalse(isEmpty(MDC.get(MdcKeys.REQUEST_ID.getMdcKey())));
    }
}

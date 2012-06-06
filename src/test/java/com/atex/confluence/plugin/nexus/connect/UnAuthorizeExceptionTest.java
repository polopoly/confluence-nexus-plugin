package com.atex.confluence.plugin.nexus.connect;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnAuthorizeExceptionTest {

    UnAuthorizeException target;

    @Test
    public void testExceptionWithNoMessage() {
        target = new UnAuthorizeException();
        assertNull(target.getMessage());
    }

    @Test
    public void testExceptionWithMessage() {
        target = new UnAuthorizeException("UnAuthorize exception message");
        assertEquals("UnAuthorize exception message", target.getMessage());
    }

    @Test
    public void testExceptionWithCause() {
        Throwable cause = new Throwable();
        target = new UnAuthorizeException(cause);
        assertEquals(cause, target.getCause());
    }

    @Test
    public void testExceptionWithMesssageCause() {
        Throwable cause = new Throwable();
        target = new UnAuthorizeException("UnAuthorize exception message", cause);
        assertEquals("UnAuthorize exception message", target.getMessage());
        assertEquals(cause, target.getCause());
    }
}

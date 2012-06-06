package com.atex.confluence.plugin.nexus.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AddressNotFoundExceptionTest {

    AddressNotFoundException target;

    @Test
    public void testExceptionWithNoMessage() {
        target = new AddressNotFoundException();
        assertNull(target.getMessage());
    }

    @Test
    public void testExceptionWithMessage() {
        target = new AddressNotFoundException("Address not found message");
        assertEquals("Address not found message", target.getMessage());
    }

    @Test
    public void testExceptionWithCause() {
        Throwable cause = new Throwable();
        target = new AddressNotFoundException(cause);
        assertEquals(cause, target.getCause());
    }

    @Test
    public void testExceptionWithMessageCause() {
        Throwable cause = new Throwable();
        target = new AddressNotFoundException("Address not found message", cause);
        assertEquals(cause, target.getCause());
        assertEquals("Address not found message", target.getMessage());
    }

}

package com.atex.confluence.plugin.nexus.connect;

import java.io.IOException;

public class AddressNotFoundException extends IOException {

    private static final long serialVersionUID = -8512032111048418598L;

    public AddressNotFoundException() {
        super();
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Throwable cause) {
        super(cause);
    }
    

}

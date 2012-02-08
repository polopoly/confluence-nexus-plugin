package com.atex.confluence.plugin.nexus;

import java.io.IOException;

public class UnAuthorizeException extends IOException{

    /**
     * 
     */
    private static final long serialVersionUID = -4132321923794859405L;

    public UnAuthorizeException() {
        super();
    }

    public UnAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnAuthorizeException(String message) {
        super(message);
    }

    public UnAuthorizeException(Throwable cause) {
        super(cause);
    }
    
    

}

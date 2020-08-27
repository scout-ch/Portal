package ch.itds.pbs.portal.exception;


import org.springframework.security.access.AccessDeniedException;

public class InvalidTileApiKeyException extends AccessDeniedException {
    public InvalidTileApiKeyException(String msg) {
        super(msg);
    }

    public InvalidTileApiKeyException(String msg, Throwable t) {
        super(msg, t);
    }
}

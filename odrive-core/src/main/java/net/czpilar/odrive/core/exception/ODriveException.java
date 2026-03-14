package net.czpilar.odrive.core.exception;

/**
 * Base exception for all oDrive exceptions.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class ODriveException extends RuntimeException {

    public ODriveException(String message) {
        super(message);
    }

    public ODriveException(String message, Throwable cause) {
        super(message, cause);
    }
}

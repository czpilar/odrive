package net.czpilar.odrive.core.exception;

/**
 * Exception thrown by OneDrive client operations.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class OneDriveClientException extends ODriveException {

    public OneDriveClientException(String message) {
        super(message);
    }

    public OneDriveClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

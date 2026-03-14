package net.czpilar.odrive.core.exception;

/**
 * Exception used when authorization fails.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class AuthorizationFailedException extends ODriveException {

    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

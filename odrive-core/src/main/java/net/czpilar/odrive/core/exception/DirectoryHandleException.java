package net.czpilar.odrive.core.exception;

/**
 * Exception used when directory handling fails.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class DirectoryHandleException extends ODriveException {

    public DirectoryHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}

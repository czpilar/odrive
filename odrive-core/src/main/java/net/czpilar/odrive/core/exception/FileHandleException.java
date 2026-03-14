package net.czpilar.odrive.core.exception;

/**
 * Exception used when file handling fails.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class FileHandleException extends ODriveException {

    public FileHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}

package net.czpilar.odrive.core.exception;

/**
 * Exception used when no credential found.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class NoCredentialFoundException extends ODriveException {

    public NoCredentialFoundException(String message) {
        super(message);
    }
}

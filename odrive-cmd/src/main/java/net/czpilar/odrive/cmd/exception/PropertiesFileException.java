package net.czpilar.odrive.cmd.exception;

import net.czpilar.odrive.core.exception.ODriveException;

/**
 * Exception used for error during work with properties file.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class PropertiesFileException extends ODriveException {

    public PropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

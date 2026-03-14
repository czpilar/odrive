package net.czpilar.odrive.cmd.exception;

import net.czpilar.odrive.core.exception.ODriveException;

/**
 * Exception used for error during work with command line.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class CommandLineException extends ODriveException {

    public CommandLineException(String message) {
        super(message);
    }

    public CommandLineException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public CommandLineException(String message, Throwable cause) {
        super(message, cause);
    }
}

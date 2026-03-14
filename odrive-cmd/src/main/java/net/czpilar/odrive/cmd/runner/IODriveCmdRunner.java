package net.czpilar.odrive.cmd.runner;

/**
 * Command line runner interface.
 *
 * @author David Pilar (david@czpilar.net)
 */
public interface IODriveCmdRunner {

    /**
     * Runs command line oDrive.
     *
     * @param args arguments
     */
    void run(String[] args);
}

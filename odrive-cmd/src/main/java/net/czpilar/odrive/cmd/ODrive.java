package net.czpilar.odrive.cmd;

import net.czpilar.odrive.cmd.context.ODriveCmdContext;
import net.czpilar.odrive.cmd.runner.IODriveCmdRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main class for running oDrive from command line.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class ODrive {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ODriveCmdContext.class)
                .getBean(IODriveCmdRunner.class)
                .run(args);
    }
}

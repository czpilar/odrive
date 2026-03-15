package net.czpilar.odrive.cmd;

import net.czpilar.odrive.cmd.context.ODriveCmdContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author David Pilar (david@czpilar.net)
 */
@ExtendWith(SpringExtension.class)
@Import(ODriveCmdContext.class)
class ApplicationContextTest {

    @Test
    void test() {
    }
}

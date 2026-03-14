package net.czpilar.odrive.cmd;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author David Pilar (david@czpilar.net)
 */
@Disabled("Integration test for ODrive command line application. Requires valid credentials and network connection.")
public class ODriveIntegrationTest {

    private static final String PROPERTIES = "odrive.properties";

    @Test
    public void testNoArgs() {
        ODrive.main(new String[]{});
    }

    @Test
    public void testHelp() {
        ODrive.main(new String[]{"-h"});
    }

    @Test
    public void testVersion() {
        ODrive.main(new String[]{"-v"});
    }

    @Test
    public void testProperties() {
        ODrive.main(new String[]{"-p", PROPERTIES});
    }

    @Test
    public void testShowAuthLink() {
        ODrive.main(new String[]{"-l", "-p", PROPERTIES});
    }

    @Test
    public void testAuthorize() {
        ODrive.main(new String[]{"-a", "auth_code", "-p", PROPERTIES});
    }

    @Test
    public void testShowAuthLinkAndAuthorize() {
        ODrive.main(new String[]{"-l", "-a", "-p", PROPERTIES});
    }

    private void createFileIfNotExist(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            FileUtils.writeStringToFile(file, "This is a testing file with filename: " + filename, Charset.defaultCharset());
        }
    }

    @Test
    public void testUploadFiles() throws IOException {
        String filename1 = "target/test1.txt";
        String filename2 = "target/test2.txt";
        String filename3 = "target/test3.txt";
        createFileIfNotExist(filename1);
        createFileIfNotExist(filename2);
        createFileIfNotExist(filename3);
        ODrive.main(new String[]{"-f", filename1, filename2, filename3, "-d", "odrive-test-backup", "-p", PROPERTIES});
    }

    @Test
    public void testUploadFileToSubdirectory() throws IOException {
        String filename = "target/test1.txt";
        createFileIfNotExist(filename);
        ODrive.main(new String[]{"-f", filename, "-d", "odrive-test-backup/odrive-subdir/odrive-last-dir", "-p", PROPERTIES});
    }
}

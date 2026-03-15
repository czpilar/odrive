package net.czpilar.odrive.cmd;

import net.czpilar.odrive.core.request.FileRequest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

/**
 * @author David Pilar (david@czpilar.net)
 */
class ODriveIntegrationTest {

    private static final String PROPERTIES = "odrive.properties";

    @Test
    void testNoArgs() {
        ODrive.main(new String[]{});
    }

    @Test
    void testHelp() {
        ODrive.main(new String[]{"-h"});
    }

    @Test
    void testVersion() {
        ODrive.main(new String[]{"-v"});
    }

    @Test
    void testProperties() {
        ODrive.main(new String[]{"-p", PROPERTIES});
    }

    @Test
    void testShowAuthLink() {
        ODrive.main(new String[]{"-l", "-p", PROPERTIES});
    }

    @Test
    void testAuthorize() {
        ODrive.main(new String[]{"-a", "auth_code", "-p", PROPERTIES});
    }

    @Test
    void testShowAuthLinkAndAuthorize() {
        ODrive.main(new String[]{"-l", "-a", "-p", PROPERTIES});
    }

    private void createFileIfNotExist(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            FileUtils.writeStringToFile(file, "This is a testing file with filename: " + filename, Charset.defaultCharset());
        }
    }

    private void createLargeFileIfNotExist(String filename, long size) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(size);
            }
        }
    }

    @Test
    void testUploadFiles() throws IOException {
        String filename1 = "target/test1.txt";
        String filename2 = "target/test2.txt";
        String filename3 = "target/test3.txt";
        createFileIfNotExist(filename1);
        createFileIfNotExist(filename2);
        createFileIfNotExist(filename3);
        ODrive.main(new String[]{"-f", filename1, filename2, filename3, "-d", "odrive-test-backup", "-p", PROPERTIES});
    }

    @Test
    void testUploadFileToSubdirectory() throws IOException {
        String filename = "target/test1.txt";
        createFileIfNotExist(filename);
        ODrive.main(new String[]{"-f", filename, "-d", "odrive-test-backup/odrive-subdir/odrive-last-dir", "-p", PROPERTIES});
    }

    @Test
    void testUploadLargeFile() throws IOException {
        String filename = "target/test-large-file.bin";
        createLargeFileIfNotExist(filename, FileRequest.SMALL_FILE_LIMIT + 1);
        ODrive.main(new String[]{"-f", filename, "-d", "odrive-test-backup", "-p", PROPERTIES});
    }

    @Test
    void testUploadLargeFileMultipleChunks() throws IOException {
        String filename = "target/test-large-file-multi-chunk.bin";
        createLargeFileIfNotExist(filename, FileRequest.CHUNK_SIZE * 3L + 1);
        ODrive.main(new String[]{"-f", filename, "-d", "odrive-test-backup", "-p", PROPERTIES});
    }

    @Test
    void testUploadSmallAndLargeFiles() throws IOException {
        String smallFilename = "target/test-small.txt";
        String largeFilename = "target/test-large-file-mixed.bin";
        createFileIfNotExist(smallFilename);
        createLargeFileIfNotExist(largeFilename, FileRequest.SMALL_FILE_LIMIT + 1);
        ODrive.main(new String[]{"-f", smallFilename, largeFilename, "-d", "odrive-test-backup", "-p", PROPERTIES});
    }
}

package net.czpilar.odrive.cmd.credential;

import net.czpilar.odrive.cmd.context.ODriveCmdContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertiesODriveCredentialTest {

    private PropertiesODriveCredential oDrivePropertiesNotExist;
    private PropertiesODriveCredential oDrivePropertiesExist;

    private File propertiesNotExist;
    private File propertiesExist;

    @BeforeEach
    void before() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        propertiesNotExist = new File(tempDir + "test-properties-not-exist-file-" + System.currentTimeMillis() + ".properties");
        propertiesExist = new File(tempDir + "test-properties-exist-file-" + System.currentTimeMillis() + ".properties");
        deleteIfExist(propertiesNotExist);
        deleteIfExist(propertiesExist);

        Properties properties = new Properties();
        properties.setProperty(ODriveCmdContext.UPLOAD_DIR_PROPERTY_KEY, "test-upload-dir");
        properties.setProperty(ODriveCmdContext.REFRESH_TOKEN_PROPERTY_KEY, "test-refresh-token");
        try (FileOutputStream out = new FileOutputStream(propertiesExist)) {
            properties.store(out, "properties created in test");
        }

        oDrivePropertiesNotExist = createODriveCredential(propertiesNotExist.getPath());
        oDrivePropertiesExist = createODriveCredential(propertiesExist.getPath());
    }

    @AfterEach
    void after() throws IOException {
        deleteIfExist(propertiesNotExist);
        deleteIfExist(propertiesExist);
    }

    private PropertiesODriveCredential createODriveCredential(String propertyFile) {
        PropertiesODriveCredential oDriveCredential = new PropertiesODriveCredential(
                ODriveCmdContext.UPLOAD_DIR_PROPERTY_KEY,
                ODriveCmdContext.REFRESH_TOKEN_PROPERTY_KEY, ODriveCmdContext.DEFAULT_UPLOAD_DIR);
        oDriveCredential.setPropertyFile(propertyFile);
        return oDriveCredential;
    }

    private void deleteIfExist(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
    }

    @Test
    void testGetRefreshTokenWherePropertiesExist() {
        assertEquals("test-refresh-token", oDrivePropertiesExist.getRefreshToken());
    }

    @Test
    void testGetRefreshTokenWherePropertiesNotExist() {
        assertNull(oDrivePropertiesNotExist.getRefreshToken());
    }

    @Test
    void testSaveRefreshToken() {
        oDrivePropertiesNotExist.saveRefreshToken("new-refresh-token-to-save");

        PropertiesODriveCredential oDrivePropertiesInTest = createODriveCredential(propertiesNotExist.getPath());

        assertEquals("new-refresh-token-to-save", oDrivePropertiesInTest.getRefreshToken());
    }

    @Test
    void testSaveRefreshTokenDoesNotSaveNull() {
        oDrivePropertiesNotExist.saveRefreshToken(null);

        PropertiesODriveCredential oDrivePropertiesInTest = createODriveCredential(propertiesNotExist.getPath());

        assertNull(oDrivePropertiesInTest.getRefreshToken());
    }

    @Test
    void testGetUploadDirWherePropertiesNotExist() {
        assertEquals(ODriveCmdContext.DEFAULT_UPLOAD_DIR, oDrivePropertiesNotExist.getUploadDir());
    }

    @Test
    void testGetUploadDirWherePropertiesExist() {
        assertEquals("test-upload-dir", oDrivePropertiesExist.getUploadDir());
    }

    @Test
    void testSetUploadDir() {
        oDrivePropertiesNotExist.setUploadDir("new-upload-dir");
        assertEquals("new-upload-dir", oDrivePropertiesNotExist.getUploadDir());
    }
}

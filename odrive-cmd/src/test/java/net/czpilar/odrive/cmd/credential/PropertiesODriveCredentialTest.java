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

public class PropertiesODriveCredentialTest {

    private PropertiesODriveCredential oDrivePropertiesNotExist;
    private PropertiesODriveCredential oDrivePropertiesExist;

    private File propertiesNotExist;
    private File propertiesExist;

    @BeforeEach
    public void before() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        propertiesNotExist = new File(tempDir + "test-properties-not-exist-file-" + System.currentTimeMillis() + ".properties");
        propertiesExist = new File(tempDir + "test-properties-exist-file-" + System.currentTimeMillis() + ".properties");
        deleteIfExist(propertiesNotExist);
        deleteIfExist(propertiesExist);

        Properties properties = new Properties();
        properties.setProperty(ODriveCmdContext.UPLOAD_DIR_PROPERTY_KEY, "test-upload-dir");
        properties.setProperty(ODriveCmdContext.ACCESS_TOKEN_PROPERTY_KEY, "test-access-token");
        properties.setProperty(ODriveCmdContext.REFRESH_TOKEN_PROPERTY_KEY, "test-refresh-token");
        try (FileOutputStream out = new FileOutputStream(propertiesExist)) {
            properties.store(out, "properties created in test");
        }

        oDrivePropertiesNotExist = createODriveCredential(propertiesNotExist.getPath());
        oDrivePropertiesExist = createODriveCredential(propertiesExist.getPath());
    }

    @AfterEach
    public void after() throws IOException {
        deleteIfExist(propertiesNotExist);
        deleteIfExist(propertiesExist);
    }

    private PropertiesODriveCredential createODriveCredential(String propertyFile) {
        PropertiesODriveCredential oDriveCredential = new PropertiesODriveCredential(
                ODriveCmdContext.UPLOAD_DIR_PROPERTY_KEY, ODriveCmdContext.ACCESS_TOKEN_PROPERTY_KEY,
                ODriveCmdContext.REFRESH_TOKEN_PROPERTY_KEY, ODriveCmdContext.DEFAULT_UPLOAD_DIR);
        oDriveCredential.setPropertyFile(propertyFile);
        return oDriveCredential;
    }

    private void deleteIfExist(File file) throws IOException {
        Files.deleteIfExists(file.toPath());
    }

    @Test
    public void testGetAccessTokenWherePropertiesExist() {
        assertEquals("test-access-token", oDrivePropertiesExist.getAccessToken());
    }

    @Test
    public void testSetAccessToken() {
        oDrivePropertiesNotExist.setAccessToken("new-access-token");
        assertEquals("new-access-token", oDrivePropertiesNotExist.getAccessToken());
    }

    @Test
    public void testGetRefreshTokenWherePropertiesExist() {
        assertEquals("test-refresh-token", oDrivePropertiesExist.getRefreshToken());
    }

    @Test
    public void testSetRefreshToken() {
        oDrivePropertiesNotExist.setRefreshToken("new-refresh-token");
        assertEquals("new-refresh-token", oDrivePropertiesNotExist.getRefreshToken());
    }

    @Test
    public void testSaveTokens() {
        oDrivePropertiesNotExist.saveTokens("new-access-token-to-save", "new-refresh-token-to-save");

        PropertiesODriveCredential oDrivePropertiesInTest = createODriveCredential(propertiesNotExist.getPath());

        assertEquals("new-access-token-to-save", oDrivePropertiesInTest.getAccessToken());
        assertEquals("new-refresh-token-to-save", oDrivePropertiesInTest.getRefreshToken());
    }

    @Test
    public void testGetUploadDirWherePropertiesNotExist() {
        assertEquals(ODriveCmdContext.DEFAULT_UPLOAD_DIR, oDrivePropertiesNotExist.getUploadDir());
    }

    @Test
    public void testGetUploadDirWherePropertiesExist() {
        assertEquals("test-upload-dir", oDrivePropertiesExist.getUploadDir());
    }

    @Test
    public void testSetUploadDir() {
        oDrivePropertiesNotExist.setUploadDir("new-upload-dir");
        assertEquals("new-upload-dir", oDrivePropertiesNotExist.getUploadDir());
    }
}

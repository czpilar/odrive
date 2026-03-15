package net.czpilar.odrive.core.credential.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleODriveCredentialTest {

    private SimpleODriveCredential credential;

    @BeforeEach
    public void before() {
        credential = new SimpleODriveCredential();
    }

    @Test
    public void testGetRefreshTokenReturnsNull() {
        assertNull(credential.getRefreshToken());
    }

    @Test
    public void testSaveAndGetRefreshToken() {
        credential.saveRefreshToken("test-refresh-token");

        assertEquals("test-refresh-token", credential.getRefreshToken());
    }

    @Test
    public void testSetAndGetUploadDir() {
        assertNull(credential.getUploadDir());

        String uploadDir = "test-upload-dir";
        credential.setUploadDir(uploadDir);

        assertEquals(uploadDir, credential.getUploadDir());
    }
}

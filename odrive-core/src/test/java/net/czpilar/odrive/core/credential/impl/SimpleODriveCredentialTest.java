package net.czpilar.odrive.core.credential.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SimpleODriveCredentialTest {

    private SimpleODriveCredential credential;

    @BeforeEach
    void before() {
        credential = new SimpleODriveCredential();
    }

    @Test
    void testGetRefreshTokenReturnsNull() {
        assertNull(credential.getRefreshToken());
    }

    @Test
    void testSaveAndGetRefreshToken() {
        credential.saveRefreshToken("test-refresh-token");

        assertEquals("test-refresh-token", credential.getRefreshToken());
    }

    @Test
    void testSetAndGetUploadDir() {
        assertNull(credential.getUploadDir());

        String uploadDir = "test-upload-dir";
        credential.setUploadDir(uploadDir);

        assertEquals(uploadDir, credential.getUploadDir());
    }
}

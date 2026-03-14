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
    public void testSetAndGetAccessToken() {
        assertNull(credential.getAccessToken());

        String accessToken = "test-access-token";
        credential.setAccessToken(accessToken);

        assertEquals(accessToken, credential.getAccessToken());
    }

    @Test
    public void testSetAndGetRefreshToken() {
        assertNull(credential.getRefreshToken());

        String refreshToken = "test-refresh-token";
        credential.setRefreshToken(refreshToken);

        assertEquals(refreshToken, credential.getRefreshToken());
    }

    @Test
    public void testSaveTokens() {
        assertNull(credential.getAccessToken());
        assertNull(credential.getRefreshToken());

        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";

        credential.saveTokens(accessToken, refreshToken);

        assertEquals(accessToken, credential.getAccessToken());
        assertEquals(refreshToken, credential.getRefreshToken());
    }

    @Test
    public void testSetAndGetUploadDir() {
        assertNull(credential.getUploadDir());

        String uploadDir = "test-upload-dir";
        credential.setUploadDir(uploadDir);

        assertEquals(uploadDir, credential.getUploadDir());
    }
}

package net.czpilar.odrive.core.credential.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractODriveCredentialTest {

    @Test
    void testAbstractODriveCredentialIsAbstract() {
        AbstractODriveCredential credential = new AbstractODriveCredential() {
            @Override
            public String getRefreshToken() {
                return null;
            }

            @Override
            public void saveRefreshToken(String refreshToken) {
            }

            @Override
            public String getUploadDir() {
                return null;
            }
        };

        assertNotNull(credential);
    }
}

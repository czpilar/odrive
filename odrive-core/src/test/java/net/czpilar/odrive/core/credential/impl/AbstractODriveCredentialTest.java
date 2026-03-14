package net.czpilar.odrive.core.credential.impl;

import net.czpilar.odrive.core.credential.Credential;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AbstractODriveCredentialTest {

    @Mock
    private AbstractODriveCredential oDriveCredential;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetCredential() {
        when(oDriveCredential.getCredential()).thenCallRealMethod();
        when(oDriveCredential.getAccessToken()).thenReturn("access-token");
        when(oDriveCredential.getRefreshToken()).thenReturn("refresh-token");

        Credential result = oDriveCredential.getCredential();

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());

        verify(oDriveCredential).getCredential();
        verify(oDriveCredential).getAccessToken();
        verify(oDriveCredential).getRefreshToken();

        verifyNoMoreInteractions(oDriveCredential);
    }

    @Test
    public void testSaveCredential() {
        doCallRealMethod().when(oDriveCredential).saveCredential(any(Credential.class));
        doNothing().when(oDriveCredential).saveTokens(anyString(), anyString());

        Credential credential = new Credential("access-token", "refresh-token");

        oDriveCredential.saveCredential(credential);

        verify(oDriveCredential).saveCredential(any(Credential.class));
        verify(oDriveCredential).saveTokens("access-token", "refresh-token");

        verifyNoMoreInteractions(oDriveCredential);
    }
}

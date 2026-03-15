package net.czpilar.odrive.core.credential.loader;

import net.czpilar.odrive.core.credential.IODriveCredential;
import net.czpilar.odrive.core.exception.NoCredentialFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CredentialLoaderTest {

    private CredentialLoader loader;

    @Mock
    private IODriveCredential oDriveCredential;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        loader = new CredentialLoader(oDriveCredential);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testConstructorWithNullCredential() {
        assertThrows(NoCredentialFoundException.class, () -> new CredentialLoader(null));
    }

    @Test
    void testGetRefreshToken() {
        when(oDriveCredential.getRefreshToken()).thenReturn("test-refresh-token");

        String result = loader.getRefreshToken();

        assertEquals("test-refresh-token", result);

        verify(oDriveCredential).getRefreshToken();
        verifyNoMoreInteractions(oDriveCredential);
    }

    @Test
    void testGetRefreshTokenReturnsNull() {
        when(oDriveCredential.getRefreshToken()).thenReturn(null);

        String result = loader.getRefreshToken();

        assertNull(result);

        verify(oDriveCredential).getRefreshToken();
        verifyNoMoreInteractions(oDriveCredential);
    }
}

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

public class CredentialLoaderTest {

    private CredentialLoader loader;

    @Mock
    private IODriveCredential oDriveCredential;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        loader = new CredentialLoader(oDriveCredential);
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testConstructorWithNullCredential() {
        assertThrows(NoCredentialFoundException.class, () -> new CredentialLoader(null));
    }

    @Test
    public void testGetRefreshToken() {
        when(oDriveCredential.getRefreshToken()).thenReturn("test-refresh-token");

        String result = loader.getRefreshToken();

        assertEquals("test-refresh-token", result);

        verify(oDriveCredential).getRefreshToken();
        verifyNoMoreInteractions(oDriveCredential);
    }

    @Test
    public void testGetRefreshTokenReturnsNull() {
        when(oDriveCredential.getRefreshToken()).thenReturn(null);

        String result = loader.getRefreshToken();

        assertNull(result);

        verify(oDriveCredential).getRefreshToken();
        verifyNoMoreInteractions(oDriveCredential);
    }
}

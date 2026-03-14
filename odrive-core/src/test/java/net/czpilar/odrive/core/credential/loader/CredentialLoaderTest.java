package net.czpilar.odrive.core.credential.loader;

import net.czpilar.odrive.core.credential.Credential;
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

    @Mock
    private Credential credential;

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
    public void testGetCredentialWhereNoCredentialLoaded() {
        assertThrows(NoCredentialFoundException.class, () -> new CredentialLoader(null).getCredential());
    }

    @Test
    public void testGetCredential() {
        when(oDriveCredential.getCredential()).thenReturn(credential);

        Credential result = loader.getCredential();

        assertNotNull(result);
        assertEquals(credential, result);

        verify(oDriveCredential).getCredential();

        verifyNoMoreInteractions(oDriveCredential);
        verifyNoInteractions(credential);
    }
}

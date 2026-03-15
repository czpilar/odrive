package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenRefresherTest {

    @Mock
    private CredentialLoader credentialLoader;

    @Mock
    private IAuthorizationService authorizationService;

    private TokenRefresher tokenRefresher;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        tokenRefresher = new TokenRefresher(credentialLoader, authorizationService);
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetReturnsAccessToken() {
        Credential credential = new Credential("fresh-access-token", "new-refresh-token");
        when(credentialLoader.getRefreshToken()).thenReturn("my-refresh-token");
        when(authorizationService.refreshAccessToken("my-refresh-token")).thenReturn(credential);

        String result = tokenRefresher.get();

        assertEquals("fresh-access-token", result);
        verify(credentialLoader).getRefreshToken();
        verify(authorizationService).refreshAccessToken("my-refresh-token");
    }

    @Test
    public void testGetReturnsNullWhenNoRefreshToken() {
        when(credentialLoader.getRefreshToken()).thenReturn(null);

        String result = tokenRefresher.get();

        assertNull(result);
        verify(credentialLoader).getRefreshToken();
        verifyNoInteractions(authorizationService);
    }
}

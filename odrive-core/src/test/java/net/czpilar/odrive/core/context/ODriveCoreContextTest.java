package net.czpilar.odrive.core.context;

import net.czpilar.odrive.core.client.BearerAuthInterceptor;
import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ODriveCoreContextTest {

    private ODriveCoreContext context;

    @BeforeEach
    void before() {
        context = new ODriveCoreContext();
    }

    @Test
    void testOdriveClientRegistration() {
        ODriveSetting setting = new ODriveSetting("1.0.0", "test-client-id", "common", "http://127.0.0.1:8783/odrive", 8783, "/odrive");

        ClientRegistration registration = context.oDriveClientRegistration(setting);

        assertNotNull(registration);
        assertEquals("odrive-core-idp-client", registration.getRegistrationId());
        assertEquals("test-client-id", registration.getClientId());
        assertEquals(ClientAuthenticationMethod.NONE, registration.getClientAuthenticationMethod());
        assertEquals(AuthorizationGrantType.AUTHORIZATION_CODE, registration.getAuthorizationGrantType());
        assertEquals("http://127.0.0.1:8783/odrive", registration.getRedirectUri());
        assertTrue(registration.getScopes().containsAll(ODriveSetting.SCOPES));
        assertEquals("https://login.microsoftonline.com/common/oauth2/v2.0/authorize",
                registration.getProviderDetails().getAuthorizationUri());
        assertEquals("https://login.microsoftonline.com/common/oauth2/v2.0/token",
                registration.getProviderDetails().getTokenUri());
    }

    @Test
    void testOdriveClientRegistrationWithCustomTenant() {
        ODriveSetting setting = new ODriveSetting("1.0.0", "my-client", "my-tenant", "http://127.0.0.1:9999/callback", 9999, "/callback");

        ClientRegistration registration = context.oDriveClientRegistration(setting);

        assertEquals("my-client", registration.getClientId());
        assertEquals("http://127.0.0.1:9999/callback", registration.getRedirectUri());
        assertEquals("https://login.microsoftonline.com/my-tenant/oauth2/v2.0/authorize",
                registration.getProviderDetails().getAuthorizationUri());
        assertEquals("https://login.microsoftonline.com/my-tenant/oauth2/v2.0/token",
                registration.getProviderDetails().getTokenUri());
    }

    @Test
    void testAuthorizationCodeTokenResponseClient() {
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client =
                context.authorizationCodeTokenResponseClient();

        assertNotNull(client);
    }

    @Test
    void testRefreshTokenResponseClient() {
        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> client =
                context.refreshTokenResponseClient();

        assertNotNull(client);
    }

    @Test
    void testOneDriveClient() {
        BearerAuthInterceptor interceptor = mock(BearerAuthInterceptor.class);

        OneDriveClient client = context.oneDriveClient(interceptor);

        assertNotNull(client);
    }
}

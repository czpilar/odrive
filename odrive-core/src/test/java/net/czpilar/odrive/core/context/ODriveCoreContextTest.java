package net.czpilar.odrive.core.context;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.client.TokenRefresher;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ODriveCoreContextTest {

    private ODriveCoreContext context;

    @BeforeEach
    public void before() {
        context = new ODriveCoreContext();
    }

    @Test
    public void testMicrosoftClientRegistration() {
        ODriveSetting setting = new ODriveSetting("1.0.0", "test-client-id", "common", 8783, "/odrive");

        ClientRegistration registration = context.microsoftClientRegistration(setting);

        assertNotNull(registration);
        assertEquals("microsoft", registration.getRegistrationId());
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
    public void testMicrosoftClientRegistrationWithCustomTenant() {
        ODriveSetting setting = new ODriveSetting("1.0.0", "my-client", "my-tenant", 9999, "/callback");

        ClientRegistration registration = context.microsoftClientRegistration(setting);

        assertEquals("my-client", registration.getClientId());
        assertEquals("http://127.0.0.1:9999/callback", registration.getRedirectUri());
        assertEquals("https://login.microsoftonline.com/my-tenant/oauth2/v2.0/authorize",
                registration.getProviderDetails().getAuthorizationUri());
        assertEquals("https://login.microsoftonline.com/my-tenant/oauth2/v2.0/token",
                registration.getProviderDetails().getTokenUri());
    }

    @Test
    public void testAuthorizationCodeTokenResponseClient() {
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client =
                context.authorizationCodeTokenResponseClient();

        assertNotNull(client);
    }

    @Test
    public void testRefreshTokenResponseClient() {
        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> client =
                context.refreshTokenResponseClient();

        assertNotNull(client);
    }

    @Test
    public void testGraphRestTemplate() {
        RestTemplate restTemplate = context.graphRestTemplate();

        assertNotNull(restTemplate);
    }

    @Test
    public void testGraphRestTemplateHasJacksonConverter() {
        RestTemplate restTemplate = context.graphRestTemplate();

        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        boolean hasJacksonConverter = converters.stream()
                .anyMatch(c -> c instanceof JacksonJsonHttpMessageConverter);
        assertTrue(hasJacksonConverter);
    }

    @Test
    public void testGraphRestTemplateJacksonConverterIsFirst() {
        RestTemplate restTemplate = context.graphRestTemplate();

        assertInstanceOf(JacksonJsonHttpMessageConverter.class, restTemplate.getMessageConverters().getFirst());
    }

    @Test
    public void testGraphRestTemplateHasExactlyOneJacksonConverter() {
        RestTemplate restTemplate = context.graphRestTemplate();

        long count = restTemplate.getMessageConverters().stream()
                .filter(c -> c instanceof JacksonJsonHttpMessageConverter)
                .count();
        assertEquals(1, count);
    }

    @Test
    public void testOneDriveClient() {
        RestTemplate restTemplate = context.graphRestTemplate();
        TokenRefresher tokenRefresher = mock(TokenRefresher.class);

        OneDriveClient client = context.oneDriveClient(restTemplate, tokenRefresher);

        assertNotNull(client);
    }
}

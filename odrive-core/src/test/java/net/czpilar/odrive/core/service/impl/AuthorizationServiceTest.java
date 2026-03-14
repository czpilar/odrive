package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.IODriveCredential;
import net.czpilar.odrive.core.exception.AuthorizationFailedException;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthorizationServiceTest {

    private final AuthorizationService service = new AuthorizationService();

    @Mock
    private ODriveSetting oDriveSetting;

    @Mock
    private IODriveCredential oDriveCredential;

    @Mock
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient;

    @Mock
    private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient;

    private ClientRegistration clientRegistration;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        clientRegistration = ClientRegistration.withRegistrationId("microsoft")
                .clientId("test-client-id")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8783/odrive")
                .scope("Files.ReadWrite", "User.Read", "offline_access")
                .authorizationUri("https://login.microsoftonline.com/common/oauth2/v2.0/authorize")
                .tokenUri("https://login.microsoftonline.com/common/oauth2/v2.0/token")
                .build();

        service.setClientRegistration(clientRegistration);
        service.setODriveSetting(oDriveSetting);
        service.setAuthorizationCodeTokenResponseClient(authorizationCodeTokenResponseClient);
        service.setRefreshTokenResponseClient(refreshTokenResponseClient);
        service.setODriveCredential(oDriveCredential);

        when(oDriveSetting.getAuthorizationEndpoint()).thenReturn("https://login.microsoftonline.com/common/oauth2/v2.0/authorize");
        when(oDriveSetting.getTokenEndpoint()).thenReturn("https://login.microsoftonline.com/common/oauth2/v2.0/token");
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetAuthorizationURL() {
        String result = service.getAuthorizationURL();

        assertNotNull(result);
        assertTrue(result.contains("client_id=test-client-id"));
        assertTrue(result.contains("response_type=code"));
        assertTrue(result.contains("redirect_uri="));

        verify(oDriveSetting).getAuthorizationEndpoint();
    }

    @Test
    public void testAuthorize() {
        String authorizationCode = "test-authorization-code";

        Instant now = Instant.now();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-access-token", now, now.plusSeconds(3600));
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("test-refresh-token", now);

        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse
                .withToken("test-access-token")
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600)
                .refreshToken("test-refresh-token")
                .scopes(Set.of("Files.ReadWrite", "User.Read", "offline_access"))
                .build();

        when(authorizationCodeTokenResponseClient.getTokenResponse(any(OAuth2AuthorizationCodeGrantRequest.class)))
                .thenReturn(tokenResponse);

        Credential result = service.authorize(authorizationCode);

        assertNotNull(result);
        assertEquals("test-access-token", result.accessToken());
        assertEquals("test-refresh-token", result.refreshToken());

        verify(authorizationCodeTokenResponseClient).getTokenResponse(any(OAuth2AuthorizationCodeGrantRequest.class));
        verify(oDriveCredential).saveCredential(any(Credential.class));
    }

    @Test
    public void testAuthorizeWithException() {
        String authorizationCode = "test-authorization-code";

        when(authorizationCodeTokenResponseClient.getTokenResponse(any(OAuth2AuthorizationCodeGrantRequest.class)))
                .thenThrow(new RuntimeException("Authorization failed"));

        assertThrows(AuthorizationFailedException.class, () -> service.authorize(authorizationCode));

        verify(authorizationCodeTokenResponseClient).getTokenResponse(any(OAuth2AuthorizationCodeGrantRequest.class));
    }

    @Test
    public void testRefreshAccessToken() {
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse
                .withToken("new-access-token")
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600)
                .refreshToken("new-refresh-token")
                .scopes(Set.of("Files.ReadWrite", "User.Read", "offline_access"))
                .build();

        when(refreshTokenResponseClient.getTokenResponse(any(OAuth2RefreshTokenGrantRequest.class)))
                .thenReturn(tokenResponse);

        Credential result = service.refreshAccessToken("old-refresh-token");

        assertNotNull(result);
        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());

        verify(refreshTokenResponseClient).getTokenResponse(any(OAuth2RefreshTokenGrantRequest.class));
        verify(oDriveCredential).saveCredential(any(Credential.class));
    }

    @Test
    public void testRefreshAccessTokenWithException() {
        when(refreshTokenResponseClient.getTokenResponse(any(OAuth2RefreshTokenGrantRequest.class)))
                .thenThrow(new RuntimeException("Refresh failed"));

        assertThrows(AuthorizationFailedException.class, () -> service.refreshAccessToken("old-refresh-token"));

        verify(refreshTokenResponseClient).getTokenResponse(any(OAuth2RefreshTokenGrantRequest.class));
    }
}

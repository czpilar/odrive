package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.exception.AuthorizationFailedException;
import net.czpilar.odrive.core.service.IAuthorizationService;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Authorization service with methods for authorization to OneDrive
 * using Spring Security OAuth2 Client.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Service
public class AuthorizationService extends AbstractService implements IAuthorizationService {

    private static final String CLI_STATE = "odrive-cli";

    private ClientRegistration clientRegistration;
    private ODriveSetting oDriveSetting;
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient;
    private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient;

    @Autowired
    public void setClientRegistration(ClientRegistration clientRegistration) {
        this.clientRegistration = clientRegistration;
    }

    @Autowired
    public void setODriveSetting(ODriveSetting oDriveSetting) {
        this.oDriveSetting = oDriveSetting;
    }

    @Autowired
    public void setAuthorizationCodeTokenResponseClient(
            OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient) {
        this.authorizationCodeTokenResponseClient = authorizationCodeTokenResponseClient;
    }

    @Autowired
    public void setRefreshTokenResponseClient(
            OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient) {
        this.refreshTokenResponseClient = refreshTokenResponseClient;
    }

    private OAuth2AuthorizationRequest buildAuthorizationRequest() {
        return OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(oDriveSetting.getAuthorizationEndpoint())
                .redirectUri(clientRegistration.getRedirectUri())
                .scopes(clientRegistration.getScopes())
                .state(CLI_STATE)
                .build();
    }

    @Override
    public String getAuthorizationURL() {
        return buildAuthorizationRequest().getAuthorizationRequestUri();
    }

    @Override
    public Credential authorize(String authorizationCode) {
        try {
            OAuth2AuthorizationRequest authorizationRequest = buildAuthorizationRequest();

            OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse
                    .success(authorizationCode)
                    .redirectUri(clientRegistration.getRedirectUri())
                    .state(CLI_STATE)
                    .build();

            OAuth2AuthorizationExchange authorizationExchange =
                    new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);

            OAuth2AuthorizationCodeGrantRequest grantRequest =
                    new OAuth2AuthorizationCodeGrantRequest(clientRegistration, authorizationExchange);

            OAuth2AccessTokenResponse tokenResponse = authorizationCodeTokenResponseClient.getTokenResponse(grantRequest);

            Credential credential = toCredential(tokenResponse);
            getODriveCredential().saveRefreshToken(credential.refreshToken());
            return credential;
        } catch (Exception e) {
            throw new AuthorizationFailedException("Error occurs during authorization process.", e);
        }
    }

    @Override
    public Credential refreshAccessToken(String refreshToken) {
        try {
            Instant now = Instant.now();
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER, "expired",
                    now.minusSeconds(3600), now.minusSeconds(1));
            OAuth2RefreshToken refreshTokenObj = new OAuth2RefreshToken(refreshToken, now.minusSeconds(3600));

            OAuth2RefreshTokenGrantRequest refreshRequest =
                    new OAuth2RefreshTokenGrantRequest(clientRegistration, accessToken, refreshTokenObj);

            OAuth2AccessTokenResponse tokenResponse = refreshTokenResponseClient.getTokenResponse(refreshRequest);

            Credential credential = toCredential(tokenResponse);
            getODriveCredential().saveRefreshToken(credential.refreshToken());
            return credential;
        } catch (Exception e) {
            throw new AuthorizationFailedException("Error occurs during token refresh.", e);
        }
    }

    private static Credential toCredential(OAuth2AccessTokenResponse tokenResponse) {
        String accessToken = tokenResponse.getAccessToken().getTokenValue();
        String refreshToken = tokenResponse.getRefreshToken() != null
                ? tokenResponse.getRefreshToken().getTokenValue()
                : null;
        return new Credential(accessToken, refreshToken);
    }
}

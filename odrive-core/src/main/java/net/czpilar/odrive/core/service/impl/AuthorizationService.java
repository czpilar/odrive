package net.czpilar.odrive.core.service.impl;

import com.google.gson.Gson;
import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.exception.AuthorizationFailedException;
import net.czpilar.odrive.core.model.TokenResponse;
import net.czpilar.odrive.core.service.IAuthorizationService;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Authorization service with methods for authorization to OneDrive
 * using Microsoft OAuth 2.0 authorization code flow.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Service
public class AuthorizationService extends AbstractService implements IAuthorizationService {

    private ODriveSetting oDriveSetting;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Autowired
    public void setODriveSetting(ODriveSetting oDriveSetting) {
        this.oDriveSetting = oDriveSetting;
    }

    @Override
    public String getAuthorizationURL() {
        return oDriveSetting.getAuthorizationEndpoint()
                + "?client_id=" + encode(oDriveSetting.getClientId())
                + "&response_type=code"
                + "&redirect_uri=" + encode(ODriveSetting.REDIRECT_URI)
                + "&scope=" + encode(ODriveSetting.SCOPES)
                + "&response_mode=query";
    }

    @Override
    public Credential authorize(String authorizationCode) {
        try {
            String body = "client_id=" + encode(oDriveSetting.getClientId())
                    + "&grant_type=authorization_code"
                    + "&code=" + encode(authorizationCode)
                    + "&redirect_uri=" + encode(ODriveSetting.REDIRECT_URI)
                    + "&scope=" + encode(ODriveSetting.SCOPES);

            TokenResponse tokenResponse = executeTokenRequest(body);
            Credential credential = new Credential(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
            getODriveCredential().saveCredential(credential);
            return credential;
        } catch (Exception e) {
            throw new AuthorizationFailedException("Error occurs during authorization process.", e);
        }
    }

    @Override
    public Credential refreshAccessToken(String refreshToken) {
        try {
            String body = "client_id=" + encode(oDriveSetting.getClientId())
                    + "&grant_type=refresh_token"
                    + "&refresh_token=" + encode(refreshToken)
                    + "&scope=" + encode(ODriveSetting.SCOPES);

            TokenResponse tokenResponse = executeTokenRequest(body);
            Credential credential = new Credential(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
            getODriveCredential().saveCredential(credential);
            return credential;
        } catch (Exception e) {
            throw new AuthorizationFailedException("Error occurs during token refresh.", e);
        }
    }

    private TokenResponse executeTokenRequest(String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(oDriveSetting.getTokenEndpoint()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        TokenResponse tokenResponse = gson.fromJson(response.body(), TokenResponse.class);

        if (tokenResponse.getError() != null) {
            throw new RuntimeException("Token request failed: " + tokenResponse.getError()
                    + " - " + tokenResponse.getErrorDescription());
        }

        return tokenResponse;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

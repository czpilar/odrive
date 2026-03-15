package net.czpilar.odrive.core.setting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Holder for oDrive settings including Microsoft app registration details.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Component
public class ODriveSetting {

    public static final String APPLICATION_NAME = "odrive";

    public static final List<String> SCOPES = List.of("Files.ReadWrite", "User.Read", "offline_access");

    private final String applicationVersion;
    private final String clientId;
    private final String tenant;
    private final String redirectUri;
    private final int redirectUriPort;
    private final String redirectUriContext;

    public ODriveSetting(@Value("${odrive.version}") String applicationVersion,
                         @Value("${odrive.core.drive.clientId}") String clientId,
                         @Value("${odrive.core.drive.tenant:common}") String tenant,
                         @Value("${odrive.core.drive.redirectUri}") String redirectUri,
                         @Value("${odrive.core.drive.redirectUri.port}") int redirectUriPort,
                         @Value("${odrive.core.drive.redirectUri.context}") String redirectUriContext) {
        this.applicationVersion = applicationVersion;
        this.clientId = clientId;
        this.tenant = tenant;
        this.redirectUri = redirectUri;
        this.redirectUriPort = redirectUriPort;
        this.redirectUriContext = redirectUriContext;
    }

    public String getApplicationName() {
        return APPLICATION_NAME;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTenant() {
        return tenant;
    }

    public String getAuthorizationEndpoint() {
        return "https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/authorize";
    }

    public String getTokenEndpoint() {
        return "https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/token";
    }

    public int getRedirectUriPort() {
        return redirectUriPort;
    }

    public String getRedirectUriContext() {
        return redirectUriContext;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}

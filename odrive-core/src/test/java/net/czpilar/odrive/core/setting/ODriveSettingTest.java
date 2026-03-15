package net.czpilar.odrive.core.setting;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ODriveSettingTest {

    @Test
    void testODriveSetting() {
        String applicationVersion = "test-application-version";
        String clientId = "test-client-id";
        String tenant = "test-tenant";
        String redirectUri = "http://127.0.0.1:9999/test-context";
        int redirectUriPort = 9999;
        String redirectUriContext = "/test-context";
        ODriveSetting setting = new ODriveSetting(applicationVersion, clientId, tenant, redirectUri, redirectUriPort, redirectUriContext);

        assertEquals(ODriveSetting.APPLICATION_NAME, setting.getApplicationName());
        assertEquals(applicationVersion, setting.getApplicationVersion());
        assertEquals(clientId, setting.getClientId());
        assertEquals(tenant, setting.getTenant());
        assertEquals("https://login.microsoftonline.com/test-tenant/oauth2/v2.0/authorize", setting.getAuthorizationEndpoint());
        assertEquals("https://login.microsoftonline.com/test-tenant/oauth2/v2.0/token", setting.getTokenEndpoint());
        assertEquals(redirectUriPort, setting.getRedirectUriPort());
        assertEquals(redirectUriContext, setting.getRedirectUriContext());
        assertEquals(redirectUri, setting.getRedirectUri());
        assertEquals(List.of("Files.ReadWrite", "User.Read", "offline_access"), ODriveSetting.SCOPES);
    }
}

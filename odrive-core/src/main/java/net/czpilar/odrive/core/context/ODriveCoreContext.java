package net.czpilar.odrive.core.context;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;

/**
 * Spring configuration for oDrive core providing OneDrive client.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Configuration
@ComponentScan(basePackages = "net.czpilar.odrive.core")
@PropertySource("classpath:odrive.properties")
public class ODriveCoreContext {

    private static final Logger LOG = LoggerFactory.getLogger(ODriveCoreContext.class);

    private final CredentialLoader credentialLoader;
    private final IAuthorizationService authorizationService;

    public ODriveCoreContext(CredentialLoader credentialLoader, IAuthorizationService authorizationService) {
        this.credentialLoader = credentialLoader;
        this.authorizationService = authorizationService;
    }

    @Bean
    @Scope("prototype")
    public OneDriveClient oneDriveClient() {
        Credential credential = credentialLoader.getCredential();
        String accessToken = credential.accessToken();

        if (accessToken == null && credential.refreshToken() != null) {
            LOG.info("Access token is null, attempting to refresh...");
            Credential refreshed = authorizationService.refreshAccessToken(credential.refreshToken());
            accessToken = refreshed.accessToken();
        }

        return new OneDriveClient(accessToken);
    }
}

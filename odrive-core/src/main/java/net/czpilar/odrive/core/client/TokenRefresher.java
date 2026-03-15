package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Supplies a fresh access token by refreshing it using the stored refresh token.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Component
public class TokenRefresher implements Supplier<String> {

    private static final Logger LOG = LoggerFactory.getLogger(TokenRefresher.class);

    private final CredentialLoader credentialLoader;
    private final IAuthorizationService authorizationService;

    public TokenRefresher(CredentialLoader credentialLoader, IAuthorizationService authorizationService) {
        this.credentialLoader = credentialLoader;
        this.authorizationService = authorizationService;
    }

    @Override
    public String get() {
        String refreshToken = credentialLoader.getRefreshToken();
        if (refreshToken == null) {
            return null;
        }
        LOG.info("Refreshing access token...");
        Credential credential = authorizationService.refreshAccessToken(refreshToken);
        return credential.accessToken();
    }
}

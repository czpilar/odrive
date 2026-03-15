package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * REST client interceptor that adds Bearer authentication to requests.
 * Lazily obtains the access token on first request by refreshing it
 * using the stored refresh token, and automatically retries on
 * 401 Unauthorized responses.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Component
public class BearerAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(BearerAuthInterceptor.class);

    private final CredentialLoader credentialLoader;
    private final IAuthorizationService authorizationService;
    private final AtomicReference<String> currentToken = new AtomicReference<>();

    public BearerAuthInterceptor(CredentialLoader credentialLoader, IAuthorizationService authorizationService) {
        this.credentialLoader = credentialLoader;
        this.authorizationService = authorizationService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (currentToken.get() == null) {
            currentToken.set(refreshAccessToken());
        }
        request.getHeaders().setBearerAuth(currentToken.get());
        try {
            return execution.execute(request, body);
        } catch (HttpClientErrorException.Unauthorized e) {
            currentToken.set(refreshAccessToken());
            request.getHeaders().setBearerAuth(currentToken.get());
            return execution.execute(request, body);
        }
    }

    private String refreshAccessToken() {
        String refreshToken = credentialLoader.getRefreshToken();
        if (refreshToken == null) {
            return null;
        }
        LOG.info("Refreshing access token...");
        Credential credential = authorizationService.refreshAccessToken(refreshToken);
        return credential.accessToken();
    }
}

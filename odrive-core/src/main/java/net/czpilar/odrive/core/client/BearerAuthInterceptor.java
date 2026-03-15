package net.czpilar.odrive.core.client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * REST client interceptor that adds Bearer authentication to requests.
 * Lazily obtains the access token on first request and automatically
 * refreshes it on 401 Unauthorized responses.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class BearerAuthInterceptor implements ClientHttpRequestInterceptor {

    private final Supplier<String> tokenRefresher;
    private final AtomicReference<String> currentToken = new AtomicReference<>();

    public BearerAuthInterceptor(Supplier<String> tokenRefresher) {
        this.tokenRefresher = tokenRefresher;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (currentToken.get() == null) {
            currentToken.set(tokenRefresher.get());
        }
        request.getHeaders().setBearerAuth(currentToken.get());
        try {
            return execution.execute(request, body);
        } catch (HttpClientErrorException.Unauthorized e) {
            currentToken.set(tokenRefresher.get());
            request.getHeaders().setBearerAuth(currentToken.get());
            return execution.execute(request, body);
        }
    }
}

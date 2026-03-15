package net.czpilar.odrive.core.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class BearerAuthInterceptorTest {

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @Mock
    private Supplier<String> tokenRefresher;

    private HttpHeaders headers;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testFirstRequestObtainsToken() throws IOException {
        when(tokenRefresher.get()).thenReturn("access-token");
        when(execution.execute(request, new byte[0])).thenReturn(response);

        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(tokenRefresher);
        interceptor.intercept(request, new byte[0], execution);

        verify(tokenRefresher).get();
        verify(execution).execute(request, new byte[0]);
    }

    @Test
    public void testSecondRequestReusesToken() throws IOException {
        when(tokenRefresher.get()).thenReturn("access-token");
        when(execution.execute(request, new byte[0])).thenReturn(response);

        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(tokenRefresher);
        interceptor.intercept(request, new byte[0], execution);
        interceptor.intercept(request, new byte[0], execution);

        verify(tokenRefresher, times(1)).get();
        verify(execution, times(2)).execute(request, new byte[0]);
    }

    @Test
    public void testRefreshesTokenOn401() throws IOException {
        when(tokenRefresher.get()).thenReturn("old-token", "new-token");
        when(execution.execute(request, new byte[0]))
                .thenThrow(mock(HttpClientErrorException.Unauthorized.class))
                .thenReturn(response);

        BearerAuthInterceptor interceptor = new BearerAuthInterceptor(tokenRefresher);
        interceptor.intercept(request, new byte[0], execution);

        verify(tokenRefresher, times(2)).get();
        verify(execution, times(2)).execute(request, new byte[0]);
    }
}

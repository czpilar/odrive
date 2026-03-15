package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BearerAuthInterceptorTest {

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @Mock
    private CredentialLoader credentialLoader;

    @Mock
    private IAuthorizationService authorizationService;

    private HttpHeaders headers;
    private BearerAuthInterceptor interceptor;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        interceptor = new BearerAuthInterceptor(credentialLoader, authorizationService);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testFirstRequestObtainsToken() throws IOException {
        when(credentialLoader.getRefreshToken()).thenReturn("my-refresh-token");
        when(authorizationService.refreshAccessToken("my-refresh-token"))
                .thenReturn(new Credential("access-token", "new-refresh-token"));
        when(execution.execute(request, new byte[0])).thenReturn(response);

        interceptor.intercept(request, new byte[0], execution);

        verify(credentialLoader).getRefreshToken();
        verify(authorizationService).refreshAccessToken("my-refresh-token");
        verify(execution).execute(request, new byte[0]);
        assertEquals("Bearer access-token", headers.getFirst("Authorization"));
    }

    @Test
    void testSecondRequestReusesToken() throws IOException {
        when(credentialLoader.getRefreshToken()).thenReturn("my-refresh-token");
        when(authorizationService.refreshAccessToken("my-refresh-token"))
                .thenReturn(new Credential("access-token", "new-refresh-token"));
        when(execution.execute(request, new byte[0])).thenReturn(response);

        interceptor.intercept(request, new byte[0], execution);
        interceptor.intercept(request, new byte[0], execution);

        verify(credentialLoader, times(1)).getRefreshToken();
        verify(authorizationService, times(1)).refreshAccessToken("my-refresh-token");
        verify(execution, times(2)).execute(request, new byte[0]);
    }

    @Test
    void testRefreshesTokenOn401() throws IOException {
        when(credentialLoader.getRefreshToken()).thenReturn("my-refresh-token");
        when(authorizationService.refreshAccessToken("my-refresh-token"))
                .thenReturn(new Credential("old-token", "rt1"))
                .thenReturn(new Credential("new-token", "rt2"));
        when(execution.execute(request, new byte[0]))
                .thenThrow(mock(HttpClientErrorException.Unauthorized.class))
                .thenReturn(response);

        interceptor.intercept(request, new byte[0], execution);

        verify(credentialLoader, times(2)).getRefreshToken();
        verify(authorizationService, times(2)).refreshAccessToken("my-refresh-token");
        verify(execution, times(2)).execute(request, new byte[0]);
        assertEquals("Bearer new-token", headers.getFirst("Authorization"));
    }

    @Test
    void testReturnsNullTokenWhenNoRefreshToken() throws IOException {
        when(credentialLoader.getRefreshToken()).thenReturn(null);
        when(execution.execute(request, new byte[0])).thenReturn(response);

        interceptor.intercept(request, new byte[0], execution);

        verify(credentialLoader).getRefreshToken();
        verifyNoInteractions(authorizationService);
    }

}

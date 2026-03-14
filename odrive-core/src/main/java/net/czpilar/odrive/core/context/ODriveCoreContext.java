package net.czpilar.odrive.core.context;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.loader.CredentialLoader;
import net.czpilar.odrive.core.service.IAuthorizationService;
import net.czpilar.odrive.core.setting.ODriveSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring configuration for oDrive core providing OneDrive client
 * and Spring Security OAuth2 client registration.
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
    public ClientRegistration microsoftClientRegistration(ODriveSetting setting) {
        return ClientRegistration.withRegistrationId("microsoft")
                .clientId(setting.getClientId())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(ODriveSetting.REDIRECT_URI)
                .scope(ODriveSetting.SCOPES)
                .authorizationUri(setting.getAuthorizationEndpoint())
                .tokenUri(setting.getTokenEndpoint())
                .build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
        return new RestClientAuthorizationCodeTokenResponseClient();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient() {
        return new RestClientRefreshTokenTokenResponseClient();
    }

    @Bean
    public RestTemplate graphRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Configure Jackson 3 message converter
        JacksonJsonHttpMessageConverter jacksonConverter = new JacksonJsonHttpMessageConverter(
                JsonMapper.builder()
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .build()
        );
        restTemplate.getMessageConverters().removeIf(c -> c instanceof JacksonJsonHttpMessageConverter);
        restTemplate.getMessageConverters().addFirst(jacksonConverter);

        return restTemplate;
    }

    @Bean
    @Scope("prototype")
    public OneDriveClient oneDriveClient(RestTemplate graphRestTemplate) {
        Credential credential = credentialLoader.getCredential();
        String accessToken = credential.accessToken();

        if (accessToken == null && credential.refreshToken() != null) {
            LOG.info("Access token is null, attempting to refresh...");
            Credential refreshed = authorizationService.refreshAccessToken(credential.refreshToken());
            accessToken = refreshed.accessToken();
        }

        return new OneDriveClient(graphRestTemplate, accessToken);
    }
}

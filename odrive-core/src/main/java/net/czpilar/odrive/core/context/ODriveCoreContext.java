package net.czpilar.odrive.core.context;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.client.TokenRefresher;
import net.czpilar.odrive.core.setting.ODriveSetting;
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

    @Bean
    public ClientRegistration microsoftClientRegistration(ODriveSetting setting) {
        return ClientRegistration.withRegistrationId("microsoft")
                .clientId(setting.getClientId())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(setting.getRedirectUri())
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
    @Lazy
    public OneDriveClient oneDriveClient(RestTemplate graphRestTemplate, TokenRefresher tokenRefresher) {
        return new OneDriveClient(graphRestTemplate, tokenRefresher);
    }
}

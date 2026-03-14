package net.czpilar.odrive.core.service;

import net.czpilar.odrive.core.credential.Credential;

/**
 * Authorization service interface.
 *
 * @author David Pilar (david@czpilar.net)
 */
public interface IAuthorizationService {

    /**
     * Returns authorization URL to authorize application.
     *
     * @return authorization url
     */
    String getAuthorizationURL();

    /**
     * Authorize application and returns credential.
     *
     * @param authorizationCode authorization code
     * @return credential
     */
    Credential authorize(String authorizationCode);

    /**
     * Refreshes the access token using the refresh token.
     *
     * @param refreshToken refresh token
     * @return new credential with refreshed tokens
     */
    Credential refreshAccessToken(String refreshToken);

}

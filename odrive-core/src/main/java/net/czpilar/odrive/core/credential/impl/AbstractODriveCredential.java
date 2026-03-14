package net.czpilar.odrive.core.credential.impl;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.IODriveCredential;

/**
 * Template implementation of {@link IODriveCredential} interface
 * for oDrive credential.
 *
 * @author David Pilar (david@czpilar.net)
 */
public abstract class AbstractODriveCredential implements IODriveCredential {

    /**
     * Returns access token.
     *
     * @return access token
     */
    protected abstract String getAccessToken();

    /**
     * Returns refresh token.
     *
     * @return refresh token
     */
    protected abstract String getRefreshToken();

    /**
     * Saves access token and refresh token.
     *
     * @param accessToken  access token
     * @param refreshToken refresh token
     */
    protected abstract void saveTokens(String accessToken, String refreshToken);

    @Override
    public Credential getCredential() {
        return new Credential(getAccessToken(), getRefreshToken());
    }

    @Override
    public void saveCredential(Credential credential) {
        saveTokens(credential.accessToken(), credential.refreshToken());
    }
}

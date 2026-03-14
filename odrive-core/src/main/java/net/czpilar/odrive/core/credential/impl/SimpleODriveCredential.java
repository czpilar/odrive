package net.czpilar.odrive.core.credential.impl;

/**
 * Simple implementation of {@link net.czpilar.odrive.core.credential.IODriveCredential} interface
 * for oDrive credential.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class SimpleODriveCredential extends AbstractODriveCredential {

    private String accessToken;
    private String refreshToken;
    private String uploadDir;

    @Override
    protected String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    protected void saveTokens(String accessToken, String refreshToken) {
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
    }

    @Override
    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}

package net.czpilar.odrive.core.credential.impl;

/**
 * Simple implementation of {@link net.czpilar.odrive.core.credential.IODriveCredential} interface
 * for oDrive credential.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class SimpleODriveCredential extends AbstractODriveCredential {

    private String refreshToken;
    private String uploadDir;

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}

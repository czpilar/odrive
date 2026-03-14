package net.czpilar.odrive.core.credential;

/**
 * oDrive credential interface.
 *
 * @author David Pilar (david@czpilar.net)
 */
public interface IODriveCredential {

    /**
     * Returns credential.
     *
     * @return credential
     */
    Credential getCredential();

    /**
     * Saves credential.
     *
     * @param credential credential
     */
    void saveCredential(Credential credential);

    /**
     * Returns upload dir.
     *
     * @return upload dir
     */
    String getUploadDir();

}

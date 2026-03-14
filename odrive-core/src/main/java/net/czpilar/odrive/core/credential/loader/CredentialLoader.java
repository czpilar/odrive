package net.czpilar.odrive.core.credential.loader;

import net.czpilar.odrive.core.credential.Credential;
import net.czpilar.odrive.core.credential.IODriveCredential;
import net.czpilar.odrive.core.exception.NoCredentialFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Autoloader of oDrive credential from Spring context.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Component
public class CredentialLoader {

    private final IODriveCredential oDriveCredential;

    @Autowired
    public CredentialLoader(IODriveCredential oDriveCredential) {
        if (oDriveCredential == null) {
            throw new NoCredentialFoundException("No credential found.");
        }
        this.oDriveCredential = oDriveCredential;
    }

    public Credential getCredential() {
        return oDriveCredential.getCredential();
    }
}

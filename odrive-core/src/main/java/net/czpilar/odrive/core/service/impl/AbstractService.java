package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.credential.IODriveCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Template service.
 *
 * @author David Pilar (david@czpilar.net)
 */
public abstract class AbstractService {

    private ApplicationContext applicationContext;
    private IODriveCredential oDriveCredential;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public IODriveCredential getODriveCredential() {
        return oDriveCredential;
    }

    @Autowired
    public void setODriveCredential(IODriveCredential oDriveCredential) {
        this.oDriveCredential = oDriveCredential;
    }

    protected OneDriveClient getOneDriveClient() {
        return applicationContext.getBean(OneDriveClient.class);
    }

}

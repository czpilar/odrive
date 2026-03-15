package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.FileHandleException;
import net.czpilar.odrive.core.exception.OneDriveClientException;
import net.czpilar.odrive.core.model.DriveItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Base service for file and directory common functions.
 *
 * @author David Pilar (david@czpilar.net)
 */
public abstract class AbstractFileService extends AbstractService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFileService.class);

    protected String getPath(String filename, DriveItem parent) {
        Assert.notNull(filename, "Filename must not be null.");

        StringBuilder path = new StringBuilder();
        if (parent != null) {
            path.append(parent.getPathDisplay());
        }
        if (!filename.startsWith("/")) {
            path.append("/");
        }
        path.append(filename);

        return path.toString();
    }

    protected DriveItem findFolder(String filename, DriveItem parent) {
        DriveItem entry = findEntry(filename, parent);
        return entry != null && entry.isFolder() ? entry : null;
    }

    protected DriveItem findFile(String filename, DriveItem parent) {
        DriveItem entry = findEntry(filename, parent);
        return entry != null && entry.isFile() ? entry : null;
    }

    private DriveItem findEntry(String filename, DriveItem parent) {
        try {
            String path = getPath(filename, parent);
            return getOneDriveClient().getItemByPath(path);
        } catch (OneDriveClientException e) {
            LOG.error("Unable to find {}.", filename);
            throw new FileHandleException("Unable to find file.", e);
        }
    }

}

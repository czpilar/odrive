package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.DirectoryHandleException;
import net.czpilar.odrive.core.exception.OneDriveClientException;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.service.IDirectoryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service with methods for handling directories in OneDrive.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Service
public class DirectoryService extends AbstractFileService implements IDirectoryService {

    private static final String DIRECTORY_SEPARATOR = "/";

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryService.class);

    private static String normalizePathname(String pathname) {
        return Strings.CS.replace(pathname, "\\", DIRECTORY_SEPARATOR);
    }

    private static String getCurrentDirname(String pathname) {
        return StringUtils.trimToNull(StringUtils.substringBefore(pathname, DIRECTORY_SEPARATOR));
    }

    private static String getNextPathname(String pathname) {
        return StringUtils.trimToNull(StringUtils.substringAfter(pathname, DIRECTORY_SEPARATOR));
    }

    protected DriveItem createOneDirectory(String dirname, DriveItem parentDir) {
        try {
            if (parentDir == null) {
                return getOneDriveClient().createFolderAtRoot(dirname);
            }
            return getOneDriveClient().createFolder(parentDir.id(), dirname);
        } catch (OneDriveClientException e) {
            LOG.error("Unable to create directory {}.", dirname);
            throw new DirectoryHandleException("Unable to create directory.", e);
        }
    }

    protected DriveItem findOrCreateOneDirectory(String dirname, DriveItem parentDir) {
        DriveItem dir = findFolder(dirname, parentDir);
        if (dir == null) {
            dir = createOneDirectory(dirname, parentDir);
        }
        return dir;
    }

    @Override
    public DriveItem findDirectory(String pathname) {
        return findDirectory(pathname, null);
    }

    @Override
    public DriveItem findDirectory(String pathname, DriveItem parentDir) {
        pathname = normalizePathname(pathname);
        String dirname = getCurrentDirname(pathname);
        DriveItem currentDir = parentDir;
        if (dirname != null) {
            currentDir = findFolder(dirname, parentDir);
        }
        String nextPathname = getNextPathname(pathname);
        if (currentDir != null && nextPathname != null) {
            currentDir = findDirectory(nextPathname, currentDir);
        }
        return currentDir;
    }

    @Override
    public DriveItem findOrCreateDirectory(String pathname) {
        return findOrCreateDirectory(pathname, null);
    }

    @Override
    public DriveItem findOrCreateDirectory(String pathname, DriveItem parentDir) {
        pathname = normalizePathname(pathname);
        String dirname = getCurrentDirname(pathname);
        DriveItem currentDir = parentDir;
        if (dirname != null) {
            currentDir = findOrCreateOneDirectory(dirname, parentDir);
        }
        String nextPathname = getNextPathname(pathname);
        if (nextPathname != null) {
            currentDir = findOrCreateDirectory(nextPathname, currentDir);
        }
        return currentDir;
    }
}

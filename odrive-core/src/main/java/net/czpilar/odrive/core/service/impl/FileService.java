package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.FileHandleException;
import net.czpilar.odrive.core.listener.impl.FileUploadProgressListener;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.request.FileRequest;
import net.czpilar.odrive.core.service.IDirectoryService;
import net.czpilar.odrive.core.service.IFileService;
import net.czpilar.odrive.core.util.EqualUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service with methods for handling files in OneDrive.
 *
 * @author David Pilar (david@czpilar.net)
 */
@Service
public class FileService extends AbstractFileService implements IFileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    private final int retries;

    private IDirectoryService directoryService;

    public FileService(@Value("${odrive.file.upload.retries}") int retries) {
        this.retries = retries;
    }

    public int getRetries() {
        return retries;
    }

    @Autowired
    public void setDirectoryService(IDirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    protected IDirectoryService getDirectoryService() {
        return directoryService;
    }

    protected String getUploadDir(String uploadDirname) {
        if (uploadDirname == null) {
            uploadDirname = getODriveCredential().getUploadDir();
        }
        return uploadDirname;
    }

    @Override
    public DriveItem uploadFile(String filename, String pathname) {
        DriveItem parentDir = getDirectoryService().findOrCreateDirectory(getUploadDir(pathname));
        return uploadFile(filename, parentDir);
    }

    private DriveItem insertFile(Path pathToFile, DriveItem parentDir) throws Exception {
        String filename = pathToFile.getFileName().toString();
        LOG.info("Uploading new file {}", filename);
        String remotePath = getPath(filename, parentDir);
        FileRequest request = FileRequest.create(getOneDriveClient(), remotePath, pathToFile.toFile());
        request.setProgressListener(new FileUploadProgressListener(filename, pathToFile.toFile().length()));
        return execute(request);
    }

    private DriveItem updateFile(DriveItem currentFile, Path pathToFile) throws Exception {
        String filename = pathToFile.getFileName().toString();
        LOG.info("Uploading updated file {}", filename);
        String remotePath = currentFile.getPathDisplay();
        FileRequest request = FileRequest.create(getOneDriveClient(), remotePath, pathToFile.toFile());
        request.setProgressListener(new FileUploadProgressListener(filename, pathToFile.toFile().length()));
        return execute(request);
    }

    private DriveItem execute(FileRequest request) throws Exception {
        int retry = 0;
        while (true) {
            try {
                return request.execute();
            } catch (OneDriveClient.OneDriveClientException e) {
                retry++;
                if (retry > getRetries()) {
                    throw e;
                }
                LOG.warn("Error during executing uploading file, retrying for {} time(s), message: {}", retry, e.getMessage());
            }
        }
    }

    @Override
    public DriveItem uploadFile(String filename, DriveItem parentDir) {
        try {
            Path pathToFile = Paths.get(filename);
            DriveItem currentFile = findFile(pathToFile.getFileName().toString(), parentDir);

            if (currentFile == null) {
                currentFile = insertFile(pathToFile, parentDir);
            } else if (EqualUtils.notEquals(currentFile, pathToFile)) {
                currentFile = updateFile(currentFile, pathToFile);
            } else {
                LOG.info("There is nothing to upload.");
            }

            LOG.info("Finished uploading file {} - remote eTag is {}", filename, currentFile.eTag());
            return currentFile;
        } catch (Exception e) {
            LOG.error("Unable to upload file {}.", filename);
            throw new FileHandleException("Unable to upload file.", e);
        }
    }

    @Override
    public DriveItem uploadFile(String filename) {
        return uploadFile(filename, (DriveItem) null);
    }

    @Override
    public List<DriveItem> uploadFiles(List<String> filenames) {
        return uploadFiles(filenames, (DriveItem) null);
    }

    @Override
    public List<DriveItem> uploadFiles(List<String> filenames, String pathname) {
        DriveItem parentDir = getDirectoryService().findOrCreateDirectory(getUploadDir(pathname));
        return uploadFiles(filenames, parentDir);
    }

    @Override
    public List<DriveItem> uploadFiles(List<String> filenames, DriveItem parentDir) {
        List<DriveItem> files = new ArrayList<>();
        if (filenames != null) {
            for (String filename : filenames) {
                try {
                    files.add(uploadFile(filename, parentDir));
                } catch (FileHandleException e) {
                    LOG.error("Error during uploading file.", e);
                }
            }
        }
        return files;
    }
}

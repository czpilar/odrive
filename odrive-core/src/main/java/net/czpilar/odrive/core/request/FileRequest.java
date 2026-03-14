package net.czpilar.odrive.core.request;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.ODriveException;
import net.czpilar.odrive.core.listener.IFileUploadProgressListener;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.UploadSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * File request implementation for uploading file using chunk file upload to OneDrive.
 * Uses Microsoft Graph upload session for large files and direct content PUT for small files.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class FileRequest {

    private static final Logger LOG = LoggerFactory.getLogger(FileRequest.class);

    // Chunk size must be a multiple of 320 KiB (327,680 bytes). Recommended: 5 MB.
    public static final int CHUNK_SIZE = 5 * 327680; // ~1.6 MB (5 * 320 KiB)
    public static final int CHUNK_RETRIES = 5;
    public static final int SMALL_FILE_LIMIT = 4 * 1024 * 1024; // 4 MB

    public static FileRequest create(OneDriveClient client, String remoteFilePath, File localFile) {
        return new FileRequest(client, remoteFilePath, localFile);
    }

    private final OneDriveClient client;
    private final String remoteFilePath;
    private final File localFile;

    private IFileUploadProgressListener progressListener;

    private FileRequest(OneDriveClient client, String remoteFilePath, File localFile) {
        this.client = client;
        this.remoteFilePath = remoteFilePath;
        this.localFile = localFile;
    }

    public void setProgressListener(IFileUploadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public DriveItem execute() throws IOException {
        long size = localFile.length();

        progress(IFileUploadProgressListener.State.INITIATION, 0);

        if (size < SMALL_FILE_LIMIT) {
            DriveItem result = client.uploadSmallFile(remoteFilePath, localFile);
            progress(IFileUploadProgressListener.State.COMPLETE, size);
            return result;
        }

        return executeChunkedUpload(size);
    }

    private DriveItem executeChunkedUpload(long size) throws IOException {
        UploadSession session = client.createUploadSession(remoteFilePath);
        if (session == null || session.getUploadUrl() == null) {
            throw new ODriveException("Failed to create upload session.");
        }

        long offsetBytes = 0;

        try (FileInputStream stream = new FileInputStream(localFile)) {
            while (offsetBytes < size) {
                long readBytes = Math.min(CHUNK_SIZE, size - offsetBytes);
                byte[] buffer = new byte[(int) readBytes];
                int bytesRead = stream.readNBytes(buffer, 0, (int) readBytes);
                if (bytesRead < readBytes) {
                    buffer = Arrays.copyOf(buffer, bytesRead);
                }

                DriveItem result = uploadChunkWithRetries(
                        session.getUploadUrl(), buffer, offsetBytes,
                        offsetBytes + bytesRead - 1, size);

                offsetBytes += bytesRead;
                progress(IFileUploadProgressListener.State.IN_PROGRESS, offsetBytes);

                if (result != null) {
                    progress(IFileUploadProgressListener.State.COMPLETE, offsetBytes);
                    return result;
                }
            }
        }

        throw new ODriveException("Upload completed but no drive item returned.");
    }

    private DriveItem uploadChunkWithRetries(String uploadUrl, byte[] data,
                                             long rangeStart, long rangeEnd, long totalSize) throws IOException {
        int retry = 0;
        while (true) {
            try {
                return client.uploadChunk(uploadUrl, data, rangeStart, rangeEnd, totalSize);
            } catch (OneDriveClient.OneDriveClientException e) {
                retry++;
                if (retry > CHUNK_RETRIES) {
                    throw e;
                }
                LOG.warn("Error during uploading chunk, offset bytes {}, retrying for {} time(s), message: {}",
                        rangeStart, retry, e.getMessage());
            }
        }
    }

    private void progress(IFileUploadProgressListener.State state, long uploaded) {
        if (progressListener != null) {
            progressListener.progressChanged(state, uploaded);
        }
    }
}

package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.exception.OneDriveClientException;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.UploadSession;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * HTTP client for Microsoft Graph API OneDrive operations.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class OneDriveClient {

    private static final String GRAPH_BASE_URL = "https://graph.microsoft.com/v1.0";

    private final RestTemplate graphRestTemplate;
    private final RestTemplate chunkRestTemplate;

    public OneDriveClient(RestTemplate graphRestTemplate, RestTemplate chunkRestTemplate) {
        this.graphRestTemplate = graphRestTemplate;
        this.chunkRestTemplate = chunkRestTemplate;
    }

    /**
     * Gets a drive item by its path relative to the drive root.
     *
     * @param path path relative to drive root (e.g. "Documents/myfile.txt")
     * @return drive item or null if not found
     * @throws OneDriveClientException if API call fails
     */
    public DriveItem getItemByPath(String path) {
        String encodedPath = encodePath(path);
        String url = GRAPH_BASE_URL + "/me/drive/root:/" + encodedPath;
        try {
            ResponseEntity<DriveItem> response = graphRestTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, DriveItem.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Creates a folder under the drive root.
     *
     * @param name folder name
     * @return created folder drive item
     * @throws OneDriveClientException if API call fails
     */
    public DriveItem createFolderAtRoot(String name) {
        String url = GRAPH_BASE_URL + "/me/drive/root/children";
        try {
            HttpEntity<CreateFolderRequest> entity = new HttpEntity<>(new CreateFolderRequest(name));
            ResponseEntity<DriveItem> response = graphRestTemplate.exchange(url, HttpMethod.POST, entity, DriveItem.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Creates a folder under a specific parent folder.
     *
     * @param parentId parent folder ID
     * @param name     folder name
     * @return created folder drive item
     * @throws OneDriveClientException if API call fails
     */
    public DriveItem createFolder(String parentId, String name) {
        String url = GRAPH_BASE_URL + "/me/drive/items/" + parentId + "/children";
        try {
            HttpEntity<CreateFolderRequest> entity = new HttpEntity<>(new CreateFolderRequest(name));
            ResponseEntity<DriveItem> response = graphRestTemplate.exchange(url, HttpMethod.POST, entity, DriveItem.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Uploads a small file (less than 4MB) to the specified path.
     *
     * @param remotePath remote path relative to drive root
     * @param localFile  local file to upload
     * @return uploaded drive item
     * @throws OneDriveClientException if API call fails
     */
    public DriveItem uploadSmallFile(String remotePath, File localFile) {
        String encodedPath = encodePath(remotePath);
        String url = GRAPH_BASE_URL + "/me/drive/root:/" + encodedPath + ":/content";
        try {
            byte[] content = Files.readAllBytes(localFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<byte[]> entity = new HttpEntity<>(content, headers);
            ResponseEntity<DriveItem> response = graphRestTemplate.exchange(url, HttpMethod.PUT, entity, DriveItem.class);
            return response.getBody();
        } catch (IOException e) {
            throw new OneDriveClientException("Failed to read local file.", e);
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Creates an upload session for large file upload.
     *
     * @param remotePath remote path relative to drive root
     * @return upload session with upload URL
     * @throws OneDriveClientException if API call fails
     */
    public UploadSession createUploadSession(String remotePath) {
        String encodedPath = encodePath(remotePath);
        String url = GRAPH_BASE_URL + "/me/drive/root:/" + encodedPath + ":/createUploadSession";
        try {
            ResponseEntity<UploadSession> response = graphRestTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, UploadSession.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Uploads a chunk of a large file to the upload session URL.
     *
     * @param uploadUrl  upload session URL
     * @param data       chunk data
     * @param rangeStart start byte position
     * @param rangeEnd   end byte position (inclusive)
     * @param totalSize  total file size
     * @return drive item if upload is complete, null if more chunks needed
     * @throws OneDriveClientException if API call fails
     */
    public DriveItem uploadChunk(String uploadUrl, byte[] data, long rangeStart, long rangeEnd, long totalSize) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + totalSize);
            headers.setContentLength(data.length);
            HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
            ResponseEntity<DriveItem> response = chunkRestTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, DriveItem.class);
            if (response.getStatusCode().value() == 200 || response.getStatusCode().value() == 201) {
                return response.getBody();
            }
            return null; // 202 - more chunks needed
        } catch (HttpClientErrorException e) {
            throw new OneDriveClientException("API call failed with status " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }
    }

    private String encodePath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] segments = path.split("/");
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                encoded.append("/");
            }
            encoded.append(URLEncoder.encode(segments[i], StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return encoded.toString();
    }

}

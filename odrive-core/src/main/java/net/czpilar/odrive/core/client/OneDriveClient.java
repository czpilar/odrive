package net.czpilar.odrive.core.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.UploadSession;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * HTTP client for Microsoft Graph API OneDrive operations.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class OneDriveClient {

    private static final String GRAPH_BASE_URL = "https://graph.microsoft.com/v1.0";

    private final HttpClient httpClient;
    private final String accessToken;
    private final Gson gson;

    public OneDriveClient(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().create();
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
        HttpResponse<String> response = doGet(url);
        if (response.statusCode() == 404) {
            return null;
        }
        checkResponse(response);
        return gson.fromJson(response.body(), DriveItem.class);
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
        String body = gson.toJson(new FolderCreateRequest(name));
        HttpResponse<String> response = doPost(url, body, "application/json");
        checkResponse(response);
        return gson.fromJson(response.body(), DriveItem.class);
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
        String body = gson.toJson(new FolderCreateRequest(name));
        HttpResponse<String> response = doPost(url, body, "application/json");
        checkResponse(response);
        return gson.fromJson(response.body(), DriveItem.class);
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(content))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponse(response);
            return gson.fromJson(response.body(), DriveItem.class);
        } catch (IOException | InterruptedException e) {
            throw new OneDriveClientException("Failed to upload file.", e);
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
        HttpResponse<String> response = doPost(url, "{}", "application/json");
        checkResponse(response);
        return gson.fromJson(response.body(), UploadSession.class);
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + totalSize)
                    .header("Content-Length", String.valueOf(data.length))
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return gson.fromJson(response.body(), DriveItem.class);
            } else if (response.statusCode() == 202) {
                return null; // more chunks needed
            }
            checkResponse(response);
            return null;
        } catch (IOException | InterruptedException e) {
            throw new OneDriveClientException("Failed to upload chunk.", e);
        }
    }

    private HttpResponse<String> doGet(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new OneDriveClientException("HTTP GET request failed.", e);
        }
    }

    private HttpResponse<String> doPost(String url, String body, String contentType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new OneDriveClientException("HTTP POST request failed.", e);
        }
    }

    private void checkResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            throw new OneDriveClientException("API call failed with status " + response.statusCode() + ": " + response.body());
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

    @SuppressWarnings("unused")
    private static class FolderCreateRequest {
        private final String name;
        private final Object folder = new Object() {};
        @SuppressWarnings("FieldCanBeLocal")
        private final ConflictBehavior conflictBehavior;

        FolderCreateRequest(String name) {
            this.name = name;
            this.conflictBehavior = null;
        }

        private enum ConflictBehavior {
            fail, rename, replace
        }
    }

    public static class OneDriveClientException extends RuntimeException {
        public OneDriveClientException(String message) {
            super(message);
        }

        public OneDriveClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

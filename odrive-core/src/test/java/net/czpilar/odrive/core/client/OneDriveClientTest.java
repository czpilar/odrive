package net.czpilar.odrive.core.client;

import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.UploadSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OneDriveClientTest {

    private static final String ACCESS_TOKEN = "test-access-token";
    private static final String GRAPH_BASE_URL = "https://graph.microsoft.com/v1.0";

    @Mock
    private RestTemplate restTemplate;

    private OneDriveClient client;

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
        client = new OneDriveClient(restTemplate, ACCESS_TOKEN);
    }

    private static DriveItem driveItem(String id) {
        return new DriveItem(id, null, null, null, null, null, null, null);
    }

    private static DriveItem emptyDriveItem() {
        return new DriveItem(null, null, null, null, null, null, null, null);
    }

    // --- getItemByPath ---

    @Test
    public void testGetItemByPath() {
        DriveItem item = driveItem("item-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(item, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        DriveItem result = client.getItemByPath("Documents/myfile.txt");

        assertNotNull(result);
        assertEquals("item-id", result.id());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root:/Documents/myfile.txt", urlCaptor.getValue());
    }

    @Test
    public void testGetItemByPathNotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        DriveItem result = client.getItemByPath("nonexistent/path");

        assertNull(result);
    }

    @Test
    public void testGetItemByPathApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

        assertThrows(OneDriveClient.OneDriveClientException.class, () -> client.getItemByPath("path"));
    }

    @Test
    public void testGetItemByPathWithLeadingSlash() {
        ResponseEntity<DriveItem> response = new ResponseEntity<>(emptyDriveItem(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        client.getItemByPath("/Documents/test.txt");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root:/Documents/test.txt", urlCaptor.getValue());
    }

    @Test
    public void testGetItemByPathWithSpacesInPath() {
        ResponseEntity<DriveItem> response = new ResponseEntity<>(emptyDriveItem(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        client.getItemByPath("My Documents/my file.txt");

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.GET), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root:/My%20Documents/my%20file.txt", urlCaptor.getValue());
    }

    // --- createFolderAtRoot ---

    @Test
    public void testCreateFolderAtRoot() {
        DriveItem folder = driveItem("folder-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(folder, HttpStatus.CREATED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        DriveItem result = client.createFolderAtRoot("TestFolder");

        assertNotNull(result);
        assertEquals("folder-id", result.id());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root/children", urlCaptor.getValue());
    }

    @Test
    public void testCreateFolderAtRootApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.CONFLICT, "Conflict"));

        assertThrows(OneDriveClient.OneDriveClientException.class, () -> client.createFolderAtRoot("TestFolder"));
    }

    // --- createFolder ---

    @Test
    public void testCreateFolder() {
        DriveItem folder = driveItem("subfolder-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(folder, HttpStatus.CREATED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        DriveItem result = client.createFolder("parent-id", "SubFolder");

        assertNotNull(result);
        assertEquals("subfolder-id", result.id());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/items/parent-id/children", urlCaptor.getValue());
    }

    @Test
    public void testCreateFolderApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

        assertThrows(OneDriveClient.OneDriveClientException.class, () -> client.createFolder("parent-id", "Folder"));
    }

    // --- uploadSmallFile ---

    @Test
    public void testUploadSmallFile(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(localFile.toPath(), "test content");

        DriveItem uploadedItem = driveItem("uploaded-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(uploadedItem, HttpStatus.CREATED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        DriveItem result = client.uploadSmallFile("Documents/test.txt", localFile);

        assertNotNull(result);
        assertEquals("uploaded-id", result.id());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root:/Documents/test.txt:/content", urlCaptor.getValue());
    }

    @Test
    public void testUploadSmallFileWithNonExistentFile() {
        File localFile = new File("non-existent-file.txt");

        assertThrows(OneDriveClient.OneDriveClientException.class,
                () -> client.uploadSmallFile("remote/path.txt", localFile));
    }

    @Test
    public void testUploadSmallFileApiError(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(localFile.toPath(), "test content");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INSUFFICIENT_STORAGE, "Insufficient Storage"));

        assertThrows(OneDriveClient.OneDriveClientException.class,
                () -> client.uploadSmallFile("Documents/test.txt", localFile));
    }

    // --- createUploadSession ---

    @Test
    public void testCreateUploadSession() {
        UploadSession session = new UploadSession("https://upload.example.com/session123", null);
        ResponseEntity<UploadSession> response = new ResponseEntity<>(session, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(UploadSession.class)))
                .thenReturn(response);

        UploadSession result = client.createUploadSession("Documents/large-file.zip");

        assertNotNull(result);
        assertEquals("https://upload.example.com/session123", result.uploadUrl());

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(urlCaptor.capture(), eq(HttpMethod.POST), any(HttpEntity.class), eq(UploadSession.class));
        assertEquals(GRAPH_BASE_URL + "/me/drive/root:/Documents/large-file.zip:/createUploadSession", urlCaptor.getValue());
    }

    @Test
    public void testCreateUploadSessionApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(UploadSession.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Forbidden"));

        assertThrows(OneDriveClient.OneDriveClientException.class,
                () -> client.createUploadSession("Documents/large-file.zip"));
    }

    // --- uploadChunk ---

    @Test
    public void testUploadChunkComplete() {
        DriveItem item = driveItem("completed-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(item, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        byte[] data = "chunk-data".getBytes(StandardCharsets.UTF_8);
        DriveItem result = client.uploadChunk("https://upload.example.com/session", data, 0, 9, 100);

        assertNotNull(result);
        assertEquals("completed-id", result.id());
    }

    @Test
    public void testUploadChunkCreated() {
        DriveItem item = driveItem("created-id");
        ResponseEntity<DriveItem> response = new ResponseEntity<>(item, HttpStatus.CREATED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        byte[] data = "chunk-data".getBytes(StandardCharsets.UTF_8);
        DriveItem result = client.uploadChunk("https://upload.example.com/session", data, 90, 99, 100);

        assertNotNull(result);
        assertEquals("created-id", result.id());
    }

    @Test
    public void testUploadChunkAccepted() {
        ResponseEntity<DriveItem> response = ResponseEntity.status(HttpStatus.ACCEPTED).build();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        byte[] data = "chunk-data".getBytes(StandardCharsets.UTF_8);
        DriveItem result = client.uploadChunk("https://upload.example.com/session", data, 0, 9, 100);

        assertNull(result);
    }

    @Test
    public void testUploadChunkApiError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

        byte[] data = "chunk-data".getBytes(StandardCharsets.UTF_8);
        assertThrows(OneDriveClient.OneDriveClientException.class,
                () -> client.uploadChunk("https://upload.example.com/session", data, 0, 9, 100));
    }

    @Test
    public void testUploadChunkContentRangeHeader() {
        ResponseEntity<DriveItem> response = ResponseEntity.status(HttpStatus.ACCEPTED).build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(DriveItem.class)))
                .thenReturn(response);

        byte[] data = new byte[1024];
        client.uploadChunk("https://upload.example.com/session", data, 1024, 2047, 10240);

        ArgumentCaptor<HttpEntity<byte[]>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("https://upload.example.com/session"), eq(HttpMethod.PUT), entityCaptor.capture(), eq(DriveItem.class));

        HttpHeaders headers = entityCaptor.getValue().getHeaders();
        assertEquals("bytes 1024-2047/10240", headers.getFirst("Content-Range"));
        assertEquals(1024, headers.getContentLength());
    }

    // --- interceptor ---

    @Test
    public void testInterceptorIsRegistered() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        RestTemplate rt = mock(RestTemplate.class);
        when(rt.getInterceptors()).thenReturn(interceptors);

        new OneDriveClient(rt, "my-token");

        assertEquals(1, interceptors.size());
    }

    // --- OneDriveClientException ---

    @Test
    public void testOneDriveClientExceptionWithMessage() {
        OneDriveClient.OneDriveClientException ex = new OneDriveClient.OneDriveClientException("test message");

        assertEquals("test message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void testOneDriveClientExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");
        OneDriveClient.OneDriveClientException ex = new OneDriveClient.OneDriveClientException("test message", cause);

        assertEquals("test message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}

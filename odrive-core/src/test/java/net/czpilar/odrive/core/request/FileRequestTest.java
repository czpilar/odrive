package net.czpilar.odrive.core.request;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.ODriveException;
import net.czpilar.odrive.core.listener.IFileUploadProgressListener;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.UploadSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FileRequestTest {

    @Mock
    private OneDriveClient client;

    @Mock
    private IFileUploadProgressListener progressListener;

    @BeforeEach
    public void before() {
        MockitoAnnotations.openMocks(this);
    }

    private static DriveItem driveItem(String id) {
        return new DriveItem(id, null, null, null, null, null, null, null);
    }

    private static DriveItem emptyDriveItem() {
        return new DriveItem(null, null, null, null, null, null, null, null);
    }

    // --- Small file upload ---

    @Test
    public void testExecuteSmallFile(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("small.txt").toFile();
        Files.writeString(localFile.toPath(), "small file content");

        DriveItem uploadedItem = driveItem("small-file-id");
        when(client.uploadSmallFile(anyString(), any(File.class))).thenReturn(uploadedItem);

        FileRequest request = FileRequest.create(client, "remote/small.txt", localFile);
        request.setProgressListener(progressListener);

        DriveItem result = request.execute();

        assertNotNull(result);
        assertEquals("small-file-id", result.id());

        verify(client).uploadSmallFile("remote/small.txt", localFile);
        verify(progressListener).progressChanged(IFileUploadProgressListener.State.INITIATION, 0);
        verify(progressListener).progressChanged(IFileUploadProgressListener.State.COMPLETE, localFile.length());
        verifyNoMoreInteractions(progressListener);
    }

    @Test
    public void testExecuteSmallFileWithoutProgressListener(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("small.txt").toFile();
        Files.writeString(localFile.toPath(), "small file content");

        DriveItem uploadedItem = emptyDriveItem();
        when(client.uploadSmallFile(anyString(), any(File.class))).thenReturn(uploadedItem);

        FileRequest request = FileRequest.create(client, "remote/small.txt", localFile);

        DriveItem result = request.execute();

        assertNotNull(result);
        verify(client).uploadSmallFile("remote/small.txt", localFile);
    }

    // --- Chunked upload ---

    @Test
    public void testExecuteChunkedUpload(@TempDir Path tempDir) throws IOException {
        // Create file larger than SMALL_FILE_LIMIT (4 MB)
        File localFile = tempDir.resolve("large.bin").toFile();
        byte[] content = new byte[FileRequest.SMALL_FILE_LIMIT + 1024];
        Files.write(localFile.toPath(), content);

        UploadSession session = new UploadSession("https://upload.example.com/session", null);
        when(client.createUploadSession("remote/large.bin")).thenReturn(session);

        // First chunks return null (202 Accepted), last chunk returns DriveItem
        DriveItem completedItem = driveItem("large-file-id");
        when(client.uploadChunk(anyString(), any(byte[].class), anyLong(), anyLong(), anyLong()))
                .thenReturn(null)   // first chunk
                .thenReturn(null)   // second chunk
                .thenReturn(completedItem); // last chunk

        FileRequest request = FileRequest.create(client, "remote/large.bin", localFile);
        request.setProgressListener(progressListener);

        DriveItem result = request.execute();

        assertNotNull(result);
        assertEquals("large-file-id", result.id());

        verify(client).createUploadSession("remote/large.bin");
        verify(client, atLeastOnce()).uploadChunk(eq("https://upload.example.com/session"),
                any(byte[].class), anyLong(), anyLong(), eq((long) content.length));
        verify(progressListener).progressChanged(IFileUploadProgressListener.State.INITIATION, 0);
        verify(progressListener, atLeastOnce()).progressChanged(eq(IFileUploadProgressListener.State.IN_PROGRESS), anyLong());
        verify(progressListener).progressChanged(eq(IFileUploadProgressListener.State.COMPLETE), anyLong());
    }

    @Test
    public void testExecuteChunkedUploadWithNullSession(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("large.bin").toFile();
        byte[] content = new byte[FileRequest.SMALL_FILE_LIMIT + 1024];
        Files.write(localFile.toPath(), content);

        when(client.createUploadSession("remote/large.bin")).thenReturn(null);

        FileRequest request = FileRequest.create(client, "remote/large.bin", localFile);

        assertThrows(ODriveException.class, () -> request.execute());
    }

    @Test
    public void testExecuteChunkedUploadWithNullUploadUrl(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("large.bin").toFile();
        byte[] content = new byte[FileRequest.SMALL_FILE_LIMIT + 1024];
        Files.write(localFile.toPath(), content);

        UploadSession session = new UploadSession(null, null);
        when(client.createUploadSession("remote/large.bin")).thenReturn(session);

        FileRequest request = FileRequest.create(client, "remote/large.bin", localFile);

        assertThrows(ODriveException.class, () -> request.execute());
    }

    // --- Retry logic ---

    @Test
    public void testUploadChunkRetriesOnFailure(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("large.bin").toFile();
        byte[] content = new byte[FileRequest.SMALL_FILE_LIMIT + 100];
        Files.write(localFile.toPath(), content);

        UploadSession session = new UploadSession("https://upload.example.com/session", null);
        when(client.createUploadSession("remote/large.bin")).thenReturn(session);

        DriveItem completedItem = driveItem("retry-success-id");

        // First call fails, second succeeds
        when(client.uploadChunk(anyString(), any(byte[].class), anyLong(), anyLong(), anyLong()))
                .thenThrow(new OneDriveClient.OneDriveClientException("Transient error"))
                .thenReturn(completedItem);

        FileRequest request = FileRequest.create(client, "remote/large.bin", localFile);
        DriveItem result = request.execute();

        assertNotNull(result);
        assertEquals("retry-success-id", result.id());
        verify(client, times(2)).uploadChunk(anyString(), any(byte[].class), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testUploadChunkExhaustsRetries(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("large.bin").toFile();
        byte[] content = new byte[FileRequest.SMALL_FILE_LIMIT + 100];
        Files.write(localFile.toPath(), content);

        UploadSession session = new UploadSession("https://upload.example.com/session", null);
        when(client.createUploadSession("remote/large.bin")).thenReturn(session);

        when(client.uploadChunk(anyString(), any(byte[].class), anyLong(), anyLong(), anyLong()))
                .thenThrow(new OneDriveClient.OneDriveClientException("Persistent error"));

        FileRequest request = FileRequest.create(client, "remote/large.bin", localFile);

        assertThrows(OneDriveClient.OneDriveClientException.class, () -> request.execute());
        // Should be called CHUNK_RETRIES + 1 times (initial + retries)
        verify(client, times(FileRequest.CHUNK_RETRIES + 1))
                .uploadChunk(anyString(), any(byte[].class), anyLong(), anyLong(), anyLong());
    }

    // --- Constants ---

    @Test
    public void testChunkSizeIsMultipleOf320KiB() {
        assertEquals(0, FileRequest.CHUNK_SIZE % 327680);
    }

    @Test
    public void testSmallFileLimitIs4MB() {
        assertEquals(4 * 1024 * 1024, FileRequest.SMALL_FILE_LIMIT);
    }

    @Test
    public void testChunkRetriesIs5() {
        assertEquals(5, FileRequest.CHUNK_RETRIES);
    }

    // --- Factory method ---

    @Test
    public void testCreateFactoryMethod(@TempDir Path tempDir) throws IOException {
        File localFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(localFile.toPath(), "content");

        FileRequest request = FileRequest.create(client, "remote/test.txt", localFile);

        assertNotNull(request);
    }
}

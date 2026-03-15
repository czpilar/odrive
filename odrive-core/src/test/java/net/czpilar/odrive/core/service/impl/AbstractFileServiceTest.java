package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.FileHandleException;
import net.czpilar.odrive.core.exception.OneDriveClientException;
import net.czpilar.odrive.core.model.DriveItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbstractFileServiceTest {

    @Mock
    private AbstractFileService service;

    @Mock
    private OneDriveClient oneDriveClient;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        when(service.getOneDriveClient()).thenReturn(oneDriveClient);
    }

    @AfterEach
    public void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testGetPathWhereFilenameIsNullAndParentIsNull() {
        when(service.getPath(any(), any())).thenCallRealMethod();

        assertThrows(IllegalArgumentException.class, () -> service.getPath(null, null));

        verify(service).getPath(null, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testGetPathWhereParentIsNull() {
        String filename = "test-filename";
        when(service.getPath(anyString(), any())).thenCallRealMethod();

        String result = service.getPath(filename, null);

        assertNotNull(result);
        assertEquals("/" + filename, result);

        verify(service).getPath(filename, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testGetPathWhereParentExists() {
        String filename = "test-filename";
        DriveItem parent = mock(DriveItem.class);
        when(service.getPath(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(parent.getPathDisplay()).thenReturn("/parent-path");

        String result = service.getPath(filename, parent);

        assertNotNull(result);
        assertEquals("/parent-path/" + filename, result);

        verify(service).getPath(filename, parent);
        verify(parent).getPathDisplay();

        verifyNoMoreInteractions(service);
        verifyNoMoreInteractions(parent);
    }

    @Test
    public void testGetPathWhereFilenameStartsWithSlash() {
        String filename = "/test-filename";
        when(service.getPath(anyString(), any())).thenCallRealMethod();

        String result = service.getPath(filename, null);

        assertNotNull(result);
        assertEquals("/test-filename", result);

        verify(service).getPath(filename, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void testFindFolderWhereEntryIsFolder() {
        String filename = "test-folder";
        DriveItem parent = mock(DriveItem.class);
        DriveItem folder = mock(DriveItem.class);

        when(service.findFolder(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-folder");
        when(oneDriveClient.getItemByPath("/test-folder")).thenReturn(folder);
        when(folder.isFolder()).thenReturn(true);

        DriveItem result = service.findFolder(filename, parent);

        assertNotNull(result);
        assertEquals(folder, result);
    }

    @Test
    public void testFindFolderWhereEntryIsFile() {
        String filename = "test-file";
        DriveItem parent = mock(DriveItem.class);
        DriveItem file = mock(DriveItem.class);

        when(service.findFolder(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-file");
        when(oneDriveClient.getItemByPath("/test-file")).thenReturn(file);
        when(file.isFolder()).thenReturn(false);

        DriveItem result = service.findFolder(filename, parent);

        assertNull(result);
    }

    @Test
    public void testFindFolderWhereEntryIsNull() {
        String filename = "test-folder";
        DriveItem parent = mock(DriveItem.class);

        when(service.findFolder(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-folder");
        when(oneDriveClient.getItemByPath("/test-folder")).thenReturn(null);

        DriveItem result = service.findFolder(filename, parent);

        assertNull(result);
    }

    @Test
    public void testFindFileWhereEntryIsFile() {
        String filename = "test-file";
        DriveItem parent = mock(DriveItem.class);
        DriveItem file = mock(DriveItem.class);

        when(service.findFile(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-file");
        when(oneDriveClient.getItemByPath("/test-file")).thenReturn(file);
        when(file.isFile()).thenReturn(true);

        DriveItem result = service.findFile(filename, parent);

        assertNotNull(result);
        assertEquals(file, result);
    }

    @Test
    public void testFindFileWhereEntryIsFolder() {
        String filename = "test-folder";
        DriveItem parent = mock(DriveItem.class);
        DriveItem folder = mock(DriveItem.class);

        when(service.findFile(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-folder");
        when(oneDriveClient.getItemByPath("/test-folder")).thenReturn(folder);
        when(folder.isFile()).thenReturn(false);

        DriveItem result = service.findFile(filename, parent);

        assertNull(result);
    }

    @Test
    public void testFindFileWhereExceptionThrown() {
        String filename = "test-file";
        DriveItem parent = mock(DriveItem.class);

        when(service.findFile(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-file");
        when(oneDriveClient.getItemByPath("/test-file")).thenThrow(new OneDriveClientException("error"));

        assertThrows(FileHandleException.class, () -> service.findFile(filename, parent));
    }
}

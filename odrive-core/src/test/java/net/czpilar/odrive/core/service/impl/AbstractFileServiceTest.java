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

class AbstractFileServiceTest {

    @Mock
    private AbstractFileService service;

    @Mock
    private OneDriveClient oneDriveClient;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        when(service.getOneDriveClient()).thenReturn(oneDriveClient);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testGetPathWhereFilenameIsNullAndParentIsNull() {
        when(service.getPath(any(), any())).thenCallRealMethod();

        assertThrows(IllegalArgumentException.class, () -> service.getPath(null, null));

        verify(service).getPath(null, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testGetPathWhereParentIsNull() {
        String filename = "test-filename";
        when(service.getPath(anyString(), any())).thenCallRealMethod();

        String result = service.getPath(filename, null);

        assertNotNull(result);
        assertEquals("/" + filename, result);

        verify(service).getPath(filename, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testGetPathWhereParentExists() {
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
    void testGetPathWhereFilenameStartsWithSlash() {
        String filename = "/test-filename";
        when(service.getPath(anyString(), any())).thenCallRealMethod();

        String result = service.getPath(filename, null);

        assertNotNull(result);
        assertEquals("/test-filename", result);

        verify(service).getPath(filename, null);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testFindFolderWhereEntryIsFolder() {
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
    void testFindFolderWhereEntryIsFile() {
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
    void testFindFolderWhereEntryIsNull() {
        String filename = "test-folder";
        DriveItem parent = mock(DriveItem.class);

        when(service.findFolder(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-folder");
        when(oneDriveClient.getItemByPath("/test-folder")).thenReturn(null);

        DriveItem result = service.findFolder(filename, parent);

        assertNull(result);
    }

    @Test
    void testFindFileWhereEntryIsFile() {
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
    void testFindFileWhereEntryIsFolder() {
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
    void testFindFileWhereExceptionThrown() {
        String filename = "test-file";
        DriveItem parent = mock(DriveItem.class);

        when(service.findFile(anyString(), any())).thenCallRealMethod();
        when(service.getPath(anyString(), any())).thenReturn("/test-file");
        when(oneDriveClient.getItemByPath("/test-file")).thenThrow(new OneDriveClientException("error"));

        assertThrows(FileHandleException.class, () -> service.findFile(filename, parent));
    }
}

package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.exception.DirectoryHandleException;
import net.czpilar.odrive.core.exception.OneDriveClientException;
import net.czpilar.odrive.core.model.DriveItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectoryServiceTest {

    private final DirectoryService service = new DirectoryService();

    @Mock
    private DirectoryService serviceMock;

    @Mock
    private OneDriveClient oneDriveClient;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        when(serviceMock.getOneDriveClient()).thenReturn(oneDriveClient);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreateOneDirectoryWhereParentDirIsNull() {
        String dirname = "test-dirname";
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.createOneDirectory(anyString(), any())).thenCallRealMethod();
        when(oneDriveClient.createFolderAtRoot(dirname)).thenReturn(directory);

        DriveItem result = serviceMock.createOneDirectory(dirname, null);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).createOneDirectory(dirname, null);
        verify(serviceMock).getOneDriveClient();
        verify(oneDriveClient).createFolderAtRoot(dirname);

        verifyNoMoreInteractions(serviceMock);
        verifyNoMoreInteractions(oneDriveClient);
        verifyNoInteractions(directory);
    }

    @Test
    void testCreateOneDirectoryWhereParentDirExists() {
        String dirname = "test-dirname";
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.createOneDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(oneDriveClient.createFolder("test-parent-dir-id", dirname)).thenReturn(directory);
        when(parentDir.id()).thenReturn("test-parent-dir-id");

        DriveItem result = serviceMock.createOneDirectory(dirname, parentDir);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).createOneDirectory(dirname, parentDir);
        verify(serviceMock).getOneDriveClient();
        verify(oneDriveClient).createFolder("test-parent-dir-id", dirname);
        verify(parentDir).id();

        verifyNoMoreInteractions(serviceMock);
        verifyNoMoreInteractions(oneDriveClient);
        verifyNoMoreInteractions(parentDir);
        verifyNoInteractions(directory);
    }

    @Test
    void testCreateOneDirectoryWhereExceptionWasThrown() {
        String dirname = "test-dirname";

        when(serviceMock.createOneDirectory(anyString(), any())).thenCallRealMethod();
        when(oneDriveClient.createFolderAtRoot(dirname)).thenThrow(new OneDriveClientException("error"));

        assertThrows(DirectoryHandleException.class, () -> serviceMock.createOneDirectory(dirname, null));

        verify(serviceMock).createOneDirectory(dirname, null);
        verify(serviceMock).getOneDriveClient();
        verify(oneDriveClient).createFolderAtRoot(dirname);

        verifyNoMoreInteractions(serviceMock);
        verifyNoMoreInteractions(oneDriveClient);
    }

    @Test
    void testFindOrCreateOneDirectoryWhereDirectoryIsFound() {
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory = mock(DriveItem.class);
        String dirname = "test-dirname";

        when(serviceMock.findOrCreateOneDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findFolder(anyString(), any(DriveItem.class))).thenReturn(directory);

        DriveItem result = serviceMock.findOrCreateOneDirectory(dirname, parentDir);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findOrCreateOneDirectory(dirname, parentDir);
        verify(serviceMock).findFolder(dirname, parentDir);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(parentDir);
        verifyNoInteractions(directory);
    }

    @Test
    void testFindOrCreateOneDirectoryWhereDirectoryIsCreated() {
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory = mock(DriveItem.class);
        String dirname = "test-dirname";

        when(serviceMock.findOrCreateOneDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findFolder(anyString(), any(DriveItem.class))).thenReturn(null);
        when(serviceMock.createOneDirectory(anyString(), any(DriveItem.class))).thenReturn(directory);

        DriveItem result = serviceMock.findOrCreateOneDirectory(dirname, parentDir);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findOrCreateOneDirectory(dirname, parentDir);
        verify(serviceMock).findFolder(dirname, parentDir);
        verify(serviceMock).createOneDirectory(dirname, parentDir);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(parentDir);
        verifyNoInteractions(directory);
    }

    @Test
    void testFindDirectoryWithPathname() {
        String pathname = "test-pathname";
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.findDirectory(anyString())).thenCallRealMethod();
        when(serviceMock.findDirectory(anyString(), any())).thenReturn(directory);

        DriveItem result = serviceMock.findDirectory(pathname);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findDirectory(pathname);
        verify(serviceMock).findDirectory(pathname, null);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testFindDirectoryWithPathnameAndParentWhereDirnameIsNull() {
        when(serviceMock.findDirectory(any(), any(DriveItem.class))).thenCallRealMethod();

        DriveItem result = serviceMock.findDirectory(null, null);

        assertNull(result);

        verify(serviceMock).findDirectory(null, null);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testFindDirectoryWithPathnameAndParentWherePathnameHasMoreDirsButCurrentDirIsNull() {
        String dirname1 = "test-dirname1";
        String dirname2 = "test-dirname2";
        String pathname = dirname1 + "/" + dirname2;
        DriveItem parentDir = mock(DriveItem.class);

        when(serviceMock.findDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findFolder(anyString(), any(DriveItem.class))).thenReturn(null);

        DriveItem result = serviceMock.findDirectory(pathname, parentDir);

        assertNull(result);

        verify(serviceMock).findDirectory(pathname, parentDir);
        verify(serviceMock).findFolder(dirname1, parentDir);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(parentDir);
    }

    @Test
    void testFindDirectoryWithPathnameAndParentWherePathnameHasOneDir() {
        String pathname = "test-dirname";
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.findDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findFolder(anyString(), any(DriveItem.class))).thenReturn(directory);

        DriveItem result = serviceMock.findDirectory(pathname, parentDir);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findDirectory(pathname, parentDir);
        verify(serviceMock).findFolder(pathname, parentDir);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(parentDir);
        verifyNoInteractions(directory);
    }

    @Test
    void testFindDirectoryWithPathnameAndParentWherePathnameHasMoreDirs() {
        String dirname1 = "test-dirname1";
        String dirname2 = "test-dirname2";
        String dirname3 = "test-dirname3";
        String pathname = dirname1 + "/" + dirname2 + "/" + dirname3;
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory1 = mock(DriveItem.class);
        DriveItem directory2 = mock(DriveItem.class);
        DriveItem directory3 = mock(DriveItem.class);

        when(serviceMock.findDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findFolder(anyString(), any(DriveItem.class))).thenReturn(directory1, directory2, directory3);

        DriveItem result = serviceMock.findDirectory(pathname, parentDir);

        assertNotNull(result);
        assertEquals(directory3, result);

        verify(serviceMock).findDirectory(pathname, parentDir);
        verify(serviceMock).findDirectory(dirname2 + "/" + dirname3, directory1);
        verify(serviceMock).findDirectory(dirname3, directory2);
        verify(serviceMock).findFolder(dirname1, parentDir);
        verify(serviceMock).findFolder(dirname2, directory1);
        verify(serviceMock).findFolder(dirname3, directory2);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(directory1);
        verifyNoInteractions(directory2);
        verifyNoInteractions(directory3);
        verifyNoInteractions(parentDir);
    }

    @Test
    void testFindOrCreateDirectoryWithPathname() {
        String pathname = "test-pathname";
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.findOrCreateDirectory(anyString())).thenCallRealMethod();
        when(serviceMock.findOrCreateDirectory(anyString(), any())).thenReturn(directory);

        DriveItem result = serviceMock.findOrCreateDirectory(pathname);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findOrCreateDirectory(pathname);
        verify(serviceMock).findOrCreateDirectory(pathname, null);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testFindOrCreateDirectoryWithPathnameAndParentWhereDirnameIsNull() {
        when(serviceMock.findOrCreateDirectory(any(), any(DriveItem.class))).thenCallRealMethod();

        DriveItem result = serviceMock.findOrCreateDirectory(null, null);

        assertNull(result);

        verify(serviceMock).findOrCreateDirectory(null, null);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testFindOrCreateDirectoryWithPathnameAndParentWherePathnameHasOneDir() {
        String pathname = "test-dirname";
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory = mock(DriveItem.class);

        when(serviceMock.findOrCreateDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findOrCreateOneDirectory(anyString(), any(DriveItem.class))).thenReturn(directory);

        DriveItem result = serviceMock.findOrCreateDirectory(pathname, parentDir);

        assertNotNull(result);
        assertEquals(directory, result);

        verify(serviceMock).findOrCreateDirectory(pathname, parentDir);
        verify(serviceMock).findOrCreateOneDirectory(pathname, parentDir);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(directory);
        verifyNoInteractions(parentDir);
    }

    @Test
    void testFindOrCreateDirectoryWithPathnameAndParentWherePathnameHasMoreDirs() {
        String dirname1 = "test-dirname1";
        String dirname2 = "test-dirname2";
        String dirname3 = "test-dirname3";
        String pathname = dirname1 + "/" + dirname2 + "/" + dirname3;
        DriveItem parentDir = mock(DriveItem.class);
        DriveItem directory1 = mock(DriveItem.class);
        DriveItem directory2 = mock(DriveItem.class);
        DriveItem directory3 = mock(DriveItem.class);

        when(serviceMock.findOrCreateDirectory(anyString(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.findOrCreateOneDirectory(anyString(), any(DriveItem.class))).thenReturn(directory1, directory2, directory3);

        DriveItem result = serviceMock.findOrCreateDirectory(pathname, parentDir);

        assertNotNull(result);
        assertEquals(directory3, result);

        verify(serviceMock).findOrCreateDirectory(pathname, parentDir);
        verify(serviceMock).findOrCreateDirectory(dirname2 + "/" + dirname3, directory1);
        verify(serviceMock).findOrCreateDirectory(dirname3, directory2);
        verify(serviceMock).findOrCreateOneDirectory(dirname1, parentDir);
        verify(serviceMock).findOrCreateOneDirectory(dirname2, directory1);
        verify(serviceMock).findOrCreateOneDirectory(dirname3, directory2);

        verifyNoMoreInteractions(serviceMock);

        verifyNoInteractions(directory1);
        verifyNoInteractions(directory2);
        verifyNoInteractions(directory3);
        verifyNoInteractions(parentDir);
    }
}

package net.czpilar.odrive.core.service.impl;

import net.czpilar.odrive.core.client.OneDriveClient;
import net.czpilar.odrive.core.credential.IODriveCredential;
import net.czpilar.odrive.core.exception.FileHandleException;
import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.service.IDirectoryService;
import net.czpilar.odrive.core.util.EqualUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    private final FileService service = new FileService(3);

    @Mock
    private FileService serviceMock;

    @Mock
    private IDirectoryService directoryService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private OneDriveClient oneDriveClient;

    @Mock
    private IODriveCredential oDriveCredential;

    private AutoCloseable autoCloseable;

    private MockedStatic<EqualUtils> equalUtilsMockedStatic;

    @BeforeEach
    void before() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        service.setApplicationContext(applicationContext);
        service.setODriveCredential(oDriveCredential);
        service.setDirectoryService(directoryService);

        when(serviceMock.getDirectoryService()).thenReturn(directoryService);
        when(applicationContext.getBean(OneDriveClient.class)).thenReturn(oneDriveClient);

        equalUtilsMockedStatic = mockStatic(EqualUtils.class);
    }

    @AfterEach
    void after() throws Exception {
        autoCloseable.close();
        equalUtilsMockedStatic.close();
    }

    @Test
    void testGetDirectoryService() {
        IDirectoryService result = service.getDirectoryService();

        assertNotNull(result);
        assertEquals(directoryService, result);
    }

    @Test
    void testGetUploadDir() {
        String uploadDirName = "test-upload-dir";

        String result = service.getUploadDir(uploadDirName);

        assertNotNull(result);
        assertEquals(uploadDirName, result);

        verifyNoInteractions(oDriveCredential);
    }

    @Test
    void testGetUploadDirWithNullUploadDir() {
        String uploadDirName = "test-default-upload-dir";

        when(oDriveCredential.getUploadDir()).thenReturn(uploadDirName);

        String result = service.getUploadDir(null);

        assertNotNull(result);
        assertEquals(uploadDirName, result);

        verify(oDriveCredential).getUploadDir();

        verifyNoMoreInteractions(oDriveCredential);
    }

    @Test
    void testUploadFilesWithNullFilenames() {
        DriveItem parent = mock(DriveItem.class);

        List<DriveItem> result = service.uploadFiles(null, parent);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUploadFilesWithEmptyListOfFilenames() {
        DriveItem parent = mock(DriveItem.class);

        List<DriveItem> result = service.uploadFiles(List.of(), parent);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUploadFilesWhereUploadFileThrowsException() {
        DriveItem parent = mock(DriveItem.class);

        when(serviceMock.uploadFiles(anyList(), any(DriveItem.class))).thenCallRealMethod();
        when(serviceMock.uploadFile(anyString(), any(DriveItem.class))).thenThrow(FileHandleException.class);

        List<DriveItem> result = serviceMock.uploadFiles(Arrays.asList("filename1", "filename2"), parent);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(serviceMock).uploadFiles(anyList(), any(DriveItem.class));
        verify(serviceMock).uploadFile("filename1", parent);
        verify(serviceMock).uploadFile("filename2", parent);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testUploadFilesWithListOfFilenames() {
        DriveItem parent = mock(DriveItem.class);

        when(serviceMock.uploadFiles(anyList(), any(DriveItem.class))).thenCallRealMethod();
        DriveItem file1 = mock(DriveItem.class);
        DriveItem file2 = mock(DriveItem.class);
        when(serviceMock.uploadFile(anyString(), any(DriveItem.class))).thenReturn(file1, file2);

        List<DriveItem> result = serviceMock.uploadFiles(Arrays.asList("filename1", "filename2"), parent);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(file1, result.get(0));
        assertEquals(file2, result.get(1));

        verify(serviceMock).uploadFiles(anyList(), any(DriveItem.class));
        verify(serviceMock).uploadFile("filename1", parent);
        verify(serviceMock).uploadFile("filename2", parent);

        verifyNoMoreInteractions(serviceMock);
    }

    @Test
    void testUploadFileWithStringFilenameAndStringParentDir() {
        String pathname = "test-parent-dir";
        String filename = "test-filename";
        DriveItem file = mock(DriveItem.class);
        DriveItem parent = mock(DriveItem.class);

        when(serviceMock.uploadFile(anyString(), anyString())).thenCallRealMethod();
        when(serviceMock.uploadFile(anyString(), any(DriveItem.class))).thenReturn(file);
        when(serviceMock.getUploadDir(anyString())).thenReturn(pathname);
        when(directoryService.findOrCreateDirectory(anyString())).thenReturn(parent);

        DriveItem result = serviceMock.uploadFile(filename, pathname);

        assertNotNull(result);
        assertEquals(file, result);

        verify(serviceMock).uploadFile(filename, pathname);
        verify(serviceMock).uploadFile(filename, parent);
        verify(serviceMock).getUploadDir(pathname);
        verify(serviceMock).getDirectoryService();
        verify(directoryService).findOrCreateDirectory(anyString());

        verifyNoMoreInteractions(serviceMock);
        verifyNoMoreInteractions(directoryService);
        verifyNoInteractions(file);
        verifyNoInteractions(parent);
    }

    @Test
    void testUploadFileWithStringFilename() {
        String filename = "test-filename";
        DriveItem file = mock(DriveItem.class);

        when(serviceMock.uploadFile(anyString())).thenCallRealMethod();
        when(serviceMock.uploadFile(anyString(), (DriveItem) any())).thenReturn(file);

        DriveItem result = serviceMock.uploadFile(filename);

        assertNotNull(result);
        assertEquals(file, result);

        verify(serviceMock).uploadFile(filename);
        verify(serviceMock).uploadFile(filename, (DriveItem) null);

        verifyNoMoreInteractions(serviceMock);
        verifyNoInteractions(file);
    }

    @Test
    void testUploadFilesWithStringFilenames() {
        List<String> filenames = Arrays.asList("test-filename1", "test-filename2");
        DriveItem file1 = mock(DriveItem.class);
        DriveItem file2 = mock(DriveItem.class);
        List<DriveItem> files = Arrays.asList(file1, file2);

        when(serviceMock.uploadFiles(anyList())).thenCallRealMethod();
        when(serviceMock.uploadFiles(anyList(), (DriveItem) any())).thenReturn(files);

        List<DriveItem> result = serviceMock.uploadFiles(filenames);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(files, result);

        verify(serviceMock).uploadFiles(filenames);
        verify(serviceMock).uploadFiles(filenames, (DriveItem) null);

        verifyNoMoreInteractions(serviceMock);
        verifyNoInteractions(file1);
        verifyNoInteractions(file2);
    }

    @Test
    void testUploadFilesWithStringFilenamesAndStringParentDir() {
        String parentDir = "test-parent-dir";
        List<String> filenames = Arrays.asList("test-filename1", "test-filename2");
        DriveItem file1 = mock(DriveItem.class);
        DriveItem file2 = mock(DriveItem.class);
        List<DriveItem> files = Arrays.asList(file1, file2);
        DriveItem parent = mock(DriveItem.class);

        when(serviceMock.uploadFiles(anyList(), anyString())).thenCallRealMethod();
        when(serviceMock.uploadFiles(anyList(), any(DriveItem.class))).thenReturn(files);
        when(serviceMock.getUploadDir(anyString())).thenReturn(parentDir);
        when(directoryService.findOrCreateDirectory(anyString())).thenReturn(parent);

        List<DriveItem> result = serviceMock.uploadFiles(filenames, parentDir);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(files, result);

        verify(serviceMock).uploadFiles(filenames, parentDir);
        verify(serviceMock).uploadFiles(filenames, parent);
        verify(serviceMock).getUploadDir(parentDir);
        verify(serviceMock).getDirectoryService();
        verify(directoryService).findOrCreateDirectory(anyString());

        verifyNoMoreInteractions(serviceMock);
        verifyNoMoreInteractions(directoryService);
        verifyNoInteractions(file1);
        verifyNoInteractions(file2);
        verifyNoInteractions(parent);
    }
}

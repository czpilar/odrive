package net.czpilar.odrive.core.util;

import net.czpilar.odrive.core.model.DriveItem;
import net.czpilar.odrive.core.model.FileSystemInfo;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EqualUtilsTest {

    private java.io.File testFile;

    @BeforeEach
    public void before() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        testFile = new java.io.File(tempDir + "some-test-file-for-equal-" + System.currentTimeMillis() + ".properties");
        FileUtils.writeStringToFile(testFile, "Some test file data to store.", Charset.defaultCharset(), false);
    }

    @AfterEach
    public void after() throws IOException {
        Files.deleteIfExists(testFile.toPath());
    }

    @Test
    public void testEqualsWhereBothParametersAreNull() {
        boolean result = EqualUtils.equals(null, null);

        assertFalse(result);
    }

    @Test
    public void testEqualsWhereRemoteFileIsNull() {
        Path path = mock(Path.class);
        boolean result = EqualUtils.equals(null, path);

        assertFalse(result);
    }

    @Test
    public void testEqualsWherePathIsNull() {
        DriveItem item = mock(DriveItem.class);
        boolean result = EqualUtils.equals(item, null);

        assertFalse(result);
    }

    @Test
    public void testEqualsWhereFileNotExists() {
        DriveItem item = mock(DriveItem.class);
        Path path = mock(Path.class);
        java.io.File ioFile = new java.io.File("invalid-file-to-test.qwerty");
        when(path.toFile()).thenReturn(ioFile);

        boolean result = EqualUtils.equals(item, path);

        assertFalse(result);
    }

    @Test
    public void testEqualsWhereNotEqualWhereLengthIsEqualAndRemoteLastModifiedIsLower() {
        Path path = Paths.get(testFile.getPath());
        DriveItem item = mock(DriveItem.class);
        FileSystemInfo fsi = mock(FileSystemInfo.class);
        long lastModified = path.toFile().lastModified();
        String isoTime = Instant.ofEpochMilli(lastModified - 2000).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        when(item.size()).thenReturn(path.toFile().length());
        when(item.fileSystemInfo()).thenReturn(fsi);
        when(fsi.lastModifiedDateTime()).thenReturn(isoTime);

        boolean result = EqualUtils.equals(item, path);

        assertFalse(result);
    }

    @Test
    public void testEqualsWhereNotEqualWhereLengthIsNotEqual() {
        Path path = Paths.get(testFile.getPath());
        DriveItem item = mock(DriveItem.class);
        FileSystemInfo fsi = mock(FileSystemInfo.class);
        long lastModified = path.toFile().lastModified();
        String isoTime = Instant.ofEpochMilli(lastModified).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        when(item.size()).thenReturn(path.toFile().length() + 1);
        when(item.fileSystemInfo()).thenReturn(fsi);
        when(fsi.lastModifiedDateTime()).thenReturn(isoTime);

        boolean result = EqualUtils.equals(item, path);

        assertFalse(result);
    }

    @Test
    public void testEqualsWhereEqualWhereLengthIsEqualAndLastModifiedIsEqual() {
        Path path = Paths.get(testFile.getPath());
        DriveItem item = mock(DriveItem.class);
        FileSystemInfo fsi = mock(FileSystemInfo.class);
        long lastModified = path.toFile().lastModified();
        String isoTime = Instant.ofEpochMilli(lastModified).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        when(item.size()).thenReturn(path.toFile().length());
        when(item.fileSystemInfo()).thenReturn(fsi);
        when(fsi.lastModifiedDateTime()).thenReturn(isoTime);

        boolean result = EqualUtils.equals(item, path);

        assertTrue(result);
    }

    @Test
    public void testEqualsWhereEqualWhereLengthIsEqualAndRemoteLastModifiedIsGreater() {
        Path path = Paths.get(testFile.getPath());
        DriveItem item = mock(DriveItem.class);
        FileSystemInfo fsi = mock(FileSystemInfo.class);
        long lastModified = path.toFile().lastModified();
        String isoTime = Instant.ofEpochMilli(lastModified + 2000).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        when(item.size()).thenReturn(path.toFile().length());
        when(item.fileSystemInfo()).thenReturn(fsi);
        when(fsi.lastModifiedDateTime()).thenReturn(isoTime);

        boolean result = EqualUtils.equals(item, path);

        assertTrue(result);
    }

    @Test
    public void testNotEquals1() {
        try (MockedStatic<EqualUtils> equalUtilsMockedStatic = Mockito.mockStatic(EqualUtils.class)) {
            equalUtilsMockedStatic.when(() -> EqualUtils.notEquals(any(DriveItem.class), any(Path.class))).thenCallRealMethod();
            equalUtilsMockedStatic.when(() -> EqualUtils.equals(any(DriveItem.class), any(Path.class))).thenReturn(true);

            boolean result = EqualUtils.notEquals(mock(DriveItem.class), mock(Path.class));

            assertFalse(result);
        }
    }

    @Test
    public void testNotEquals2() {
        try (MockedStatic<EqualUtils> equalUtilsMockedStatic = Mockito.mockStatic(EqualUtils.class)) {
            equalUtilsMockedStatic.when(() -> EqualUtils.notEquals(any(DriveItem.class), any(Path.class))).thenCallRealMethod();
            equalUtilsMockedStatic.when(() -> EqualUtils.equals(any(DriveItem.class), any(Path.class))).thenReturn(false);

            boolean result = EqualUtils.notEquals(mock(DriveItem.class), mock(Path.class));

            assertTrue(result);
        }
    }
}

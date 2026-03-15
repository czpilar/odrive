package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DriveItemTest {

    @Test
    void testIsFolderWhenFolderIsNotNull() {
        DriveItem item = new DriveItem(null, null, null, null, new Object(), null, null, null);

        assertTrue(item.isFolder());
    }

    @Test
    void testIsFolderWhenFolderIsNull() {
        DriveItem item = new DriveItem(null, null, null, null, null, null, null, null);

        assertFalse(item.isFolder());
    }

    @Test
    void testIsFileWhenFileIsNotNull() {
        DriveItem item = new DriveItem(null, null, null, null, null, new Object(), null, null);

        assertTrue(item.isFile());
    }

    @Test
    void testIsFileWhenFileIsNull() {
        DriveItem item = new DriveItem(null, null, null, null, null, null, null, null);

        assertFalse(item.isFile());
    }

    @Test
    void testGetPathDisplayWithParentPathAndName() {
        ParentReference ref = new ParentReference(null, "/drive/root:/Documents/Subfolder");
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, ref, null);

        assertEquals("/Documents/Subfolder/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithRootParentPath() {
        ParentReference ref = new ParentReference(null, "/drive/root:");
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, ref, null);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithSlashAfterColon() {
        ParentReference ref = new ParentReference(null, "/drive/root:/");
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, ref, null);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithNullName() {
        ParentReference ref = new ParentReference(null, "/drive/root:/Documents");
        DriveItem item = new DriveItem(null, null, null, null, null, null, ref, null);

        assertEquals("/Documents", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithNullParentReference() {
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, null, null);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithNullParentPath() {
        ParentReference ref = new ParentReference(null, null);
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, ref, null);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithNullParentReferenceAndNullName() {
        DriveItem item = new DriveItem(null, null, null, null, null, null, null, null);

        assertEquals("", item.getPathDisplay());
    }

    @Test
    void testGetPathDisplayWithPathNotContainingColon() {
        ParentReference ref = new ParentReference(null, "no-colon-path");
        DriveItem item = new DriveItem(null, "myfile.txt", null, null, null, null, ref, null);

        // No colon found, parentPath stays empty
        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    void testRecordAccessors() {
        Object folder = new Object();
        Object file = new Object();
        ParentReference ref = new ParentReference("ref-id", "/path");
        FileSystemInfo fsi = new FileSystemInfo("2026-03-14T12:00:00Z");

        DriveItem item = new DriveItem("test-id", "test-name", 1024L, "test-etag", folder, file, ref, fsi);

        assertEquals("test-id", item.id());
        assertEquals("test-name", item.name());
        assertEquals(1024L, item.size());
        assertEquals("test-etag", item.eTag());
        assertSame(folder, item.folder());
        assertSame(file, item.file());
        assertSame(ref, item.parentReference());
        assertSame(fsi, item.fileSystemInfo());
    }
}

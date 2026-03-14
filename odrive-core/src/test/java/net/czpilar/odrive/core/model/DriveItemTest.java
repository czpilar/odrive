package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DriveItemTest {

    @Test
    public void testIsFolderWhenFolderIsNotNull() {
        DriveItem item = new DriveItem();
        item.setFolder(new Object());

        assertTrue(item.isFolder());
    }

    @Test
    public void testIsFolderWhenFolderIsNull() {
        DriveItem item = new DriveItem();

        assertFalse(item.isFolder());
    }

    @Test
    public void testIsFileWhenFileIsNotNull() {
        DriveItem item = new DriveItem();
        item.setFile(new Object());

        assertTrue(item.isFile());
    }

    @Test
    public void testIsFileWhenFileIsNull() {
        DriveItem item = new DriveItem();

        assertFalse(item.isFile());
    }

    @Test
    public void testGetPathDisplayWithParentPathAndName() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");
        ParentReference ref = new ParentReference();
        ref.setPath("/drive/root:/Documents/Subfolder");
        item.setParentReference(ref);

        assertEquals("/Documents/Subfolder/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithRootParentPath() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");
        ParentReference ref = new ParentReference();
        ref.setPath("/drive/root:");
        item.setParentReference(ref);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithSlashAfterColon() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");
        ParentReference ref = new ParentReference();
        ref.setPath("/drive/root:/");
        item.setParentReference(ref);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithNullName() {
        DriveItem item = new DriveItem();
        ParentReference ref = new ParentReference();
        ref.setPath("/drive/root:/Documents");
        item.setParentReference(ref);

        assertEquals("/Documents", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithNullParentReference() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithNullParentPath() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");
        ParentReference ref = new ParentReference();
        item.setParentReference(ref);

        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithNullParentReferenceAndNullName() {
        DriveItem item = new DriveItem();

        assertEquals("", item.getPathDisplay());
    }

    @Test
    public void testGetPathDisplayWithPathNotContainingColon() {
        DriveItem item = new DriveItem();
        item.setName("myfile.txt");
        ParentReference ref = new ParentReference();
        ref.setPath("no-colon-path");
        item.setParentReference(ref);

        // No colon found, parentPath stays empty
        assertEquals("/myfile.txt", item.getPathDisplay());
    }

    @Test
    public void testGettersAndSetters() {
        DriveItem item = new DriveItem();
        item.setId("test-id");
        item.setName("test-name");
        item.setSize(1024L);
        item.setETag("test-etag");

        Object folder = new Object();
        item.setFolder(folder);
        Object file = new Object();
        item.setFile(file);

        ParentReference ref = new ParentReference();
        item.setParentReference(ref);

        FileSystemInfo fsi = new FileSystemInfo();
        item.setFileSystemInfo(fsi);

        assertEquals("test-id", item.getId());
        assertEquals("test-name", item.getName());
        assertEquals(1024L, item.getSize());
        assertEquals("test-etag", item.getETag());
        assertSame(folder, item.getFolder());
        assertSame(file, item.getFile());
        assertSame(ref, item.getParentReference());
        assertSame(fsi, item.getFileSystemInfo());
    }
}

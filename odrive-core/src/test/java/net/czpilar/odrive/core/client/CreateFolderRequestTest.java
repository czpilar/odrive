package net.czpilar.odrive.core.client;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateFolderRequestTest {

    @Test
    void testConvenienceConstructor() {
        CreateFolderRequest request = new CreateFolderRequest("TestFolder");

        assertEquals("TestFolder", request.name());
        assertNotNull(request.folder());
        assertTrue(request.folder().isEmpty());
    }

    @Test
    void testFullConstructor() {
        Map<String, Object> folder = Map.of("key", "value");
        CreateFolderRequest request = new CreateFolderRequest("MyFolder", folder);

        assertEquals("MyFolder", request.name());
        assertEquals(folder, request.folder());
        assertEquals(1, request.folder().size());
    }

    @Test
    void testConvenienceConstructorReturnsEmptyMap() {
        CreateFolderRequest request = new CreateFolderRequest("Folder");

        assertEquals(Map.of(), request.folder());
    }

    @Test
    void testRecordEquality() {
        CreateFolderRequest request1 = new CreateFolderRequest("Folder", Map.of());
        CreateFolderRequest request2 = new CreateFolderRequest("Folder");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testRecordToString() {
        CreateFolderRequest request = new CreateFolderRequest("TestFolder");

        String str = request.toString();
        assertTrue(str.contains("TestFolder"));
    }

    @Test
    void testRecordInequality() {
        CreateFolderRequest request1 = new CreateFolderRequest("Folder1");
        CreateFolderRequest request2 = new CreateFolderRequest("Folder2");

        assertNotEquals(request1, request2);
    }
}

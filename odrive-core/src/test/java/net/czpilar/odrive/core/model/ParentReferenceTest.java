package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParentReferenceTest {

    @Test
    public void testDefaultValues() {
        ParentReference ref = new ParentReference();

        assertNull(ref.getId());
        assertNull(ref.getPath());
    }

    @Test
    public void testSetAndGetId() {
        ParentReference ref = new ParentReference();
        ref.setId("parent-id-123");

        assertEquals("parent-id-123", ref.getId());
    }

    @Test
    public void testSetAndGetPath() {
        ParentReference ref = new ParentReference();
        ref.setPath("/drive/root:/Documents");

        assertEquals("/drive/root:/Documents", ref.getPath());
    }

    @Test
    public void testSetAllFields() {
        ParentReference ref = new ParentReference();
        ref.setId("test-id");
        ref.setPath("/drive/root:/folder");

        assertEquals("test-id", ref.getId());
        assertEquals("/drive/root:/folder", ref.getPath());
    }
}

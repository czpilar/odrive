package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParentReferenceTest {

    @Test
    void testRecordAccessors() {
        ParentReference ref = new ParentReference("parent-id-123", "/drive/root:/Documents");

        assertEquals("parent-id-123", ref.id());
        assertEquals("/drive/root:/Documents", ref.path());
    }

    @Test
    void testNullValues() {
        ParentReference ref = new ParentReference(null, null);

        assertNull(ref.id());
        assertNull(ref.path());
    }

    @Test
    void testEquality() {
        ParentReference ref1 = new ParentReference("id", "/path");
        ParentReference ref2 = new ParentReference("id", "/path");

        assertEquals(ref1, ref2);
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    void testInequality() {
        ParentReference ref1 = new ParentReference("id1", "/path1");
        ParentReference ref2 = new ParentReference("id2", "/path2");

        assertNotEquals(ref1, ref2);
    }
}

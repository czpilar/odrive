package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemInfoTest {

    @Test
    void testRecordAccessor() {
        FileSystemInfo fsi = new FileSystemInfo("2026-03-14T12:00:00Z");

        assertEquals("2026-03-14T12:00:00Z", fsi.lastModifiedDateTime());
    }

    @Test
    void testNullValue() {
        FileSystemInfo fsi = new FileSystemInfo(null);

        assertNull(fsi.lastModifiedDateTime());
    }

    @Test
    void testEquality() {
        FileSystemInfo fsi1 = new FileSystemInfo("2026-03-14T12:00:00Z");
        FileSystemInfo fsi2 = new FileSystemInfo("2026-03-14T12:00:00Z");

        assertEquals(fsi1, fsi2);
        assertEquals(fsi1.hashCode(), fsi2.hashCode());
    }

    @Test
    void testInequality() {
        FileSystemInfo fsi1 = new FileSystemInfo("2026-03-14T12:00:00Z");
        FileSystemInfo fsi2 = new FileSystemInfo("2026-03-15T12:00:00Z");

        assertNotEquals(fsi1, fsi2);
    }
}

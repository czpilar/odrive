package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemInfoTest {

    @Test
    public void testDefaultValues() {
        FileSystemInfo fsi = new FileSystemInfo();

        assertNull(fsi.getLastModifiedDateTime());
    }

    @Test
    public void testSetAndGetLastModifiedDateTime() {
        FileSystemInfo fsi = new FileSystemInfo();
        fsi.setLastModifiedDateTime("2026-03-14T12:00:00Z");

        assertEquals("2026-03-14T12:00:00Z", fsi.getLastModifiedDateTime());
    }

    @Test
    public void testSetLastModifiedDateTimeToNull() {
        FileSystemInfo fsi = new FileSystemInfo();
        fsi.setLastModifiedDateTime("2026-03-14T12:00:00Z");
        fsi.setLastModifiedDateTime(null);

        assertNull(fsi.getLastModifiedDateTime());
    }
}

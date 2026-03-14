package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UploadSessionTest {

    @Test
    public void testRecordAccessors() {
        UploadSession session = new UploadSession("https://upload.example.com/session123", "2026-03-15T12:00:00Z");

        assertEquals("https://upload.example.com/session123", session.uploadUrl());
        assertEquals("2026-03-15T12:00:00Z", session.expirationDateTime());
    }

    @Test
    public void testNullValues() {
        UploadSession session = new UploadSession(null, null);

        assertNull(session.uploadUrl());
        assertNull(session.expirationDateTime());
    }

    @Test
    public void testEquality() {
        UploadSession session1 = new UploadSession("url", "expiry");
        UploadSession session2 = new UploadSession("url", "expiry");

        assertEquals(session1, session2);
        assertEquals(session1.hashCode(), session2.hashCode());
    }

    @Test
    public void testInequality() {
        UploadSession session1 = new UploadSession("url1", "expiry1");
        UploadSession session2 = new UploadSession("url2", "expiry2");

        assertNotEquals(session1, session2);
    }
}

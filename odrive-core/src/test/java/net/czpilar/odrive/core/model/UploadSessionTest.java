package net.czpilar.odrive.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UploadSessionTest {

    @Test
    public void testDefaultValues() {
        UploadSession session = new UploadSession();

        assertNull(session.getUploadUrl());
        assertNull(session.getExpirationDateTime());
    }

    @Test
    public void testSetAndGetUploadUrl() {
        UploadSession session = new UploadSession();
        session.setUploadUrl("https://upload.example.com/session123");

        assertEquals("https://upload.example.com/session123", session.getUploadUrl());
    }

    @Test
    public void testSetAndGetExpirationDateTime() {
        UploadSession session = new UploadSession();
        session.setExpirationDateTime("2026-03-15T12:00:00Z");

        assertEquals("2026-03-15T12:00:00Z", session.getExpirationDateTime());
    }

    @Test
    public void testSetAllFields() {
        UploadSession session = new UploadSession();
        session.setUploadUrl("https://upload.example.com/session");
        session.setExpirationDateTime("2026-03-15T12:00:00Z");

        assertEquals("https://upload.example.com/session", session.getUploadUrl());
        assertEquals("2026-03-15T12:00:00Z", session.getExpirationDateTime());
    }
}

package net.czpilar.odrive.core.model;

/**
 * Model representing an upload session for large file uploads.
 *
 * @param uploadUrl          upload URL for uploading chunks
 * @param expirationDateTime expiration date time of the upload session
 * @author David Pilar (david@czpilar.net)
 */
public record UploadSession(String uploadUrl, String expirationDateTime) {
}

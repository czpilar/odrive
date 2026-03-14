package net.czpilar.odrive.core.model;

/**
 * Model representing an upload session for large file uploads.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class UploadSession {

    private String uploadUrl;
    private String expirationDateTime;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
}

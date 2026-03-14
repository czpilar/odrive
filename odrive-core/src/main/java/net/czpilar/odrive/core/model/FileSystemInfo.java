package net.czpilar.odrive.core.model;

/**
 * Model representing file system info of a drive item.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class FileSystemInfo {

    private String lastModifiedDateTime;

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(String lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }
}

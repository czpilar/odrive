package net.czpilar.odrive.core.model;

/**
 * Model representing file system info of a drive item.
 *
 * @param lastModifiedDateTime last modified date time in ISO format
 * @author David Pilar (david@czpilar.net)
 */
public record FileSystemInfo(String lastModifiedDateTime) {
}

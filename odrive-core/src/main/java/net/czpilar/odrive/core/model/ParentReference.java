package net.czpilar.odrive.core.model;

/**
 * Model representing a parent reference of a drive item.
 *
 * @param id   parent item ID
 * @param path parent path
 * @author David Pilar (david@czpilar.net)
 */
public record ParentReference(String id, String path) {
}

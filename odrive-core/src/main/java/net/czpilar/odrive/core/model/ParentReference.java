package net.czpilar.odrive.core.model;

/**
 * Model representing a parent reference of a drive item.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class ParentReference {

    private String id;
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

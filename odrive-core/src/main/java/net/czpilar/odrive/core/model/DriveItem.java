package net.czpilar.odrive.core.model;

/**
 * Model representing a OneDrive drive item (file or folder).
 *
 * @param id              item ID
 * @param name            item name
 * @param size            item size in bytes
 * @param eTag            item eTag
 * @param folder          folder facet (non-null if item is a folder)
 * @param file            file facet (non-null if item is a file)
 * @param parentReference parent reference
 * @param fileSystemInfo  file system info
 * @author David Pilar (david@czpilar.net)
 */
public record DriveItem(String id, String name, Long size, String eTag,
                        Object folder, Object file,
                        ParentReference parentReference, FileSystemInfo fileSystemInfo) {

    public boolean isFolder() {
        return folder != null;
    }

    public boolean isFile() {
        return file != null;
    }

    /**
     * Returns display path of the item relative to drive root.
     *
     * @return display path
     */
    public String getPathDisplay() {
        String parentPath = "";
        if (parentReference != null && parentReference.path() != null) {
            String refPath = parentReference.path();
            int idx = refPath.indexOf(":");
            if (idx >= 0) {
                parentPath = refPath.substring(idx + 1);
            }
        }
        if (name == null) {
            return parentPath;
        }
        if (parentPath.isEmpty() || parentPath.equals("/")) {
            return "/" + name;
        }
        return parentPath + "/" + name;
    }
}

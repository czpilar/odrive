package net.czpilar.odrive.core.model;

/**
 * Model representing a OneDrive drive item (file or folder).
 *
 * @author David Pilar (david@czpilar.net)
 */
public class DriveItem {

    private String id;
    private String name;
    private Long size;
    private String eTag;
    private Object folder;
    private Object file;
    private ParentReference parentReference;
    private FileSystemInfo fileSystemInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public Object getFolder() {
        return folder;
    }

    public void setFolder(Object folder) {
        this.folder = folder;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public ParentReference getParentReference() {
        return parentReference;
    }

    public void setParentReference(ParentReference parentReference) {
        this.parentReference = parentReference;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

    public void setFileSystemInfo(FileSystemInfo fileSystemInfo) {
        this.fileSystemInfo = fileSystemInfo;
    }

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
        if (parentReference != null && parentReference.getPath() != null) {
            String refPath = parentReference.getPath();
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

package net.czpilar.odrive.core.service;

import net.czpilar.odrive.core.model.DriveItem;

import java.util.List;

/**
 * File service interface.
 *
 * @author David Pilar (david@czpilar.net)
 */
public interface IFileService {

    /**
     * Upload file to root directory.
     *
     * @param filename filename
     * @return uploaded file
     */
    DriveItem uploadFile(String filename);

    /**
     * Upload file to directory specified by pathname starting on root level.
     * If no directory is found, the whole directory path is created.
     *
     * @param pathToFile path to file
     * @param pathname   path name
     * @return uploaded file
     */
    DriveItem uploadFile(String pathToFile, String pathname);

    /**
     * Upload file to directory specified by given parent directory.
     * Inserts a new file if a remote file does not exist or updates a remote file if content
     * was changed or do nothing if a remote file has the same content as a local file.
     *
     * @param filename  filename
     * @param parentDir parent directory
     * @return uploaded file
     */
    DriveItem uploadFile(String filename, DriveItem parentDir);

    /**
     * Upload files to the root directory.
     *
     * @param filenames filenames
     * @return uploaded files
     */
    List<DriveItem> uploadFiles(List<String> filenames);

    /**
     * Upload files to directory specified by pathname starting on root level.
     * If no directory is found, the whole directory path is created.
     *
     * @param filenames filenames
     * @param pathname  path name
     * @return uploaded files
     */
    List<DriveItem> uploadFiles(List<String> filenames, String pathname);

    /**
     * Upload files to a directory.
     *
     * @param filenames filenames
     * @param parentDir parent directory
     * @return uploaded files
     */
    List<DriveItem> uploadFiles(List<String> filenames, DriveItem parentDir);
}

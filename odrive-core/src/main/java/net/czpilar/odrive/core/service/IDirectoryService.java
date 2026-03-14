package net.czpilar.odrive.core.service;

import net.czpilar.odrive.core.model.DriveItem;

/**
 * Directory service interface.
 *
 * @author David Pilar (david@czpilar.net)
 */
public interface IDirectoryService {

    /**
     * Finds directory with given pathname where finding starts with the root parent.
     * Pathname supports directory separators "/" or "\".
     *
     * @param pathname path name
     * @return found directory or null if the directory is not found
     */
    DriveItem findDirectory(String pathname);

    /**
     * Finds directory with given pathname where finding starts with a given parent.
     * Pathname supports directory separators "/" or "\".
     *
     * @param pathname  path name
     * @param parentDir parent directory
     * @return found directory or null if the directory is not found
     */
    DriveItem findDirectory(String pathname, DriveItem parentDir);

    /**
     * Finds directory with given pathname where finding starts with the root parent.
     * If a directory does not exist, create one with a given pathname.
     * Also creates all non-existing directories on pathname.
     * Pathname supports directory separators "/" or "\".
     *
     * @param pathname path name
     * @return found or created a directory
     */
    DriveItem findOrCreateDirectory(String pathname);

    /**
     * Finds directory with the given pathname where finding starts with the given parent directory.
     * If a directory does not exist, create one with a given pathname.
     * Also creates all non-existing directories on pathname.
     * Pathname supports directory separators "/" or "\".
     *
     * @param pathname  path name
     * @param parentDir parent directory
     * @return found or created a directory
     */
    DriveItem findOrCreateDirectory(String pathname, DriveItem parentDir);
}

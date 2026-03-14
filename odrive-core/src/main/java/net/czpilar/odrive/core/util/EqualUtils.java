package net.czpilar.odrive.core.util;

import net.czpilar.odrive.core.model.DriveItem;

import java.io.File;
import java.nio.file.Path;
import java.time.OffsetDateTime;

/**
 * Equal utility class for comparing remote and local files.
 *
 * @author David Pilar (david@czpilar.net)
 */
public class EqualUtils {

    /**
     * Returns true if sizes are equal and remote last modified time is greater or equal to local file, otherwise returns false.
     *
     * @param remoteFile remote drive item (file)
     * @param pathToFile path to local file
     * @return true if equals
     */
    public static boolean equals(DriveItem remoteFile, Path pathToFile) {
        boolean result = false;
        if (remoteFile != null && pathToFile != null) {
            File localFile = pathToFile.toFile();
            if (localFile.exists()) {
                result = remoteFile.getSize() != null
                        && remoteFile.getSize() == localFile.length()
                        && getRemoteModifiedSeconds(remoteFile) >= toSeconds(localFile.lastModified());
            }
        }
        return result;
    }

    private static long getRemoteModifiedSeconds(DriveItem remoteFile) {
        if (remoteFile.getFileSystemInfo() != null
                && remoteFile.getFileSystemInfo().getLastModifiedDateTime() != null) {
            try {
                OffsetDateTime dateTime = OffsetDateTime.parse(remoteFile.getFileSystemInfo().getLastModifiedDateTime());
                return dateTime.toEpochSecond();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * This method strips milliseconds and returns seconds.
     *
     * @param milliseconds milliseconds
     * @return seconds
     */
    private static long toSeconds(long milliseconds) {
        return milliseconds / 1000;
    }

    /**
     * Returns true if sizes are not equal or remote last modified time is lower than local file, otherwise returns false.
     *
     * @param remoteFile remote drive item (file)
     * @param pathToFile path to local file
     * @return true if not equals
     */
    public static boolean notEquals(DriveItem remoteFile, Path pathToFile) {
        return !equals(remoteFile, pathToFile);
    }

}

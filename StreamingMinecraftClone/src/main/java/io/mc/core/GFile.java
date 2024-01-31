package io.mc.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;

/**
 * An abstraction of Gabes File namespace with associated functions
 */
public class GFile {

    private static final Logger logger = Logger.getLogger(GFile.class.getName());

    public static boolean removeDir(@NonNull String directoryName) {
        try {
            return Files.deleteIfExists(Paths.get(directoryName));
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e.getCause());
            return false;
        }
        
    }

    public static boolean isDir(@NonNull String directoryName) {
        File file = new File(directoryName);

        return file.isDirectory();

    }

    public static boolean isFile(@NonNull String directoryName) {
        File file = new File(directoryName);
        return file.isFile();
    }

    public static boolean moveFile(@NonNull String from, String to) {
        Path pathFrom = Paths.get(from);

        if (!pathFrom.toFile().exists()) {
            return false;
        }

        Path pathTo = Paths.get(to);

        try {
            Files.move(pathFrom, pathTo);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getCause());
            return false;
        }
        return true;
    }

    public static boolean createDirIfNotExists(@NonNull String directoryName) {
        var dir = Paths.get(directoryName);
        return dir.toFile().mkdir();
    }

    public static String getSpecialAppFolder() {

        return "";
    }

    public static FileTime getFileTimes(String fileOrDirName) {
        File file = new File(fileOrDirName);

        FileTime fileTime = new FileTime();
        fileTime.lastWrite = file.lastModified();
        return fileTime;
    }

}

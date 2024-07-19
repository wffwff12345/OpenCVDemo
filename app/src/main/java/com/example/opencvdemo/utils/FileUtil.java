package com.example.opencvdemo.utils;

import java.io.File;
import java.util.List;

public class FileUtil {
    public static boolean deleteFiles(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).exists()) {
                boolean delete = files.get(i).delete();
                if (!delete) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}

package com.main.java.task.Utils.FileUtils;

import java.io.*;

public class FileUtils {

    private static String NEW_LINE = System.getProperty("line.separator");

    public static String read(String absolutePath) throws Exception {
        return read(new File(absolutePath));
    }

    public static String read(File fullFile) throws Exception {
        if (fullFile != null && fullFile.exists()) {

            BufferedReader br = null;
            FileReader fileRdr = null;
            StringBuffer res = new StringBuffer();
            try {
                fileRdr = new FileReader(fullFile);
                br = new BufferedReader(fileRdr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    res.append(line);
                    res.append(NEW_LINE);
                }
            } finally {
                close(br);
                close(fileRdr);
            }
            return res.toString();
        } else {
            throw new Exception("Either file is null or not found. File: [" + (fullFile != null ? fullFile.getAbsolutePath() : "") + "]");
        }
    }

    public static void save(String absolutePath, String dataToSave) throws Exception {
        save(new File(absolutePath), dataToSave);
    }

    public static void save(File file, String dataToSave) throws Exception {
        FileOutputStream fileOutStream = null;
        PrintWriter printer = null;
        try {
            fileOutStream = new FileOutputStream(file, false);
            printer = new PrintWriter(fileOutStream);
            printer.println(dataToSave);
        } finally {
            close(printer);
            close(fileOutStream);
        }
    }

    private static void close(PrintWriter curObj) {
        try {
            if (curObj != null) {
                curObj.close();
            }
        } catch (Exception t) {

        }
    }

    private static void close(FileOutputStream curObj) {
        try {
            if (curObj != null) {
                curObj.close();
            }
        } catch (Exception t) {

        }
    }

    private static void close(FileReader curObj) {
        try {
            if (curObj != null) {
                curObj.close();
            }
        } catch (Exception t) {

        }
    }

    private static void close(InputStream curObj) {
        try {
            if (curObj != null) {
                curObj.close();
            }
        } catch (Exception t) {

        }
    }

    private static void close(BufferedReader curObj) {
        try {
            if (curObj != null) {
                curObj.close();
            }
        } catch (Exception t) {

        }
    }
}

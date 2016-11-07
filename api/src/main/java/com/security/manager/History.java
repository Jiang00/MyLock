package com.security.manager;

import com.security.mymodule.FileType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by song on 15/8/5.
 */
public class History {
    public static final File FILE = new File(AppsCore.ROOT + "_history_");
    public static final char HEAD_DONE = 1;
    public static final char HEAD_TD = 2;
    public static final int LINE_END = 10;

    public static boolean isHistoryValid() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE)));
            if (HEAD_DONE == reader.read()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean dropHistory() {
        try {
            return FILE.delete() && FILE.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static RandomAccessFile randomAccessFile;
    public static boolean begin() {
        try {
            randomAccessFile = new RandomAccessFile(FILE, "rw");
            randomAccessFile.seek(0);
            randomAccessFile.write(HEAD_TD);
            randomAccessFile.write(LINE_END);
            randomAccessFile.seek(randomAccessFile.length());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void end() {
        try {
            randomAccessFile.seek(0);
            randomAccessFile.write(HEAD_DONE);
            randomAccessFile.close();
            randomAccessFile = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addHistory(int fileType, String filePath) {
        try {
            RandomAccessFile randomAccessFile1 = randomAccessFile;
            randomAccessFile1.write(fileType);
            randomAccessFile1.writeUTF(filePath);
            randomAccessFile1.write(LINE_END);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addHistories(int fileType, ArrayList<String> files) {
        RandomAccessFile randomAccessFile1 = randomAccessFile;
        for (String file : files) {
            try {
                randomAccessFile1.write(fileType);
                randomAccessFile1.writeUTF(file);
                randomAccessFile1.write(LINE_END);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addHistories(int fileType, String... files) {
        RandomAccessFile randomAccessFile1 = randomAccessFile;
        for (String file : files) {
            try {
                randomAccessFile1.write(fileType);
                randomAccessFile1.writeUTF(file);
                randomAccessFile1.write(LINE_END);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static interface WalkHistory {
        void onHistory(int fileType, String file);
    }

    public static boolean iterateHistory(WalkHistory walker) {
        try {
            RandomAccessFile reader = new RandomAccessFile(FILE, "r");
            // skip the head
            reader.skipBytes(2);

            while (true) {
                int fileType = reader.read();
                if (fileType == -1) {
                    break;
                }
                if (fileType == FileType.TYPE_PIC_NATIVE) {
                    fileType = FileType.TYPE_PIC;
                }
                String file = reader.readUTF();
                reader.skipBytes(1);// skip the line end
                walker.onHistory(fileType, file);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

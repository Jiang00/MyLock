package com.security.manager.meta;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.*;

/**
 * Created by song on 15/7/9.
 */
public class MCommonFile {
    public static final File root = Environment.getExternalStorageDirectory();

    static final FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    };

    public static final Collator chi = Collator.getInstance(Locale.CHINESE);
    private static final Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File f, File t) {
            return chi.compare(f.getName(), t.getName());
        }
    };

    public static List<File> getFiles(File root) {
        File[] files = root.listFiles(fileFilter);
        List<File> orderedFiles = new ArrayList<>();
        List<File> tmp = new ArrayList<>();

        if (files == null) return orderedFiles;
        Arrays.sort(files, comparator);

        for(int i=0; i<files.length; ++i) {
            if (files[i].isDirectory()){
                orderedFiles.add(files[i]);
            } else {
                tmp.add(files[i]);
            }
            files[i] = null;
        }

        orderedFiles.addAll(tmp);

        return orderedFiles;
    }
}

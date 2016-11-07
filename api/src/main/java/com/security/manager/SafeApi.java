package com.security.manager;

import android.content.Context;
import android.util.SparseArray;

import com.security.manager.lib.async.LoadingTask;
import com.security.mymodule.FileType;
import com.security.mymodule.FolderEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by song on 15/8/5.
 */
public class SafeApi {
    static SafeApi api;

    Context context;

    public static final Object lock = new Object();

    SparseArray<HashMap<String, FolderEntry>> folderEntriesMap = new SparseArray<>();
    SparseArray<ArrayList<FolderEntry>> folderEntriesWithType = new SparseArray<>();

    private SafeApi(Context context) {
        this.context = context;
        for (int i = FileType.TYPE_PIC; i < FileType.TYPE_LAST; ++i) {
            folderEntriesWithType.put(i, new ArrayList<FolderEntry>());
            folderEntriesMap.put(i, new HashMap<String, FolderEntry>());
        }
    }

    public static synchronized SafeApi instance(Context context) {
        if (api == null) {
            api = new SafeApi(context.getApplicationContext());
        }
        return api;
    }

    public FolderEntry getFolder(int fileType, String bucket) {
        FolderEntry entry = folderEntriesMap.get(fileType).get(bucket);
        if (entry == null) {
            entry = new FolderEntry();
            entry.bucketId = bucket;
            entry.bucketName = bucket.substring(bucket.lastIndexOf('/') + 1);
            entry.fileType = fileType;
            folderEntriesMap.get(fileType).put(bucket, entry);
            folderEntriesWithType.get(fileType).add(entry);
        }
        return entry;
    }

    public ArrayList<FolderEntry> getFolders(int fileType) {
        return folderEntriesWithType.get(fileType);
    }

    public FolderEntry getFolder(int fileType, int which) {
        ArrayList<FolderEntry> folderEntries = folderEntriesWithType.get(fileType);
        return folderEntries.size() <= which ? null : folderEntries.get(which);
    }

    public void waiting(Runnable callback) {
        task.waiting(callback);
    }

    final LoadingTask task = new LoadingTask() {
        @Override
        protected void doInBackground() {
            synchronized (lock) {
                History.WalkHistory walker = new History.WalkHistory() {
                    @Override
                    public void onHistory(int fileType, String file) {
                        HashMap<String, FolderEntry> map = folderEntriesMap.get(fileType);
                        String bucket = file.substring(0, file.lastIndexOf('/'));
                        FolderEntry entry = map.get(bucket);
                        if (entry == null) {
                            entry = new FolderEntry();
                            entry.bucketId = bucket;
                            entry.bucketName = bucket.substring(bucket.lastIndexOf('/') + 1);
                            entry.fileType = fileType;
                            map.put(bucket, entry);
                        }
                        entry.addFile(file);
                    }
                };

                if (History.isHistoryValid() && History.iterateHistory(walker)) {

                } else {
                    String f = AppsCore.p(AppsCore.INFO_PATH);
                    File root = new File(f);
                    File[] files = root.listFiles();
                    if (files != null) {
                        History.dropHistory();
                        History.begin();
                        for (File ff : files) {
                            try {
                                String content = AppsCore.f(ff.getAbsolutePath());
                                int type = content.charAt(0);
                                if (type == FileType.TYPE_PIC_NATIVE) type = FileType.TYPE_PIC;
                                String path = content.substring(1);
                                History.addHistory(type, path);
                                walker.onHistory(type, path);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        History.end();
                    }
                }
                for (int i = FileType.TYPE_PIC; i < FileType.TYPE_LAST; ++i) {
                    folderEntriesWithType.get(i).addAll(folderEntriesMap.get(i).values());
                }
            }
        }
    };

    public void notifyDataSetChanged() {
        History.dropHistory();
        History.begin();
        for (int i = FileType.TYPE_PIC; i < FileType.TYPE_LAST; ++i) {
            ArrayList<FolderEntry> folders = folderEntriesWithType.get(i);
            HashMap<String, FolderEntry> maps = folderEntriesMap.get(i);
            for (int j = folders.size() - 1; j >= 0; --j) {
                FolderEntry entry = folders.get(j);
                if (entry.count() == 0) {
                    folders.remove(j);
                    maps.remove(entry.bucketId);
                    continue;
                }
                History.addHistories(i, entry.getFiles());
            }
        }
        History.end();
    }
}

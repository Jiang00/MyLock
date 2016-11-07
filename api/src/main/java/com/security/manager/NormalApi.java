package com.security.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.security.manager.lib.async.LoadingTask;
import com.security.manager.lib.io.BaseColumns;
import com.privacy.model.FileType;
import com.privacy.model.FolderEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 15/8/7.
 */
public class NormalApi {
    static NormalApi api;

    public static synchronized NormalApi instance(Context context) {
        if (api == null) {
            api = new NormalApi(context.getApplicationContext());
        }
        return api;
    }

    Context context;
    int fileType;

    public NormalApi(Context context) {
        this.context = context;
    }

    public void waiting(int fileType, Runnable callback) {
        if (this.fileType == fileType) {
            loadingTask.waiting(callback);
        } else {
            this.fileType = fileType;
            loadingTask.restart(callback);
        }
    }

    ArrayList<FolderEntry> folderEntries = new ArrayList<>();

    public ArrayList<FolderEntry> getFolders() {
        return folderEntries;
    }

    public FolderEntry getFolder(int which) {
        return folderEntries.get(which);
    }

    final LoadingTask loadingTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            String[] projection = {
                    BaseColumns._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_MODIFIED
            };
            folderEntries.clear();
            Cursor cursor = context.getContentResolver().query(getUri(fileType), projection, null, null, null);
            try {
                cursor.moveToFirst();
                Map<String, FolderEntry> entryMap = new HashMap<>();
                if (isCanceled()) return;
                do {
                    String file = cursor.getString(2);
                    String bucketId = file.substring(0, file.lastIndexOf('/'));
                    FolderEntry entry = entryMap.get(bucketId);
                    if (entry == null) {
                        entry = new FolderEntry();
                        entry.bucketId = bucketId;
                        entry.bucketName = cursor.getString(1);
                        entry.fileType = fileType;
                        entryMap.put(bucketId, entry);
                    }
                    entry.addFile(file);
                    entry.addFileId(cursor.getLong(0));

                    if (isCanceled()) return;
                } while (cursor.moveToNext());
                folderEntries.addAll(entryMap.values());
            } finally {
                cursor.close();
            }
        }
    };

    public static Uri getUri(int fileType) {
        switch (fileType) {
            case FileType.TYPE_PIC:
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            case FileType.TYPE_VIDEO:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            case FileType.TYPE_AUDIO:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            default:
                return null;
        }
    }

    public void notifyDataSetChanged() {
        this.fileType = FileType.TYPE_LAST;
    }
}

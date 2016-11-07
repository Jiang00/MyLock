package com.security.mymodule;

import java.util.ArrayList;

public class FolderEntry {
    public String bucketId;
    public String bucketName;
    public int fileType;
    ArrayList<String> files = new ArrayList<>();
    ArrayList<Long> fileIds = new ArrayList<>();

    public void addFileId(long id) {
        fileIds.add(id);
    }

    public void removeFileId(long id) {
        fileIds.remove(id);
    }

    public void addFile(String file) {
        files.add(file);
    }

    public void removeFile(String file) {
        files.remove(file);
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public int count() {
        return files.size();
    }

    public String getFile(int which) {
        return files.size() > which ? files.get(which) : null;
    }

    public ArrayList<Long> getFileIds() {
        return fileIds;
    }

    public long getFileId(int which) {
        return fileIds.size() > which ? fileIds.get(which) : 0L;
    }
}

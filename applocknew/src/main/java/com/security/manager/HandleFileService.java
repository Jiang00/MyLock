package com.security.manager;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.privacy.lock.R;
import com.security.manager.meta.FileData;
import com.security.manager.meta.MCommonFile;
import com.security.manager.lib.Utils;
import com.security.manager.lib.io.BaseColumns;
import com.security.manager.lib.io.ImageMaster;
import com.security.mymodule.FileType;
import com.security.mymodule.FolderEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SongHualin on 7/2/2015.
 */
public class HandleFileService extends IntentService {
    public static final int MSG_HAND_SHAKE = 1;
    public static final int MSG_CANCEL = 2;
    public static final int MSG_CANCELED = 3;
    public static final int MSG_FINISHED = 4;
    public static final int MSG_UPDATE_PROGRESS = 5;
    public static final int MSG_REFRESHING = 6;

    Messenger client;

    public HandleFileService() {
        super("worker");
    }

    public static void startService(int fileType, int total, boolean[] selects, boolean normal, boolean folder, ArrayList<Integer> entryIdx) {
        App.getContext().startService(
                new Intent(App.getContext(), HandleFileService.class)
                        .putExtra("fileType", fileType)
                        .putExtra("total", total)
                        .putExtra("selects", selects)
                        .putExtra("normal", normal)
                        .putExtra("folder", folder)
                        .putExtra("entries", entryIdx)
        );
    }

    public static void startService(int total, boolean[] selects, File root) {
        App.getContext().startService(new Intent(App.getContext(), HandleFileService.class)
                .putExtra("total", total)
                .putExtra("selects", selects)
                .putExtra("file", root));
    }

    boolean cancel = false;
    boolean handshake = false;

    static class HandleHandler extends Handler {
        WeakReference<HandleFileService> service;

        public HandleHandler(Looper looper, HandleFileService service) {
            super(looper);
            this.service = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            HandleFileService s = service.get();
            if (s == null) return;
            switch (msg.what) {
                case MSG_HAND_SHAKE:
                    s.client = msg.replyTo;
                    s.handshake = true;
                    break;

                case MSG_CANCEL:
                    s.cancel = true;
                    break;
            }
        }
    }

    HandleHandler handleHandler;
    Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();
        handleHandler = new HandleHandler(getMainLooper(), this);
        messenger = new Messenger(handleHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public void stopMe() {
        int count = 0;
        while (!handshake) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++count > 10) break;
        }
        manager.cancel(1);
        stopSelf();
    }

    boolean scanning = false;
    public void waiting() {
        while (scanning) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    int fileType;
    ScannerCallback scannerCallback;

    @Override
    protected void onHandleIntent(Intent intent) {
        selects = intent.getBooleanArrayExtra("selects");
        total = intent.getIntExtra("total", 0);
        ArrayList<Integer> entries = intent.getIntegerArrayListExtra("entries");
        File root = (File) intent.getSerializableExtra("file");
        fileType = intent.getIntExtra("fileType", FileType.TYPE_PIC);

        if (root != null) {
            normal = true;
            handleNormalFiles(root, selects);
        } else if (intent.getBooleanExtra("normal", false)) {
            normal = true;
            if (intent.getBooleanExtra("folder", false)) {
                handleNormalFolder(entries);
            } else {
                currentFolderIdx = 1;
                FolderEntry entry = NormalApi.instance(this).getFolder(entries.get(0));
                handleNormalFile(entry, true);
            }
        } else {
            normal = false;
            scanning = true;
            scannerCallback = new ScannerCallback(new ScannerCallback.ScannerListener() {
                @Override
                public void updateProgress() {
                    try {
                        if (client != null) {
                            Message msg = Message.obtain();
                            msg.what = MSG_REFRESHING;
                            msg.arg1 = scannerCallback.currentCount;
                            msg.arg2 = scannerCallback.totalCount;
                            client.send(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onComplete() {
                    scanning = false;
                    onFinished();
                    stopMe();
                }
            });
            if (intent.getBooleanExtra("folder", false)) {
                handleSafeFolder(entries);
            } else {
                currentFolderIdx = 1;
                FolderEntry entry = SafeApi.instance(this).getFolder(fileType, entries.get(0));
                handleSafeFile(entry, true);
            }
        }
    }

    private void handleNormalFolder(ArrayList<Integer> entries) {
        ArrayList<FolderEntry> entries1 = new ArrayList<>();
        for (int entryIdx : entries) {
            FolderEntry entry = NormalApi.instance(this).getFolder(entryIdx);
            entries1.add(entry);
        }
        for (FolderEntry entry : entries1) {
            ++currentFolderIdx;
            int count = entry.getFiles().size();
            selects = new boolean[count];
            Arrays.fill(selects, true);
            total = count;
            handleNormalFile(entry, false);
            if (cancel) break;
        }
        onFinished();
        stopMe();
    }

    private void onFinished() {
        if (client != null) {
            try {
                Message msg = Message.obtain();
                if (cancel) {
                    msg.what = MSG_CANCELED;
                } else {
                    msg.what = MSG_FINISHED;
                }
                if (failFiles.size() > 0) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("fails", failFiles);
                    msg.setData(b);
                }
                client.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int currentFolderIdx = 0;

    private void handleSafeFolder(ArrayList<Integer> entries) {
        ArrayList<FolderEntry> entries1 = new ArrayList<>();
        for (int entryIdx : entries) {
            FolderEntry entry = SafeApi.instance(this).getFolder(fileType, entryIdx);
            entries1.add(entry);
        }
        for (FolderEntry entry : entries1) {
            ++currentFolderIdx;
            int count = entry.getFiles().size();
            selects = new boolean[count];
            total = count;
            Arrays.fill(selects, true);
            handleSafeFile(entry, false);
            if (cancel) break;
        }
        scannerCallback.requireStop();
        waiting();
//        onFinished();
//        stopMe();
    }

    private void handleSafeFile(FolderEntry entry, boolean stop) {
        try {
            current = 0;
            startForeGround();
            ContentResolver resolver = getContentResolver();
            ArrayList<String> fileNames = new ArrayList<>();
            ArrayList<String> files = entry.getFiles();
            new File(entry.bucketId).mkdirs();
            History.begin();
            for (int i = selects.length - 1; i >= 0; --i) {
                if (selects[i]) {
                    String fileName = files.get(i);
                    if (AppsCore.d(fileName)) {
                        files.remove(i);
                        deleteThumbnail(resolver, fileName, 0);
                        fileNames.add(fileName);
                        if (fileNames.size() == 4) {
                            scannerCallback.addCount(4);
                            MediaScannerConnection.scanFile(App.getContext(), fileNames.toArray(new String[fileNames.size()]), null, scannerCallback);
                            fileNames.clear();
                        }
                    } else {
                        Utils.LOGER(fileName + " error: " + AppsCore.e());
                        failFiles.add(fileName);
                    }
                    ++current;
                    updateNotification();
                }
                if (cancel) break;
            }
            History.end();

            SafeApi.instance(this).notifyDataSetChanged();
            NormalApi.instance(this).notifyDataSetChanged();

            if (fileNames.size() > 0) {
                scannerCallback.addCount(fileNames.size());
                MediaScannerConnection.scanFile(App.getContext(), fileNames.toArray(new String[fileNames.size()]), null, scannerCallback);
                fileNames.clear();
            }

            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stop) {
                scannerCallback.requireStop();
                waiting();
            }
        }
    }

    private void deleteThumbnail(ContentResolver resolver, String fileName, long aLong) {
        File f = new File(AppsCore.s(fileName, true));
        f.delete();
    }

    void startForeGround() {
        Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon)
                .setOngoing(true)
                .setContentTitle(getString(getTitle()))
                .setTicker(getString(getTitle()));
        manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
    }

    boolean normal;

    private int getTitle() {
        return normal ? R.string.encrypt_title : R.string.decrypt_title;
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager manager;

    void updateNotification() {
        notificationBuilder.setContentText(current + "/" + total).setProgress(total, current, false);
        manager.notify(1, notificationBuilder.build());
        try {
            if (client != null) {
                Message msg = Message.obtain();
                msg.what = MSG_UPDATE_PROGRESS;
                msg.arg1 = current;
                msg.arg2 = (currentFolderIdx << 24) | total;
                client.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        client = null;
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public int total;
    public int current;
    boolean[] selects;
    ArrayList<String> failFiles = new ArrayList<>();

    void handleNormalFile(FolderEntry entry, boolean stop) {
        try {
            current = 0;
            startForeGround();
            ContentResolver resolver = getContentResolver();
            ArrayList<Long> deleteIds = new ArrayList<>();
            ArrayList<String> files = entry.getFiles();
            FolderEntry safeEntry = SafeApi.instance(this).getFolder(fileType, entry.bucketId);
            History.begin();
            for (int i = selects.length - 1; i >= 0; --i) {
                if (selects[i]) {
                    String fileName = files.get(i);
                    long id = entry.getFileId(i);
                    saveThumbnail(resolver, entry.fileType, fileName, id);
                    if (AppsCore.e(fileName, entry.fileType)) {
                        deleteIds.add(id);
                        safeEntry.addFile(fileName);
                        files.remove(i);
                        entry.getFileIds().remove(i);
                    } else {
                        //文件不存在，则删除记录
                        int e = AppsCore.e();
                        if (e == AppsCore.ERROR_NOT_FOUND) {
                            deleteIds.add(id);
                            files.remove(i);
                            entry.getFileIds().remove(i);
                        } else if (e == AppsCore.ERROR_RENAME_FAILS) {
                            failFiles.add(fileName);
                        }
                    }
                    ++current;
                    updateNotification();
                }
                if (cancel) break;
            }
            History.end();
            SafeApi.instance(this).notifyDataSetChanged();
            if (entry.count() == 0) {
                NormalApi.instance(this).getFolders().remove(entry);
            }

            final int length = deleteIds.size();
            final int segmentLength = 20;
            int segments = (int) Math.ceil(deleteIds.size() / 20.0);
            Uri uri = FileData.getUri(entry.fileType);
            if (uri != null) {
                for (int i = 0; i < segments; ++i) {
                    int end = (i + 1) * segmentLength;
                    if (end > length) end = length;
                    end -= 1;
                    StringBuilder sb = new StringBuilder(BaseColumns.LEFT_PARENTHESIS);
                    for (int j = i * segmentLength; j < end; ++j) {
                        sb.append(deleteIds.get(j)).append(BaseColumns.DOT);
                    }
                    sb.append(deleteIds.get(end)).append(BaseColumns.RIGHT_PARENTHESIS);
                    resolver.delete(uri, BaseColumns._ID + BaseColumns.IN + sb.toString(), null);
                }
            }
            stopForeground(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stop) {
                onFinished();
                stopMe();
            }
        }
    }

    public static void saveImageToSDCard(String fileName, Bitmap bmp) {
        try {
            File f = new File(fileName);
            FileOutputStream stream = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveThumbnail(ContentResolver resolver, int fileType, String fileName, long id) {
        Bitmap bmp = ImageMaster.getImage(fileName);
        if (bmp != null && !bmp.isRecycled()) {
            saveImageToSDCard(AppsCore.s(fileName, true), bmp);
        } else {
            switch (fileType) {
                case FileData.TYPE_PIC:
                    bmp = getImageThumbnail(resolver, id);
                    break;

                case FileData.TYPE_VIDEO:
                    bmp = getVideoThumbnail(resolver, id);
                    break;
            }
            if (bmp != null) {
                saveImageToSDCard(AppsCore.s(fileName, true), bmp);
                bmp.recycle();
            } else {
                Utils.LOGE("fails save thumbnail " + fileName);
            }
        }
    }

    /**
     * <pre>
     * <b>Design</b>
     * 1，隐藏文件
     *      生成缩略图
     *      正式隐藏
     * 2，更新目录信息
     *      更新保护目录
     *          添加到保护目录
     *          更新保护目录的预览图
     *      更新当前目录
     *          从当前目录删除
     *          更新当前目录的预览图
     * 3，更新文件信息
     *      添加保护文件的信息
     *      删除普通文件的信息
     * </pre>
     */
    static boolean handleSingleNormalFile(FolderEntry currentFolder, String currentFile, long currentFileId) {
        ContentResolver resolver = App.getContext().getContentResolver();
        saveThumbnail(resolver, currentFolder.fileType, currentFile, currentFileId);
        if (AppsCore.e(currentFile, currentFolder.fileType)) {
            FolderEntry safeEntry = SafeApi.instance(App.getContext()).getFolder(currentFolder.fileType, currentFolder.bucketId);
            safeEntry.addFile(currentFile);
            History.begin();
            History.addHistory(currentFolder.fileType, currentFile);
            History.end();
            currentFolder.removeFile(currentFile);
            if (currentFolder.count() == 0) {
                NormalApi.instance(App.getContext()).getFolders().remove(currentFolder);
            }
            resolver.delete(FileData.getUri(currentFolder.fileType), BaseColumns._ID + BaseColumns.EQU + currentFileId, null);
            return true;
        } else {
            Toast.makeText(App.getContext(), R.string.hide_fail, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * <pre>
     * <b>Design</b>
     *  1，显示文件
     *      正式显示文件
     *      删除缩略图
     *  2，更新目录信息
     *      更新当前目录信息
     *      更新普通目录信息
     *  3，更新文件信息
     *      删除保护文件的信息
     *      添加普通文件信息
     * </pre>
     */
    static boolean handleSingleSafeFile(FolderEntry currentFolder, String currentFile, long currentFileId) {
        if (AppsCore.d(currentFile)) {
            new File(AppsCore.s(currentFile, true)).delete();
            MediaScannerConnection.scanFile(App.getContext(), new String[]{currentFile}, null, null);
            NormalApi.instance(App.getContext()).notifyDataSetChanged();
            currentFolder.removeFile(currentFile);
            SafeApi.instance(App.getContext()).notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    /**
     * <pre>
     * <b>Design</b>
     *  1，隐藏文件
     *  2，更新保护的目录信息
     *      更新该目录下的保护文件数量
     *  3，更新保护的文件信息
     *      添加到保护数据表中
     * </pre>
     *
     * @param currentFile
     */
    static void handleSingleNormalFile(File currentFile) {
//        if (AppsCore.e(currentFile.getAbsolutePath(), FileData.TYPE_COMMON)) {
//            FolderEntry folder = new FolderEntry();
//            File file = currentFile.getParentFile();
//            folder.fileType = FileData.TYPE_COMMON;
//            folder.bucketName = file.getName();
//            folder.bucketUrl = file.getAbsolutePath() + "/";
//            folder.bucketId = file.toString().toLowerCase().hashCode() + "";
//            folder.count = 1;
//
//            SQLiteDatabase db = singleton(App.getContext()).getWritableDatabase();
//            String tableName = FolderEntry.bucketToTableName(folder.bucketId, FileData.TYPE_COMMON);
//            FileEntry.createTable(db, tableName);
//            FileEntry.addFile(db, tableName, currentFile.getName());
//
//            MSafeFolder.addFolder(folder);
//        }
    }

    void handleNormalFiles(File root, boolean[] selects) {
        try {
            current = 0;
            startForeGround();

            handleNormalRoot(root, selects);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onFinished();
            stopMe();
        }
    }

    private void handleNormalRoot(File root, boolean[] selects) {
        List<File> files = MCommonFile.getFiles(root);
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < selects.length; ++i) {
            if (selects[i]) {
                File file = files.get(i);
                if (file.isDirectory()) {
                    ++currentFolderIdx;
                    try {
                        handleNormalRoot(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String fileName = file.getAbsolutePath();
                    if (AppsCore.e(fileName, FileData.TYPE_COMMON)) {
                        fileNames.add(fileName);
                    }
                    ++current;
                    updateNotification();
                }
            }
        }
        try {
            updateSafeFile(root, fileNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNormalRoot(File root) throws Exception {
        List<File> files = MCommonFile.getFiles(root);
        List<String> fileNames = new ArrayList<>();
        for (int i = files.size() - 1; i >= 0; --i) {
            File f = files.get(i);
            if (f.isDirectory()) {
                break;
            } else {
                String fileName = f.getAbsolutePath();
                if (AppsCore.e(fileName, FileData.TYPE_COMMON)) {
                    fileNames.add(fileName);
                }
                ++current;
                updateNotification();
            }
        }
        updateSafeFile(root, fileNames);
    }

    private void updateSafeFile(File root, List<String> fileNames) throws Exception {
        if (fileNames.size() > 0) {
            FolderEntry folder = SafeApi.instance(this).getFolder(FileType.TYPE_COMMON, root.getAbsolutePath());
            folder.getFiles().addAll(fileNames);
            SafeApi.instance(this).notifyDataSetChanged();
        }
    }

    public static Bitmap getVideoThumbnail(ContentResolver r, long id) {
        return MediaStore.Video.Thumbnails.getThumbnail(r, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
    }

    public static Bitmap getImageThumbnail(ContentResolver r, long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(r, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }
}

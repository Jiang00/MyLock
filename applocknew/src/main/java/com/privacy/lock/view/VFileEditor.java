package com.privacy.lock.view;

import android.app.ProgressDialog;
import android.content.*;
import android.os.*;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.*;
import butterknife.*;
import com.security.manager.lib.Utils;
import com.privacy.lock.App;
import com.privacy.lock.HandleFileService;
import com.privacy.lock.R;
import com.privacy.lock.controller.CFileEditor;
import com.privacy.model.FolderEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongHualin on 6/8/2015.
 */
public class VFileEditor {
    CFileEditor editor;

    public interface IFileEditorTarget {
        byte IET_fileType();

        boolean IET_isNormal();

        boolean IET_isFolder();

        boolean IET_supportDelete();

        int IET_getFolderEntry();

        List<FolderEntry> IET_getFolderEntries();

        void IET_onItemClick(int which);

        void IET_delete(int index);

        void IET_onEnterEdit();

        void IET_onExitEdit();

        void IET_onActionFinished();
    }

    IFileEditorTarget target;
    boolean editing = false;

    @InjectView(R.id.abs_list)
    AbsListView gridView;

    @InjectView(R.id.bottom_action_bar)
    View actionBar;

    @InjectView(R.id.edit_mode)
    View editButton;

    @OnClick(R.id.edit_mode)
    public void toggleEditMode() {
        if (editing) {
            exitEditMode();
        } else {
            enterEditMode();
        }
    }

    @OnItemClick(R.id.abs_list)
    public void onItemClick(int which) {
        if (editing) {
            editor.select(which);
            Utils.notifyDataSetChanged(gridView);
        } else {
            target.IET_onItemClick(which);
        }
    }

    @OnItemLongClick(R.id.abs_list)
    public boolean onItemLongClick(int which) {
        if (!editing) {
            enterEditMode();
        }
        editor.select(which);
        Utils.notifyDataSetChanged(gridView);
        return true;
    }

    @OnClick(R.id.select_all)
    public void selectAll() {
        editor.selectAll();
        Utils.notifyDataSetChanged(gridView);
    }

    protected void handleFile() {
        boolean[] selects = editor.selects();
        ArrayList<Integer> entryIdx = new ArrayList<>();
        entryIdx.add(target.IET_getFolderEntry());
        HandleFileService.startService(target.IET_fileType(), editor.getCount(), selects, target.IET_isNormal(), false, entryIdx);
    }

    protected void handleFolder() {
        int count = editor.getCount();
        boolean[] selects = editor.selects();
        boolean normal = target.IET_isNormal();
        ArrayList<Integer> folders = new ArrayList<>(count);
        for (int i = 0; i < selects.length; ++i) {
            if (selects[i]) {
                folders.add(i);
            }
        }
        HandleFileService.startService(target.IET_fileType(), count, null, normal, true, folders);
    }

    class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandleFileService.MSG_CANCELED:
                    onFinished(false);
                    if (msg.getData() != null && msg.getData().containsKey("fails")) {
                        final ArrayList<String> fails = msg.getData().getStringArrayList("fails");
                        alertFails(fails);
                    }
                    Toast.makeText(App.getContext(), R.string.cancel, Toast.LENGTH_SHORT).show();
                    break;

                case HandleFileService.MSG_FINISHED:
                    onFinished(false);
                    if (msg.getData() != null && msg.getData().containsKey("fails")) {
                        final ArrayList<String> fails = msg.getData().getStringArrayList("fails");
                        alertFails(fails);
                    }
                    break;

                case HandleFileService.MSG_REFRESHING:
                    try {
                        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
                        int max = msg.arg2;
                        dialog.setMax(max);
                        dialog.setTitle(R.string.refreshing);
                        dialog.setProgress(msg.arg1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case HandleFileService.MSG_UPDATE_PROGRESS:
                    try {
                        int max = msg.arg2 & 0x00ffffff;
                        int currentFolderIdx = (msg.arg2 >> 24);
                        dialog.setMax(max);
                        setDialogTitle(folderCount, currentFolderIdx);
                        dialog.setProgress(msg.arg1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void alertFails(ArrayList<String> fails) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.cant_move_file)
                .setView(R.layout.scrollview)
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog.show();
        ((ListView)dialog.findViewById(R.id.abs_list)).setAdapter(new ArrayAdapter<>(context, R.layout.textview, fails));
    }

    private void onFinished(boolean destroy) {
        try {
            if (connection != null) {
                context.unbindService(connection);
            }

            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            if (editing) {
                exitEditMode();
                if (!destroy) {
                    target.IET_onActionFinished();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Messenger serverMessenger;
    Messenger clientMessenger;
    ServiceConnection connection;
    ProgressDialog dialog;
    int folderCount;

    @OnClick(R.id.encrypt)
    public void onEncryptButtonClicked() {
        if (editor.getCount() <= 0) {
            Toast.makeText(context, R.string.select_no_file, Toast.LENGTH_SHORT).show();
            return;
        }

        int total;
        if (target.IET_isFolder()) {
            handleFolder();
            total = editor.getCount();
        } else {
            handleFile();
            total = 1;
        }

        folderCount = total;
        dialog = new ProgressDialog(context);
        dialog.setMax(1);
        setDialogTitle(total, 1);
        dialog.setProgress(0);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getString(R.string.cancel), (DialogInterface.OnClickListener) null);
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            boolean canceling;

            @Override
            public void onClick(View v) {
                if (canceling) return;
                Message msg = Message.obtain();
                msg.what = HandleFileService.MSG_CANCEL;
                try {
                    serverMessenger.send(msg);
                    ((Button) v).setText(R.string.canceling);
                    canceling = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    onFinished(false);
                }
            }
        });

        if (connection == null) {
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    serverMessenger = new Messenger(service);
                    Message handleShake = Message.obtain();
                    handleShake.what = HandleFileService.MSG_HAND_SHAKE;
                    handleShake.replyTo = clientMessenger;
                    try {
                        serverMessenger.send(handleShake);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFinished(false);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    connection = null;
                    serverMessenger = null;
                    onFinished(false);
                }
            };
        }

        if (!context.bindService(new Intent(context, HandleFileService.class), connection, 0)) {
            onFinished(false);
        }
    }

    protected void setDialogTitle(int total, int current) {
        dialog.setTitle((target.IET_isNormal() ? context.getString(R.string.encrypt_title) : context.getString(R.string.decrypt_title)) + " (" + current + "/" + total + ")");
    }

    @Optional
    @OnClick(R.id.del)
    public void onDelete() {
//        if (target.IET_supportDelete()) {
//            //todo delete
//            boolean[] selects = editor.selects();
//            List<String> selectedList = new ArrayList<>();
//            if (target.IET_isBucket()) {
//                int totalFiles = 0;
//                for (int i = selects.length - 1; i >= 0; --i) {
//                    if (selects[i]) {
//                        totalFiles += target.IET_count(i);
//                        selectedList.add(target.IET_url(i));
//                        target.IET_delete(i);
//                    }
//                }
//                if (target.IET_isEncrypt()) {
//                    encryptor.encryptBuckets(context, totalFiles, selectedList, target.IET_fileType(), this);
//                } else {
//                    //todo decrypt
//                    //encryptor.encryptBuckets(context, totalFiles, selectedList, target.IET_fileType());
//                }
//            } else {
//                for (int i = selects.length - 1; i >= 0; --i) {
//                    if (selects[i]) {
//                        selectedList.add(target.IET_url(i));
//                        target.IET_delete(i);
//                    }
//                }
//                if (target.IET_isEncrypt()) {
//                    //todo encrypt urls
//                    target.IET_bucketId();
//                    encryptor.encrypt("", target.IET_fileType());
//                } else {
//                    //todo decrypt
//                }
//            }
//            editor.reset(selects.length - selectedList.size());
//        }
    }

    Context context;

    public VFileEditor(View root, IFileEditorTarget target) {
        ButterKnife.inject(this, root);
        this.target = target;
        destroyed = false;
        context = root.getContext();
        clientMessenger = new Messenger(new ClientHandler());
        editor = new CFileEditor();
        editButton.setVisibility(View.INVISIBLE);
        if (!target.IET_supportDelete()) {
            root.findViewById(R.id.del).setVisibility(View.GONE);
        }
    }

    public final boolean isSelected(int which) {
        return editing && editor.isSelected(which);
    }

    private void enterEditMode() {
        target.IET_onEnterEdit();
        actionBar.setVisibility(View.VISIBLE);
        editButton.setSelected(true);
        editing = true;
        editor.reset();
    }

    public void initComplete() {
        editButton.setVisibility(View.VISIBLE);
    }

    public void exitEditMode() {
        target.IET_onExitEdit();
        if (actionBar != null) {
            actionBar.setVisibility(View.GONE);
            editButton.setSelected(false);
        }
        editing = false;
    }

    public boolean onBackPressed() {
        if (editing) {
            exitEditMode();
            return true;
        } else
            return false;
    }

    public void reset(int size) {
        if (editor != null) {
            editor.reset(size);
        }
    }

    public void destroy() {
        synchronized (this) {
            onFinished(true);
            if (editor != null) {
                editor.destroy();
                editor = null;
            }
            context = null;
            actionBar = null;
            editButton = null;
            target = null;
            gridView = null;
            destroyed = true;
        }
    }

    boolean destroyed;

    public boolean isDestroyed() {
        synchronized (this) {
            return destroyed;
        }
    }
}

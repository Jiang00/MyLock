package com.privacy.lock.view;

import android.view.View;
import com.privacy.lock.HandleFileService;
import com.privacy.lock.R;

import java.io.File;
import java.util.List;

/**
 * Created by song on 15/7/9.
 */
public class VCommonFileEditor extends VFileEditor {
    public interface ICommonFileEditorTarget extends IFileEditorTarget {
        File IET_getRoot();
        List<File> IET_getFiles();
    }

    public VCommonFileEditor(View root, ICommonFileEditorTarget target) {
        super(root, target);
    }

    @Override
    protected void handleFolder() {
        File root = ((ICommonFileEditorTarget)target).IET_getRoot();
        boolean[] selects = editor.selects();
        HandleFileService.startService(editor.getCount(), selects, root);
    }

    @Override
    protected void handleFile() {

    }

    @Override
    protected void setDialogTitle(int total, int current) {
        boolean[] selects = editor.selects();
        List<File> files = ((ICommonFileEditorTarget)target).IET_getFiles();
        total = 0;
        for(int i=0; i<selects.length; ++i) {
            if (selects[i]) {
                //这里可以这样判断，是因为取到的文件列表是有序的，先目录，后文件
                if (!files.get(i).isDirectory()) {
                    ++total;
                    break;
                }
                ++total;
            }
        }
        dialog.setTitle((target.IET_isNormal() ? context.getString(R.string.encrypt_title) : context.getString(R.string.decrypt_title)) + " (" + current + "/" + total + ")");
    }
}

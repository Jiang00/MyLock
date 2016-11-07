package com.privacy.api;

import android.test.ApplicationTestCase;

import com.security.manager.History;
import com.security.manager.SafeApi;
import com.security.manager.lib.BaseApp;
import com.security.manager.lib.Utils;
import com.security.mymodule.FileType;
import com.security.mymodule.FolderEntry;

import junit.framework.Assert;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<BaseApp> {
    public ApplicationTest() {
        super(BaseApp.class);
    }

    public void testHistory() throws InterruptedException {
        try {
            testApplicationTestCaseSetUpProperly();
            getApplication().onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("begin fails", true, History.begin());
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/abc/abc.wav");
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/ac/def.wav");
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/abc/ghi.wav");
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/bc/jkl.wav");
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/abc/mno.wav");
        History.addHistory(FileType.TYPE_AUDIO, "/sdcard/abc/pqr.wav");
        History.addHistories(FileType.TYPE_DOC, "/sdcard/def/def.abc", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab", "/sdcard/hij/def.ab");
        History.end();

        SafeApi.instance(getContext()).waiting(new Runnable() {
            @Override
            public void run() {
                ArrayList<FolderEntry> folderEntries = SafeApi.instance(getContext()).getFolders(FileType.TYPE_DOC);
                for (FolderEntry entry : folderEntries) {
                    Utils.LOGE("--->>>", entry.bucketId + " name " + entry.bucketName);
                    for (String file : entry.getFiles()) {
                        Utils.LOGE("------", file);
                    }
                }
            }
        });

        Assert.assertEquals("not valid", true, History.isHistoryValid());
        Assert.assertEquals("drop fails", true, History.dropHistory());
    }
}
package com.security.manager;

import android.graphics.Bitmap;

import com.privacy.model.IntruderEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by song on 15/8/18.
 */
public class IntruderApi {

    public static ArrayList<IntruderEntry> getIntruders() {
        File intruderDir = makeDirValid();

        ArrayList<IntruderEntry> intruders = new ArrayList<>();
        String[] files = intruderDir.list();
        if (files != null) {
            DateFormat df = DateFormat.getDateTimeInstance();
            long now = System.currentTimeMillis();
            int count = files.length;
            for (String file : files) {
                IntruderEntry entry = new IntruderEntry();
                String date = file.substring(file.lastIndexOf('_') + 1);
                String pkg = file.substring(file.indexOf('_') + 1, file.lastIndexOf('_'));
                file = intruderDir.getAbsolutePath() + "/" + file;
                long time = Long.parseLong(date);
                //如果数量超过12个，并且时间已经超过10天，则直接删除
                if (count > 12) {
                    if (now - time > 864000000L) {
                        --count;
                        deleteIntruder(file);
                        continue;
                    }
                }
                entry.date = df.format(new Date(time));
                entry.url = file;
                entry.pkg = pkg;
                intruders.add(entry);
            }
            Collections.reverse(intruders);
        }

        return intruders;
    }

    private static File makeDirValid() {
        File intruderDir = new File(AppsCore.ROOT + "ic/");
        if (!(intruderDir.exists() && intruderDir.isDirectory())) {
            intruderDir.delete();
            intruderDir.mkdirs();
        }
        return intruderDir;
    }

    public static boolean deleteIntruder(String url) {
        return new File(url).delete();
    }

    public static boolean deleteIntruder(IntruderEntry intruder) {
        return new File(intruder.url).delete();
    }

    public static boolean addIntruder(Bitmap bmp, String pkg) {
        try {
            makeDirValid();
            FileOutputStream fos = new FileOutputStream(new File(AppsCore.ROOT + "ic/intruder_" + pkg + "_" + System.currentTimeMillis()));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            bmp.recycle();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

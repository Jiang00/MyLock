package com.security.manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.security.manager.lib.io.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SongHualin on 6/25/2015.
 */
public class SecurityProfileHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String NAME = "_PROFILES_";
    private static SecurityProfileHelper helper;

    public static synchronized SecurityProfileHelper singleton(Context context){
        if (helper == null){
            helper = new SecurityProfileHelper(context.getApplicationContext(), NAME, null, VERSION);
            helper.getWritableDatabase();
        }
        return helper;
    }

    public SecurityProfileHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SecurityProfileHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ProfileEntry.createProfileTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static class ProfileEntry implements BaseColumns {
        public long id;
        public String name;

        public static final String TABLE_PROFILES = "app_profiles";
        public static final String COLUMN_PROFILE_NAME = "profile_name";
        public static final String COLUMN_APP = "app";

        public static void createProfileTable(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE + TABLE_PROFILES + LEFT_PARENTHESIS + CREATE_ID + DOT + COLUMN_PROFILE_NAME + TEXT + RIGHT_PARENTHESIS);
        }

        public static long insertProfile(SQLiteDatabase db, String profileName) {
            ContentValues cv = new ContentValues(1);
            cv.put(COLUMN_PROFILE_NAME, profileName);
            return db.insert(TABLE_PROFILES, null, cv);
        }

        public static String getCreateSQL(String profileTable) {
            return CREATE_TABLE + profileTable + LEFT_PARENTHESIS + CREATE_ID + DOT +
                    COLUMN_APP + TEXT + RIGHT_PARENTHESIS;
        }

        /**
         * 创建profile
         *
         * @param db
         * @param profileName
         * @param apps
         * @throws Exception
         */
        public static long createProfile(SQLiteDatabase db, String profileName, List<String> apps) throws Exception {
            db.beginTransaction();
            try {
                long profileId = insertProfile(db, profileName);
                String tableName = profileIdToTableName(profileId);
                db.execSQL(getCreateSQL(tableName));

                ContentValues cv = new ContentValues(1);
                for (String app : apps) {
                    cv.put(COLUMN_APP, app);
                    db.insert(tableName, null, cv);
                }

                db.setTransactionSuccessful();
                return profileId;
            } finally {
                db.endTransaction();
            }
        }

        /**
         * 丢弃原表，重新创建新表，插入新的数据
         *
         * @param db
         * @param profileId
         * @param apps
         * @throws Exception
         */
        public static void updateProfile(SQLiteDatabase db, long profileId, List<String> apps) throws Exception {
            db.beginTransaction();
            try {
                String tableName = profileIdToTableName(profileId);
                db.execSQL(DROP_TABLE + tableName);
                db.execSQL(getCreateSQL(tableName));

                ContentValues cv = new ContentValues(1);
                for (String app : apps) {
                    cv.put(COLUMN_APP, app);
                    db.insert(tableName, null, cv);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        public static void deleteProfile(SQLiteDatabase db, long profileId) {
            String tableName = profileIdToTableName(profileId);
            db.execSQL(DROP_TABLE + tableName);
            db.delete(TABLE_PROFILES, _ID + "=" + profileId, null);
        }

        public static void deleteLockedApp(SQLiteDatabase db, long profileId, String app){
            db.delete(profileIdToTableName(profileId), COLUMN_APP + "=\"" + app + "\"", null);
        }

        public static List<ProfileEntry> getProfiles(SQLiteDatabase db) {
            List<ProfileEntry> profiles = new ArrayList<>();

            Cursor cursor = db.query(TABLE_PROFILES, new String[]{_ID, COLUMN_PROFILE_NAME}, null, null, null, null, COLUMN_PROFILE_NAME);

            if (cursor.moveToFirst()) {
                do {
                    ProfileEntry profileEntry = new ProfileEntry();
                    profileEntry.id = cursor.getLong(0);
                    profileEntry.name = cursor.getString(1);
                    profiles.add(profileEntry);
                } while (cursor.moveToNext());
            }

            cursor.close();

            return profiles;
        }

        public static Map<String, Boolean> getLockedApps(SQLiteDatabase db, long profileId) {
            Map<String, Boolean> profiles = new HashMap<>();

            Cursor cursor = db.query(profileIdToTableName(profileId), new String[]{COLUMN_APP}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    profiles.put(cursor.getString(0), true);
                } while (cursor.moveToNext());
            }
            cursor.close();

            return profiles;
        }

        public static String profileIdToTableName(long profileId) {
            return "profile_" + profileId;
        }

        public static void addLockedApp(SQLiteDatabase db, long profileId, String pkg) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_APP, pkg);
            db.insert(profileIdToTableName(profileId), null, cv);
        }
    }
}

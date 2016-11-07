package com.security.manager.lib.io;

/**
 * Created by SongHualin on 6/23/2015.
 */
public interface BaseColumns {
    /**
     * The unique ID for a row.
     * <P>Type: INTEGER (long)</P>
     */
    public static final String _ID = "_id";

    /**
     * The count of rows in a directory.
     * <P>Type: INTEGER</P>
     */
    public static final String _COUNT = "_count";

    public static final String DROP_TABLE = " DROP TABLE IF EXISTS ";
    public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS ";
    public static final String CREATE_ID = _ID + " INTEGER PRIMARY KEY AUTOINCREMENT ";
    public static final String DOT = ", ";
    public static final String LEFT_PARENTHESIS = " ( ";
    public static final String RIGHT_PARENTHESIS = " ) ";
    public static final String TEXT = " TEXT ";
    public static final String INT = " INTEGER ";
    public static final String FLOAT = " REAL ";
    public static final String BLOB = " BLOB ";
    public static final String BOOLEAN = " NUMERIC ";
    public static final String EQU = " = ";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String NOT = " NOT ";
    public static final String IN = " in ";
    public static final String TEXT_EQU = " = \"";
    public static final String TEXT_EQU_END = "\"";
}
package com.example.shushandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;
/**
 * @apiNote Helper database manager class. Refer to https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
 * @author  Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 */

public class DatabaseManager extends SQLiteOpenHelper {

    /**
     * @apiNote BaseColumns is an interface that provides 2 constants _ID and _COUNT that are automatic
     * columns in the respective database
     */

    public class DatabaseEntry implements BaseColumns {
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String SUPP = "supp"; //supplemental data (radius or duration)

        private static final String TABLE_NAME = "ShushDB";

        //create table ShushDB (name varchar, type varchar, data varchar, supp varchar)

        public static final String CREATE_QUERY = "create table " + TABLE_NAME + "(" +
                _ID + " integer, " + // using BaseColumns constant
                NAME + " varchar, " +
                TYPE + " varchar, " +
                DATA + " varchar, " +
                SUPP + " varchar)";

        public static final String DROP_QUERY = "drop table " + TABLE_NAME;
    }

    /**
     *
     * @param context Provide application context
     * @param name Provide name of the database
     * @param factory Provide cursor to navigate database to retrieve data
     * @param version Provide version of the database (API refers to number of database, so confused...) and call onUpgrade or onDowngrade appropriately
     */

    public DatabaseManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * @param sqLiteDatabase current database
     * @implNote method implemented when the database has been created for the first time
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseEntry.CREATE_QUERY);
    }

    /**
     *
     * @param sqLiteDatabase current database
     * @param i old version number
     * @param i1 new version number
     * @implNote implemented when the database needs an upgrade. onDowngrade() exists as well
     */

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DatabaseEntry.DROP_QUERY); // Drop previous version
        sqLiteDatabase.execSQL(DatabaseEntry.CREATE_QUERY); // Adopt with new version (still need to understand how to assign version. Maybe another constructor?)
    }
}

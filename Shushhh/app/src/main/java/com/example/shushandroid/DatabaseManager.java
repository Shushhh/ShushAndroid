package com.example.shushandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public class DatabaseEntry {
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String DATA = "data";
        public static final String SUPP = "supp"; //supplemental data (radius or duration)
        public static final String UUID = "uuid";

        public static final String TABLE_NAME = "ShushDB";

        //create table ShushDB (name varchar, type varchar, data varchar, supp varchar)

        public static final String CREATE_QUERY = "create table " + TABLE_NAME + "(" +
                UUID + " varchar , " +
                NAME + " varchar, " +
                TYPE + " varchar, " +
                DATA + " varchar, " +
                SUPP + " varchar) ";

        public static final String DROP_QUERY = "drop table if exists " + TABLE_NAME;
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

    public DatabaseManager(@Nullable Context context) {
        super(context, DatabaseEntry.TABLE_NAME, null, 3);
    }

    /**
     * @param sqLiteDatabase current database
     * @implNote method implemented when the database has been created for the first time
     * @apiNote onCreate is only called when a database that doesn't exist is attempted to be created
     *          so this will be called when getWriteable or getReadable is called for the first time
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

    /**
     *
     * @param shushObject insert a particular ShushObject
     * @return return true if successful, false otherwise
     */

    public boolean insert(ShushObject shushObject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseEntry.NAME, shushObject.getName());
        contentValues.put(DatabaseEntry.TYPE, shushObject.getType());
        contentValues.put(DatabaseEntry.DATA, shushObject.getData());
        contentValues.put(DatabaseEntry.SUPP, shushObject.getSupplementalData());
        contentValues.put(DatabaseEntry.UUID, shushObject.getUUID());
        long n = this.getWritableDatabase().insert(DatabaseEntry.TABLE_NAME, null, contentValues);

        if (n == -1) return false;
            else return true;
    }

    /**
     * @param shushObject provide ShushObject to update the SQLiteDatabase
     * @implNote update certain object using the UUID key provided by the method in TimeDialog and use WHERE clause with '=?' (doesn't work otherwise - possibly API restrictions)
     */

    public boolean update (ShushObject shushObject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseEntry.NAME, shushObject.getName());
        contentValues.put(DatabaseEntry.TYPE, shushObject.getType());
        contentValues.put(DatabaseEntry.DATA, shushObject.getData());
        contentValues.put(DatabaseEntry.SUPP, shushObject.getSupplementalData());
        contentValues.put(DatabaseEntry.UUID, shushObject.getUUID());
        long n = this.getWritableDatabase().update(DatabaseEntry.TABLE_NAME, contentValues, DatabaseEntry.UUID + "=?", new String[] {shushObject.getUUID()});
        if (n > 0) return true;
            else return false;
    }

    /**
     * @apiNote use a cursor object to traverse the database and retrieve data
     * @return returns list of ShushObject items from the database
     */

    private ArrayList retrieveWithCursor(Cursor cursor) {
        List shushObjectArrayList = new ArrayList();
        if(cursor.moveToFirst()) {
            do {

                String name = cursor.getString(cursor.getColumnIndex(DatabaseEntry.NAME));
                String type = cursor.getString(cursor.getColumnIndex(DatabaseEntry.TYPE));
                String data = cursor.getString(cursor.getColumnIndex(DatabaseEntry.DATA));
                String supp = cursor.getString(cursor.getColumnIndex(DatabaseEntry.SUPP));
                String uuid = cursor.getString(cursor.getColumnIndex(DatabaseEntry.UUID));

                ShushObject shushObject = new ShushObject(name, type, data, supp, uuid);

                shushObjectArrayList.add(shushObject);

            } while (cursor.moveToNext());
        } else {
            Log.e("Database Information", "Error");
        }
        cursor.close();
        return (ArrayList) shushObjectArrayList;
    }

    public ArrayList retrieveWithTAG(final String TAG) {
        Cursor cursor = this.getReadableDatabase().rawQuery("select * from " + DatabaseEntry.TABLE_NAME + " where " + DatabaseEntry.TYPE
                + " = '" + TAG + "'", null);
        return retrieveWithCursor(cursor);
    }

    /**
     * @implNote implement only to clear existing database with test values
     */
    public void deleteDatabase() {
        this.getWritableDatabase().delete(DatabaseEntry.TABLE_NAME, null, null);
    }

}

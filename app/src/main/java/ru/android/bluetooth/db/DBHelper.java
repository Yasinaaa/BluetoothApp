package ru.android.bluetooth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.android.bluetooth.schedule.helper.OneDayModel;

/**
 * Created by yasina on 23.08.17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_autorele";
    private static final String TABLE_CONTACTS = "schedule";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "work_date";
    private static final String KEY_ON_MIN = "on_min";
    private static final String KEY_OFF_MIN = "off_min";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT,"
                + KEY_ON_MIN + " TEXT,"
                + KEY_OFF_MIN + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContact(OneDayModel oneDayModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, oneDayModel.getDay());
        values.put(KEY_ON_MIN, oneDayModel.getOnMinutes());
        values.put(KEY_OFF_MIN, oneDayModel.getOffMinutes());
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    public OneDayModel getOneDayModel(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_DATE, KEY_ON_MIN, KEY_OFF_MIN}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        OneDayModel oneDayModel = new OneDayModel(cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
        return oneDayModel;
    }


    public List<OneDayModel> getAllOneDayModels() {
        List<OneDayModel> contactList = new ArrayList<OneDayModel>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OneDayModel oneDayModel = new OneDayModel(cursor.getString(1),
                        cursor.getString(2), cursor.getString(3));
                contactList.add(oneDayModel);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public int updateContact(OneDayModel oneDayModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, oneDayModel.getDay());
        values.put(KEY_ON_MIN, oneDayModel.getOnMinutes());
        values.put(KEY_OFF_MIN, oneDayModel.getOffMinutes());

        return db.update(TABLE_CONTACTS, values, KEY_DATE + " = ?",
                new String[] { String.valueOf(oneDayModel.getDay()) });
    }


    public void deleteContact(OneDayModel oneDayModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_DATE + " = ?",
                new String[] { String.valueOf(oneDayModel.getDay()) });
        db.close();
    }

    public int getOneDayModelCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }


}

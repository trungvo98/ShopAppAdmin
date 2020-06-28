package com.example.razor_studios.shopappshoppers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "shopappadmin";
    public static final String TABLE_NAME = "useraccount";
    public static final int DB_VERSION = 1;
    public static String USER_NAME="userName", USER_EMAIL="userEmail", USER_PASS="userPass", USER_FULLNAME="userFullName",PHONE_NUMBER="phoneNumber", USER_LANG="userLang";
    public static String CREATE_TABLE = "CREATE TABLE useraccount (userName text, userEmail text, userPass text, userFullName text, phoneNumber text, userLang text)";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS useraccount";
    public static final String TRUNCATE_TABLE = "DELETE FROM useraccount";

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }
    public Cursor readAccount(SQLiteDatabase sqLiteDatabase) {
        String[] values = {USER_NAME, USER_EMAIL, USER_PASS, USER_FULLNAME, PHONE_NUMBER, USER_LANG};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, values, null,null, null, null, null);
        return cursor;
    }
    public void addAccount(String uName, String uEmail, String uPass, String uFullName, String pNumber, String uLang, SQLiteDatabase sqLiteDatabase) {
        ContentValues cValues = new ContentValues();
        cValues.put(USER_NAME, uName);
        cValues.put(USER_EMAIL, uEmail);
        cValues.put(USER_PASS, uPass);
        cValues.put(USER_FULLNAME, uFullName);
        cValues.put(PHONE_NUMBER, pNumber);
        cValues.put(USER_LANG, uLang);

        sqLiteDatabase.insert(TABLE_NAME, null, cValues);
    }
    public void removeAccount(String uName, SQLiteDatabase sqLiteDatabase) {
        String selection = USER_NAME + " = '" + uName + "'";
        sqLiteDatabase.delete(TABLE_NAME, selection,null);
    }
    public void updateUserLanguage(String uLang, String uName, SQLiteDatabase sqLiteDatabase) {
        ContentValues cValues = new ContentValues();
        cValues.put(USER_LANG, uLang);

        String selection = USER_NAME + " = '" + uName + "'";
        sqLiteDatabase.update(TABLE_NAME, cValues, selection,null);
    }
}

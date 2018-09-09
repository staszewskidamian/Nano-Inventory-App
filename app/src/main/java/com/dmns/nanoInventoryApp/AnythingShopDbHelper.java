package com.dmns.nanoInventoryApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dmns.nanoInventoryApp.AnythingShopContract.AnythingShopEntry;

/**
 * Database helper for this app. Manages database creation and version management.
 */

public class AnythingShopDbHelper extends SQLiteOpenHelper {

    // Log tag
    public static final String LOG_TAG = AnythingShopDbHelper.class.getSimpleName();

    // Name of the database file
    private static final String DATABASE_NAME = "stock.db";

    // Database version number
    private static final int DATABASE_VERSION = 1;

    // InventoryDbHelper Constructor
    public AnythingShopDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created the first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // String used for creating a database
        String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + AnythingShopEntry.TABLE_NAME + " ("
                + AnythingShopEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AnythingShopEntry.ITEM_NAME + " TEXT NOT NULL, "
                + AnythingShopEntry.ITEM_PRICE + " REAL NOT NULL, "
                + AnythingShopEntry.ITEM_SUPPLY + " TEXT NOT NULL,"
                + AnythingShopEntry.ITEM_PHONE + " TEXT NOT NULL,"
                + AnythingShopEntry.ITEM_EMAIL + " TEXT NOT NULL,"
                + AnythingShopEntry.ITEM_IMAGE + " TEXT NOT NULL,"
                + AnythingShopEntry.ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";

        // Execute SQL
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    /**
     * Called when the table needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}


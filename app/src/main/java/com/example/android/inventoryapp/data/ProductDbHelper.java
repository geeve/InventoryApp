package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

/**
 * Created by Administrator on 2017/7/29 0029.
 * com.example.android.inventoryapp.data,InventoryApp
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    //name of the database
    public static final String DB_NAME = "inventory.db";

    public static final int  DB_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_PRODUCT_TALBE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME + " TEXT NOT NULL,"    //名字
                + ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT + " INTEGER NOT NULL DEFAULT 0,"   //库存
                + ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE + " FLOAT NOT NULL DEFAULT 0.0,"
                + ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES + " INTEGER DEFAULT 0,"
                + ProductContract.ProductEntry.COLUMS_PRODUCT_PIC + " TEXT,"
                + ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_EMAIL + " TEXT,"
                + ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_TEL + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TALBE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

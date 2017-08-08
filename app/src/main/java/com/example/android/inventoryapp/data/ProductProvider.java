package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/7/29 0029.
 * com.example.android.inventoryapp.data,InventoryApp
 */

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();


    public static final int PRODUCT = 100;

    public static final int PRODUDT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PRODUCT_PATH,PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PRODUCT_PATH + "/#", PRODUDT_ID);
    }

    private ProductDbHelper mProductDbHelper;

    @Override
    public boolean onCreate() {

        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mProductDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match){
            case PRODUCT:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;
            case PRODUDT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //cursor有变化后通知
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    private Uri insertProduct(Uri uri,ContentValues contentValues){
        String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME);

        if(TextUtils.isEmpty(name)){
            throw new IllegalArgumentException("Product requir a name!");
        }

        int intventory = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT);

        float price = contentValues.getAsFloat(ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE);

        int sales = contentValues.getAsInteger(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES);

        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();

        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME,null,contentValues);

        //通知更改
        getContext().getContentResolver().notifyChange(uri,null);

        //返回带有id的uri
        return ContentUris.withAppendedId(uri,id);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return deleteProduct(uri,s,strings);
            case PRODUDT_ID:
                s = ProductContract.ProductEntry._ID + "=?";
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return deleteProduct(uri,s,strings);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    public int deleteProduct(Uri uri,String selection,String[] selectionArgs){
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();

        int result = db.delete(ProductContract.ProductEntry.TABLE_NAME,selection,selectionArgs);

        if(result != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return  updateProduct(uri,contentValues,selection,selectionArgs);
            case PRODUDT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri,ContentValues contentValues,String selection, String[] selectionArgs){

        if(contentValues.containsKey(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME)){
            String name = contentValues.getAsString(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME);
            if(TextUtils.isEmpty(name)){
                throw new IllegalArgumentException("Product must have name!");
            }
        }

        if(contentValues.size() == 0){
            return 0;
        }

        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();

        int rowId = db.update(ProductContract.ProductEntry.TABLE_NAME,contentValues,selection,selectionArgs);

        if(rowId != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowId;
    }
}

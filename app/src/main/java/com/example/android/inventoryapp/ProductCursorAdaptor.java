package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;

/**
 * Created by Administrator on 2017/8/1 0001.
 * com.example.android.inventoryapp,InventoryApp
 */

public class ProductCursorAdaptor extends CursorAdapter {
    private Context mContent;


    public ProductCursorAdaptor(Context context, Cursor c,int flags) {
        super(context, c,flags);

        this.mContent = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView tvName = (TextView)view.findViewById(R.id.product_name);
        TextView tvSales = (TextView)view.findViewById(R.id.product_sales);
        TextView tvInventory =  (TextView)view.findViewById(R.id.product_invetory);
        TextView tvPrice = (TextView)view.findViewById(R.id.product_price);
        TextView btSales = (Button)view.findViewById(R.id.bt_sales);


        tvName.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME)));
        tvPrice.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE)));
        tvInventory.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT)));
        tvSales.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES)));

        final int inventory = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT));
        final int sales = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES));
        final int currentId = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));

        //sales 按钮点击库存减1，sales加1
        btSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,currentId);
                //只有当库存大于0时才能销售并减1
                if(inventory>0){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT,inventory-1);
                    contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES,sales+1);
                    String selection = ProductContract.ProductEntry._ID + "=?";
                    String[] selectionArg = {uri.getLastPathSegment().toString()};
                    int rowId = mContent.getContentResolver().update(uri,contentValues,selection,selectionArg);

                    if(rowId != 0){
                        Toast.makeText(mContent, R.string.product_sales,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


}

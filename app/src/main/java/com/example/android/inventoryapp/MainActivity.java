package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.inventoryapp.data.ProductContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ListView listView;

    private ProductCursorAdaptor mProductCursorAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.product_list);

        mProductCursorAdaptor = new ProductCursorAdaptor(this,null,0);

        listView.setAdapter(mProductCursorAdaptor);

        getSupportLoaderManager().initLoader(0,null,this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,ProductEditActivity.class);

                startActivity(i);
            }
        });

        //响应Listview中的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,ProductEditActivity.class);
                //将uri作为数据传到intent中
                Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,l);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        //insertData();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] project = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES};
        return new CursorLoader(MainActivity.this, ProductContract.ProductEntry.CONTENT_URI,project,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductCursorAdaptor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdaptor.swapCursor(null);
    }
}

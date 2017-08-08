package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/7/29 0029.
 * com.example.android.inventoryapp.data,InventoryApp
 */

public class ProductContract {

    public ProductContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //存放产品的数据库名
    public static final String PRODUCT_PATH = "products";

    public static final class ProductEntry implements BaseColumns{

        //Products数据库的访问路径
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PRODUCT_PATH);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMNS_PRODUCT_NAME = "name";

        //当前数量
        public static final String COLUMNS_PRODUCT_COUNT = "quantity";

        public static final String COLUMNS_PRODUCT_PRICE = "price";

        //销售次数
        public static final String COLUMNS_PRODUCT_SALES = "sales";

        public static final String COLUMS_PRODUCT_PIC = "pic";

        //供应商电话
        public static final String COLUMS_PRODUCT_SUPPLIER_TEL = "supplier_tel";

        public static final String COLUMS_PRODUCT_SUPPLIER_EMAIL = "supplier_email";


    }
}

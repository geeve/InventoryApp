package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract;

import java.io.FileNotFoundException;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ProductEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtInventory;
    private EditText mEtSales;
    private EditText mEtTel;
    private EditText mEtEmail;

    private ImageButton mAlbumPic;

    private ImageView mProductPic;

    private String mPicPath;

    //保存从intent传过来的uri
    private Uri mUri;

    //表示内容是否有改动
    private boolean mProductHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    private int PIC_ALUM = 1;
    private String SHARE_INVENTORY_SET = getString(R.string.inventoryset);
    private String SHARE_MODIFY_STEP = getString(R.string.modify_step);
    private int mInventoryModifyStep = 0;
    private SharedPreferences inventoryPreferneces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(mUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            MenuItem menuItemInventory = menu.findItem(R.id.action_change_inventory);
            menuItemInventory.setVisible(false);
            MenuItem menuItemBook = menu.findItem(R.id.action_book);
            menuItemBook.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if(saveData()){
                    finish();
                }
                return true;
            case R.id.action_delete:
                DialogInterface.OnClickListener deleteButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteProduct();
                        finish();
                    }
                };

                showDeleteDialog(deleteButtonClickListener);
                return true;
            case R.id.action_change_inventory:
                showInventoryDailog();
                return true;
            case R.id.action_book:
                showBookDailog();
                return true;
            case android.R.id.home:
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(ProductEditActivity.this);
                    }
                };

                showUnsaveChangDialog(discardButtonClick);
                return true;
            case R.id.action_preference:
                showPreferenceDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //显示偏好设置值
    private void showPreferenceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText setNum = new EditText(this);
        inventoryPreferneces = getSharedPreferences(SHARE_INVENTORY_SET,MODE_PRIVATE);
        setNum.setText(inventoryPreferneces.getString(SHARE_MODIFY_STEP,"1"));

        builder.setTitle(R.string.set_modify_number_title).setView(setNum).setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!TextUtils.isEmpty(setNum.getText().toString().trim())){
                    if(Integer.parseInt(setNum.getText().toString().trim()) > 0){
                        inventoryPreferneces = getSharedPreferences(SHARE_INVENTORY_SET,MODE_PRIVATE);
                        //将新输入的数字保存到偏好设置里
                        inventoryPreferneces.edit().putString(SHARE_MODIFY_STEP,setNum.getText().toString().trim()).apply();
                        mInventoryModifyStep = Integer.parseInt(setNum.getText().toString().trim());
                    }
                }

                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //显示订货对话框
    private void showBookDailog(){
        new AlertDialog.Builder(this).setTitle(R.string.title_book_selection).setSingleChoiceItems(new String[]{"Tel", "Email"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               switch (i){
                   case 0:
                       if(TextUtils.isEmpty(mEtTel.getText().toString().trim())){
                           Toast.makeText(ProductEditActivity.this, R.string.no_tel,Toast.LENGTH_SHORT).show();
                           return;
                       }
                       Intent intent1 =  new Intent(Intent.ACTION_DIAL);
                       Uri data = Uri.parse("tel:"+mEtTel.getText().toString().trim());
                       intent1.setData(data);
                       startActivity(intent1);
                       break;
                   case 1:
                       if(TextUtils.isEmpty(mEtEmail.getText().toString().trim())){
                           Toast.makeText(ProductEditActivity.this, R.string.no_email,Toast.LENGTH_SHORT).show();
                           return;
                       }
                       Uri uri = Uri.parse("mailto:"+mEtEmail.getText().toString().trim());
                       Intent intent2 = new Intent(Intent.ACTION_SENDTO, uri);
                       intent2.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_email)); // 主题
                       intent2.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_email)); // 正文
                       startActivity(Intent.createChooser(intent2, getString(R.string.email_app)));
                       break;
               }

                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.cancel,null).show();
    }


    //显示修改inventory对话框
    private void showInventoryDailog(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dailog_inventory,(ViewGroup)findViewById(R.id.dailog));
        final EditText etNewInventory = (EditText) layout.findViewById(R.id.et_new_inventory);


        etNewInventory.setText(mEtInventory.getText().toString().trim());

        Button plus = (Button) layout.findViewById(R.id.dailog_plus);
        Button dec = (Button) layout.findViewById(R.id.dailog_dec);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = Integer.parseInt(etNewInventory.getText().toString().trim());
                i = i+mInventoryModifyStep;
                etNewInventory.setText(String.valueOf(i));
                mProductHasChanged = true;
            }
        });

        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = Integer.parseInt(etNewInventory.getText().toString().trim());
                //如果大于零则减，否则什么都不做
                if(i>mInventoryModifyStep){
                    i = i-mInventoryModifyStep;
                    etNewInventory.setText(String.valueOf(i));
                    mProductHasChanged = true;
                }
            }
        });

        new AlertDialog.Builder(this).setTitle(R.string.title_set_inventory).setView(layout).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newInventory = etNewInventory.getText().toString().trim();
                mEtInventory.setText(newInventory);
            }
        }).setNegativeButton(R.string.cancel,null).show();
    }

    /***
     * 检验数据是否合法，名称不能为空
     * @return
     */
    private boolean checkData(ContentValues data){
        String name = data.getAsString(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME);

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, R.string.name_cannot_null,Toast.LENGTH_SHORT).show();
            return false;
        }

        String inventory = data.getAsString(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT);
        if(TextUtils.isEmpty(inventory) || Integer.parseInt(inventory) < 0){//|| Integer.getInteger(inventory) < 0
            Toast.makeText(this, R.string.inventory_not_small,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    //保存更新产品信息
    private boolean saveData(){
        String name = mEtName.getText().toString().trim();
        String price = mEtPrice.getText().toString().trim();
        String inventory = mEtInventory.getText().toString().trim();
        String sales = mEtSales.getText().toString().trim();
        String tel = mEtTel.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();


        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT,inventory);
        contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES,sales);
        contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME,name);
        contentValues.put(ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE,price);
        contentValues.put(ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_EMAIL,email);
        contentValues.put(ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_TEL,tel);
        contentValues.put(ProductContract.ProductEntry.COLUMS_PRODUCT_PIC,mPicPath);

        //验证数据有效性
        if(!checkData(contentValues)){
            return false;
        }

        if(mUri != null){
            //更新数据库
            String selection = ProductContract.ProductEntry._ID + "=?";
            String[] selectionArgs = {String.valueOf(ContentUris.parseId(mUri))};

            int rowId = getContentResolver().update(mUri,contentValues,selection,selectionArgs);
            if(rowId > 0){
                Toast.makeText(this, R.string.update_sucess,Toast.LENGTH_SHORT).show();
            }
        }else {
            //插入一个新商品
            Uri uri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI,contentValues);

            long newRowId = ContentUris.parseId(uri);
            if(newRowId == -1){
                Toast.makeText(this, R.string.error_with_insert,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,getString(R.string.save_with_id)+newRowId,Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    //根据id删除某一个商品
    private void deleteProduct(){
        if(mUri == null){
            return;
        }

        String selection = ProductContract.ProductEntry._ID + "=?";
        String[] selectionArgs = {mUri.getLastPathSegment().toString()};

        getContentResolver().delete(mUri,selection,selectionArgs);
    }

    @Override
    public void onBackPressed() {
        if(!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };

        showUnsaveChangDialog(discardButtonClickListener);
    }

    //删除产品时的提示对话框
    private void showDeleteDialog(DialogInterface.OnClickListener deleteButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_product);
        builder.setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton(R.string.yes,deleteButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //编辑改动保存提示
    private void showUnsaveChangDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initData(){
        mEtEmail = (EditText)findViewById(R.id.et_supplier_email);
        mEtInventory = (EditText) findViewById(R.id.et_inventory);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtSales = (EditText)findViewById(R.id.et_sales);
        mEtTel = (EditText)findViewById(R.id.et_supplier_tel);

        /////////////////////////////////////////////////////
        mAlbumPic = (ImageButton)findViewById(R.id.bt_pic_ablum);

        mProductPic = (ImageView) findViewById(R.id.iv_product_pic);

        //通过图库获得图片
        mAlbumPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intent,PIC_ALUM);
            }
        });

        Intent intent = getIntent();

        mUri = intent.getData();

        if(mUri == null){
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        }else {
            setTitle(getString(R.string.edit_product));
        }

        //mEtInventory.setEnabled(false);
        mEtEmail.setOnTouchListener(mTouchListener);
        mEtTel.setOnTouchListener(mTouchListener);
        mEtInventory.setOnTouchListener(mTouchListener);
        mEtName.setOnTouchListener(mTouchListener);
        mEtSales.setOnTouchListener(mTouchListener);
        mEtPrice.setOnTouchListener(mTouchListener);

        inventoryPreferneces = getSharedPreferences(SHARE_INVENTORY_SET,MODE_PRIVATE);
        String inventoryStep = inventoryPreferneces.getString(SHARE_MODIFY_STEP,"1");
        mInventoryModifyStep = Integer.parseInt(inventoryStep);

        getSupportLoaderManager().initLoader(1,null,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                /* 将Bitmap设定到ImageView */
                mProductPic.setImageBitmap(bitmap);
                mPicPath = uri.toString();
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMS_PRODUCT_PIC,
                ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_EMAIL,
                ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_TEL};

        if(mUri != null){
            return new CursorLoader(ProductEditActivity.this,mUri,projection,null,null,null);
        }else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            return;
        }
        if(data.getCount() > 0){
            data.moveToFirst();

            mEtName.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_NAME)));
            mEtInventory.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_COUNT)));
            mEtSales.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_SALES)));
            mEtPrice.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMNS_PRODUCT_PRICE)));
            mEtEmail.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_EMAIL)));
            mEtTel.setText(data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMS_PRODUCT_SUPPLIER_TEL)));
            mPicPath = data.getString(data.getColumnIndex(ProductContract.ProductEntry.COLUMS_PRODUCT_PIC));

            if(!TextUtils.isEmpty(mPicPath)) {
                Log.e("uri",mPicPath);
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(Uri.parse(mPicPath)));
                    mProductPic.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

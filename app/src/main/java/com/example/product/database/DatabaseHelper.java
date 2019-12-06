package com.example.product.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.product.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database
    private static final int DATABASE_VER = 1;
    private static final String DATABASE_NAME = "Product_db";

    //Tabel Product
    private static final String TABLE_PRODUCT = "Tb_product";
    private static final String KEY_PRODUCT_ID = "id_product";
    private static final String KEY_PRODUCT_NAME = "name_product";
    private static final String KEY_PRODUCT_DESC = "desc_product";
    private static final String KEY_PRODUCT_IMAGE = "image_product";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_PRODUCT_ID + " TEXT PRIMARY KEY,"
                + KEY_PRODUCT_NAME + " TEXT,"
                + KEY_PRODUCT_DESC + " TEXT,"
                + KEY_PRODUCT_IMAGE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(sqLiteDatabase);

    }

    public void addProduct(ProductModel product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues addValues = new ContentValues();
        addValues.put(KEY_PRODUCT_ID, product.getProduct_id());
        addValues.put(KEY_PRODUCT_NAME, product.getProduct_name());
        addValues.put(KEY_PRODUCT_DESC, product.getProduct_desc());
        addValues.put(KEY_PRODUCT_IMAGE, product.getProduct_image());
        Log.i("Insert SQLite", String.valueOf(addValues));

        db.insert(TABLE_PRODUCT, null, addValues);
        db.close();

    }

    public void updateProduct(ProductModel product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_PRODUCT_NAME, product.getProduct_name());
        updateValues.put(KEY_PRODUCT_DESC, product.getProduct_desc());
        updateValues.put(KEY_PRODUCT_IMAGE, product.getProduct_image());
        Log.i("Update SQLite", String.valueOf(updateValues));

        db.update(TABLE_PRODUCT, updateValues, KEY_PRODUCT_ID + " =?", new String[]{String.valueOf(product.getProduct_id())});
        db.close();
    }

    public void deleteProductById(ProductModel product) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_PRODUCT_ID + " =?", new String[]{String.valueOf(product.getProduct_id())});
        db.close();

    }

    public ProductModel getProductById(String product_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{
                        KEY_PRODUCT_ID,
                        KEY_PRODUCT_NAME,
                        KEY_PRODUCT_DESC,
                        KEY_PRODUCT_IMAGE}, KEY_PRODUCT_ID + "=?",
                new String[]{String.valueOf(product_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return new ProductModel(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3));

    }

    public List<ProductModel> getAllProduct() {
        List<ProductModel> listProduct = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProductModel product = new ProductModel();
                product.setProduct_id(cursor.getString(0));
                product.setProduct_name(cursor.getString(1));
                product.setProduct_desc(cursor.getString(2));
                product.setProduct_image(cursor.getString(3));

                listProduct.add(product);

            }
            while (cursor.moveToNext());

        }
        return listProduct;

    }
}

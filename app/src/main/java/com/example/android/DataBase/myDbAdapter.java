package com.example.android.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.Cart.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class myDbAdapter {
    myDbHelper myhelper;

    public myDbAdapter(Context context) {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String name, String description, Double price, Double weight, String imagePath, int catId, int productId, int userId, int quantity, Boolean isDeliveryFree) {

        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.id, myDbHelper.quantity};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, "productId = " + productId, null, null, null, null);
        if (cursor.moveToNext()) {
            updateQuantity(productId, cursor.getInt(cursor.getColumnIndex(myDbHelper.quantity)) + 1);
            db.close();
            return 0;
        } else {
            SQLiteDatabase dbb = myhelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(myDbHelper.name, name);
            contentValues.put(myDbHelper.description, description);
            contentValues.put(myDbHelper.price, price);
            contentValues.put(myDbHelper.weight, weight);
            contentValues.put(myDbHelper.imagePath, imagePath);
            contentValues.put(myDbHelper.catId, catId);
            contentValues.put(myDbHelper.productId, productId);
            contentValues.put(myDbHelper.userId, userId);
            contentValues.put(myDbHelper.quantity, quantity);
            contentValues.put(myDbHelper.isDeliveryFree, String.valueOf(isDeliveryFree));
            long id = dbb.insert(myDbHelper.TABLE_NAME, null, contentValues);
            db.close();
            return id;
        }

    }

    public List<CartItem> getData() {
        List<CartItem> cartItemList = new ArrayList<>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.id, myDbHelper.name, myDbHelper.description, myDbHelper.price, myDbHelper.weight, myDbHelper.imagePath, myDbHelper.catId, myDbHelper.productId, myDbHelper.userId, myDbHelper.quantity, myDbHelper.isDeliveryFree};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
//        StringBuffer buffer = new StringBuffer();
        CartItem item;
        while (cursor.moveToNext()) {
            item = new CartItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(myDbHelper.id)));
            item.setName(cursor.getString(cursor.getColumnIndex(myDbHelper.name)));
            item.setDescription(cursor.getString(cursor.getColumnIndex(myDbHelper.description)));
            item.setPrice(cursor.getDouble(cursor.getColumnIndex(myDbHelper.price)));
            item.setWeight(cursor.getDouble(cursor.getColumnIndex(myDbHelper.weight)));
            item.setImagePath(cursor.getString(cursor.getColumnIndex(myDbHelper.imagePath)));
            item.setCatId(cursor.getInt(cursor.getColumnIndex(myDbHelper.catId)));
            item.setProductId(cursor.getInt(cursor.getColumnIndex(myDbHelper.productId)));
            item.setUserId(cursor.getInt(cursor.getColumnIndex(myDbHelper.userId)));
            item.setQuantity(cursor.getInt(cursor.getColumnIndex(myDbHelper.quantity)));
            if (cursor.getString(cursor.getColumnIndex(myDbHelper.isDeliveryFree)).equals("true")) item.setDeliveryFree(true);
            else item.setDeliveryFree(false);
            cartItemList.add(item);
            // buffer.append(cid+ "   " + name + "   " + password +" \n");
        }
        db.close();
        return cartItemList;
    }

    public int getCount() {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.id};
        Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int deleteAll(int userID) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        int count = db.delete(myDbHelper.TABLE_NAME, myDbHelper.userId + "=" + userID, null);
        db.close();
        return count;
    }

    public int deleteProduct(int productId) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        int count = db.delete(myDbHelper.TABLE_NAME, myDbHelper.productId + " = " + productId, null);
        db.close();
        return count;
    }

    public int updateQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.quantity, newQuantity);
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.productId + "=" + productId, null);
        db.close();
        return count;
    }

    public int removeQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.quantity, newQuantity);
        int count = db.update(myDbHelper.TABLE_NAME, contentValues, myDbHelper.productId + "=" + productId, null);
        db.close();
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "Database";    // Database Name
        private static final String TABLE_NAME = "cart";   // Table Name
        private static final int DATABASE_Version = 2;  // Database Version

        private static final String id = "id";     // Column 1 (Primary Key)
        private static final String name = "name";    //Column 2
        private static final String description = "description";    // Column 3
        private static final String price = "price";     // Column 4
        private static final String weight = "weight";    //Column 5
        private static final String imagePath = "imagePath";    // Column 6
        private static final String catId = "catId";     // Column 7
        private static final String productId = "productId";    // Column 8
        private static final String userId = "userId";    //Column 9
        private static final String quantity = "quantity";    // Column 10
        private static final String  isDeliveryFree = "isDeliveryFree";    // Column 11


        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                name + " VARCHAR(255) ," +
                description + " VARCHAR(255) ," +
                price + " REAL ," +
                weight + " REAL ," +
                imagePath + " VARCHAR(255) ," +
                catId + " INTEGER ," +
                productId + " INTEGER ," +
                userId + " INTEGER ," +
                quantity + " INTEGER ,"+
                isDeliveryFree + " VARCHAR(255) " + ");";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
//                Log.e("i","db error creat : "+e.toString());
//                 Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                // Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (Exception e) {
                // Message.message(context,""+e);
            }
        }
    }
}
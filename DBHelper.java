package com.example.stationeryshop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StationeryDB";
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createProductsTable(db);
        createPurchasesTable(db);

        addProduct(db, "Blue Pen", "Writing", 10);
        addProduct(db, "Notebook", "Books", 50);
        addProduct(db, "Pencil", "Writing", 5);
        addProduct(db, "Eraser", "Accessories", 5);
        addProduct(db, "Marker", "Writing", 25);
        addProduct(db, "Scale", "Accessories", 15);
        addProduct(db, "Box", "Accessories", 50);
        addProduct(db, "Pencil Box", "Accessories", 80);
        addProduct(db, "School Bag", "Bags", 350);
        addProduct(db, "Chart Paper", "Paper", 20);
        addProduct(db, "Glue Stick", "Accessories", 30);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Rebuild purchases so an old/incorrect schema can't break inserts
        db.execSQL("DROP TABLE IF EXISTS purchases");
        createPurchasesTable(db);
    }

    private void createProductsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "category TEXT, " +
                "price REAL)");
    }

    private void createPurchasesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS purchases (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_name TEXT, " +
                "quantity INTEGER, " +
                "total_price REAL)");
    }

    private void addProduct(SQLiteDatabase db, String name,
                            String category, double price) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        db.insert("products", null, values);
    }

    public Cursor searchProducts(String keyword) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?",
                new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    public boolean savePurchase(String productName, int quantity, double totalPrice) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("product_name", productName);
        values.put("quantity", quantity);
        values.put("total_price", totalPrice);

        long result = db.insert("purchases", null, values);
        return result != -1;
    }

    public Cursor getProducts() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM products ORDER BY name", null);
    }
}

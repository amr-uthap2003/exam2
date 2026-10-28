package com.example.stationeryshop;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "StationeryShop";

    private DBHelper dbHelper;
    private EditText etSearch, etQuantity;
    private Button btnSearch, btnPurchase;
    private TextView tvResults, tvSelected, tvStatus;

    private String selectedName = null;
    private double selectedPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        etSearch = findViewById(R.id.etSearch);
        etQuantity = findViewById(R.id.etQuantity);
        btnSearch = findViewById(R.id.btnSearch);
        btnPurchase = findViewById(R.id.btnPurchase);
        tvResults = findViewById(R.id.tvResults);
        tvSelected = findViewById(R.id.tvSelected);
        tvStatus = findViewById(R.id.tvStatus);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProducts();
            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseSelectedProduct();
            }
        });
    }

    private void searchProducts() {
        String keyword = etSearch.getText().toString().trim();
        selectedName = null;
        selectedPrice = 0;
        tvSelected.setText("");
        tvStatus.setText("");

        if (keyword.isEmpty()) {
            tvResults.setText("");
            tvStatus.setText("Enter a product name or category.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        Cursor cursor = dbHelper.searchProducts(keyword);
        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                if (selectedName == null) {
                    selectedName = name;
                    selectedPrice = price;
                }

                sb.append("Product: ").append(name).append("\n")
                        .append("Category: ").append(category).append("\n")
                        .append(String.format(Locale.US, "Price: ₹%.2f", price)).append("\n")
                        .append("--------------------\n");
            }
        } finally {
            cursor.close();
        }

        if (selectedName == null) {
            tvResults.setText("");
            tvStatus.setText("No products found.");
        } else {
            tvResults.setText(sb.toString());
            tvSelected.setText("First matching product selected for purchase.");
        }
    }

    private void purchaseSelectedProduct() {
        if (selectedName == null) {
            tvStatus.setText("Search and select a product first.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            tvStatus.setText("Enter a valid quantity.");
            return;
        }

        if (quantity <= 0) {
            tvStatus.setText("Quantity must be at least 1.");
            return;
        }

        double total = selectedPrice * quantity;
        boolean saved = dbHelper.savePurchase(selectedName, quantity, total);
        Log.d(TAG, "savePurchase(" + selectedName + ", " + quantity + ", " + total + ") = " + saved);

        if (saved) {
            tvStatus.setText(String.format(Locale.US,
                    "Purchased %d x %s. Total: ₹%.2f", quantity, selectedName, total));
        } else {
            tvStatus.setText("Purchase failed. Please try again.");
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
package com.example.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.product.database.DatabaseHelper;
import com.example.product.model.ProductModel;
import com.google.android.material.snackbar.Snackbar;

public class EditActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductModel product;

    private EditText etName, etDesc;
    private Button btnEdit, btnImagePicker;

    private String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBProduct = new DatabaseHelper(this);
        product_id = getIntent().getExtras().getString("id_product");
        initialization();
        initGetEditProduct();

        btnImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEditProduct(v);
            }
        });
    }

    private void initialization() {
        etName = findViewById(R.id.et_name_product_edit);
        etDesc = findViewById(R.id.et_desc_product_edit);
        btnEdit = findViewById(R.id.btn_edit_product);
        btnImagePicker = findViewById(R.id.btn_image_picker_edit);
    }

    private void initGetEditProduct() {
        product = DBProduct.getProductById(product_id);

        etName.setText(product.getProduct_name());
        etDesc.setText(product.getProduct_desc());
    }

    private void initEditProduct(View v) {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        product = new ProductModel();
        product.setProduct_id(product_id);
        product.setProduct_name(name);
        product.setProduct_desc(desc);
        product.setProduct_image(null);

        Toast.makeText(getApplicationContext(), "Product berhasil diedit", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DBProduct.updateProduct(product);

                Intent toHome = new Intent(getApplicationContext(), MainActivity.class);
                toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toHome);
                finish();

            }
        }, 500);
    }
}

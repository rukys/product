package com.example.product;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.product.database.DatabaseHelper;
import com.example.product.model.ProductModel;

import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductModel product;

    private TextView tvId, tvName, tvDesc;
    private Button btnToEdit, btnDelete;
    private ImageView imgDetail;

    private String product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Detail Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBProduct = new DatabaseHelper(this);
        product_id = getIntent().getExtras().getString("id_product");

        initialization();
        initGetDetailProduct(product_id);

        btnToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(getApplicationContext(), EditActivity.class);
                toEdit.putExtra("id_product", product_id);
                startActivity(toEdit);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDeleteProduct();
            }
        });
    }

    private void initialization() {
        tvId = findViewById(R.id.tv_id_product_detail);
        tvName = findViewById(R.id.tv_name_product_detail);
        tvDesc = findViewById(R.id.tv_desc_product_detail);
        btnToEdit = findViewById(R.id.btn_edit_product_detail);
        btnDelete = findViewById(R.id.btn_hapus_product_detail);
        imgDetail = findViewById(R.id.img_product_detail);
    }

    private void initDeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setMessage("Apa anda yakin ingin menghapus product ini ?")
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        product = new ProductModel();
                        product.setProduct_id(product_id);

                        DBProduct.deleteProductById(product);
                        DetailActivity.super.onBackPressed();
                        finish();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initGetDetailProduct(String product_id) {
        product = DBProduct.getProductById(product_id);

        tvId.setText(product.getProduct_id());
        tvName.setText(product.getProduct_name());
        tvDesc.setText(product.getProduct_desc());

        byte[] imageByte = Base64.decode(product.getProduct_image(), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

        imgDetail.setVisibility(View.VISIBLE);
        imgDetail.setImageBitmap(bm);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

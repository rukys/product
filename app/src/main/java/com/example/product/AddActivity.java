package com.example.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.product.database.DatabaseHelper;
import com.example.product.model.ProductModel;
import com.example.product.utils.ConvertToBase64;
import com.google.android.material.snackbar.Snackbar;

public class AddActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductModel product;

    private EditText etId, etName, etDesc;
    private Button btnPhotoPicker, btnSave;
    private ImageView imgProduct;

    public static final int GALLERY_REQUEST_CODE = 001;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBProduct = new DatabaseHelper(this);
        initialization();

        btnPhotoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAddProduct();
            }
        });
    }

    private void initialization() {
        etId = findViewById(R.id.et_id_product);
        etName = findViewById(R.id.et_name_product);
        etDesc = findViewById(R.id.et_desc_product);
        btnPhotoPicker = findViewById(R.id.btn_image_picker);
        btnSave = findViewById(R.id.btn_save_product);
        imgProduct = findViewById(R.id.img_product);
    }

    private void pickFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    private void convertBase64(){
        imgProduct.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgProduct.getDrawingCache();

        ConvertToBase64 tb = new ConvertToBase64();
        encodedImage = tb.bitmapToBase64(bitmap, "JPEG");
    }

    private void initAddProduct() {
        String id = etId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (!id.isEmpty() && name.isEmpty() && desc.isEmpty()){
            Toast.makeText(getApplicationContext(), "id", Toast.LENGTH_LONG).show();

        } else if (!id.isEmpty() && !name.isEmpty() && desc.isEmpty()){
            Toast.makeText(getApplicationContext(), "name", Toast.LENGTH_LONG).show();

        } else if (!id.isEmpty() && !name.isEmpty() && !desc.isEmpty()) {
            Toast.makeText(getApplicationContext(), "desc", Toast.LENGTH_LONG).show();
        }
//        } else if (!id.isEmpty() && !name.isEmpty() || desc.isEmpty()){
//            Toast.makeText(getApplicationContext(), "deskripsi harus di isi", Toast.LENGTH_LONG).show();
//
//        }
        //else {
//            convertBase64();
//
//            product = new ProductModel();
//            product.setProduct_id(id);
//            product.setProduct_name(name);
//            product.setProduct_desc(desc);
//            product.setProduct_image(encodedImage);
//            DBProduct.addProduct(product);
//
//            Toast.makeText(getApplicationContext(), "Product berhasil ditambah", Toast.LENGTH_LONG).show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    AddActivity.super.onBackPressed();
//                    finish();
//
//                }
//            }, 500);
        //}

    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    imgProduct.setVisibility(View.VISIBLE);
                    imgProduct.setImageURI(selectedImage);
                    break;
            }
    }
}

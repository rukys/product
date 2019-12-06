package com.example.product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.product.database.DatabaseHelper;
import com.example.product.model.ProductModel;
import com.example.product.utils.ConvertToBase64;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductModel product;

    private EditText etId, etName, etDesc;
    private Button btnPhotoPicker, btnSave;
    private ImageView imgProduct;

    public static final int GALLERY_REQUEST_CODE = 001;
    public static final int CAMERA_REQUEST_CODE = 002;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBProduct = new DatabaseHelper(this);
        initialization();

        btnPhotoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View dialogLayout = inflater.inflate(R.layout.dialog_pick_photo, null);

                final CharSequence[] options = {"Kamera", "Galeri"};

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true);
                //builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Kamera")) {
                            pickFromCamera();
                            dialog.dismiss();

                        } else if (options[item].equals("Galeri")) {
                            pickFromGallery();
                            dialog.dismiss();

                        }
                    }
                });
                builder.show();

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

    private void pickFromCamera() {
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());

        try {
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);

            } else {
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        }

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
        imgProduct.setDrawingCacheEnabled(true);
        Bitmap bm = imgProduct.getDrawingCache();

        if (id.isEmpty() ){
            etId.setError("id tidak boleh kosong");

        } else if (name.isEmpty()){
            etName.setError("nama tidak boleh kosong");

        } else if (desc.isEmpty()) {
            etDesc.setError("deksripsi tidak boleh kosong");

        }

        else if (bm == null){
            Toast.makeText(getApplicationContext(), "Upload foto terlebih dahulu", Toast.LENGTH_LONG).show();

        }

        else {
            convertBase64();

            product = new ProductModel();
            product.setProduct_id(id);
            product.setProduct_name(name);
            product.setProduct_desc(desc);
            product.setProduct_image(encodedImage);
            DBProduct.addProduct(product);

            Toast.makeText(getApplicationContext(), "Product berhasil ditambah", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AddActivity.super.onBackPressed();
                    finish();

                }
            }, 500);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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

                case CAMERA_REQUEST_CODE:
                    try {
                        File destination = null;
                        String imgPath = null;

                        Uri selectedImageN = data.getData();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                        Log.e("Activity", "Pick from Camera::>>> ");

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        destination = new File(Environment.getExternalStorageDirectory() + "/" +
                                getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                        FileOutputStream fo;
                        try {
                            destination.createNewFile();
                            fo = new FileOutputStream(destination);
                            fo.write(bytes.toByteArray());
                            fo.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imgPath = destination.getAbsolutePath();
                        imgProduct.setVisibility(View.VISIBLE);
                        imgProduct.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
    }
}

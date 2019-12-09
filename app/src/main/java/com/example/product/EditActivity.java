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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
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

public class EditActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductModel product;

    private EditText etName, etDesc;
    private Button btnEdit, btnImagePicker;
    private ImageView imgProductEdit;

    private String product_id;
    public static final int GALLERY_REQUEST_CODE = 001;
    public static final int CAMERA_REQUEST_CODE = 002;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBProduct = new DatabaseHelper(this);
        product_id = getIntent().getExtras().getString("id_product");
        initialization();
        initGetEditProduct();

        btnImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initEditProduct();
            }
        });
    }

    private void initialization() {
        etName = findViewById(R.id.et_name_product_edit);
        etDesc = findViewById(R.id.et_desc_product_edit);
        btnEdit = findViewById(R.id.btn_edit_product);
        btnImagePicker = findViewById(R.id.btn_image_picker_edit);
        imgProductEdit = findViewById(R.id.img_product_edit);
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
                Toast.makeText(getApplicationContext(), "Permission error", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Permission error", Toast.LENGTH_LONG).show();

        }

    }

    private void convertBase64(){
        imgProductEdit.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgProductEdit.getDrawingCache();

        ConvertToBase64 tb = new ConvertToBase64();
        encodedImage = tb.bitmapToBase64(bitmap, "JPEG");
    }

    private void initGetEditProduct() {
        product = DBProduct.getProductById(product_id);

        etName.setText(product.getProduct_name());
        etDesc.setText(product.getProduct_desc());
        String sFoto = product.getProduct_image();

        try {
            byte[] imageByte = Base64.decode(sFoto, Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

            imgProductEdit.setImageBitmap(bm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEditProduct() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        imgProductEdit.setDrawingCacheEnabled(true);
        Bitmap bm = imgProductEdit.getDrawingCache();

        if (name.isEmpty()){
            etName.setError("nama tidak boleh kosong");

        } else if (desc.isEmpty()) {
            etDesc.setError("deksripsi tidak boleh kosong");

        }

        else if (bm == null){
            Toast.makeText(getApplicationContext(), "Upload foto terlebih dahulu", Toast.LENGTH_LONG).show();

        } else {
            convertBase64();

            product = new ProductModel();
            product.setProduct_id(product_id);
            product.setProduct_name(name);
            product.setProduct_desc(desc);
            product.setProduct_image(encodedImage);
            DBProduct.updateProduct(product);

            Toast.makeText(getApplicationContext(), "Product berhasil diedit", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent toHome = new Intent(getApplicationContext(), MainActivity.class);
                    toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(toHome);
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
                    imgProductEdit.setVisibility(View.VISIBLE);
                    imgProductEdit.setImageURI(selectedImage);
                    break;
            }
    }
}

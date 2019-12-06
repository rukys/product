package com.example.product;

import android.content.Intent;
import android.os.Bundle;

import com.example.product.adapter.ProductAdapter;
import com.example.product.database.DatabaseHelper;
import com.example.product.model.ProductModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper DBProduct;
    private ProductAdapter productAdapter;
    private List<ProductModel> productList = new ArrayList<>();

    private RecyclerView rvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Product");

        DBProduct = new DatabaseHelper(this);

        initialization();
        getListProduct();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toAddProduct = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(toAddProduct);
            }
        });
    }

    private void initialization() {
        rvMain = findViewById(R.id.rv_main);
    }

    private void getListProduct() {
        productList = DBProduct.getAllProduct();

        productAdapter = new ProductAdapter(productList);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(this);
        //((LinearLayoutManager) layoutmanager).setReverseLayout(true);
        //((LinearLayoutManager) layoutmanager).setStackFromEnd(true);
        //((LinearLayoutManager) layoutmanager).setSmoothScrollbarEnabled(true);
        rvMain.setLayoutManager(layoutmanager);
        rvMain.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        getListProduct();
        super.onResume();
    }
}

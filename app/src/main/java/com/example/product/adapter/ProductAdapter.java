package com.example.product.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.product.DetailActivity;
import com.example.product.R;
import com.example.product.model.ProductModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    List<ProductModel> productList;
    List<ProductModel> productSearchList;

    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
        productSearchList = new ArrayList<>(productList);
        notifyDataSetChanged();
    }

    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_list, parent, false);

        return new ProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductAdapter.MyViewHolder holder, int position) {
        final ProductModel data = productList.get(position);

        holder.id.setText(data.getProduct_id());
        holder.name.setText(data.getProduct_name());
        holder.desc.setText(data.getProduct_desc());

        byte[] imageByte = Base64.decode(data.getProduct_image(), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

        holder.ivCode.setImageBitmap(bm);

//        Glide.get(holder.itemView.getContext());
//        Glide.with(holder.itemView.getContext()).load(data.getProduct_image())
//                .thumbnail(0.25f)
//                .into(holder.ivCode);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = data.getProduct_id();

                Intent toDetail = new Intent(v.getContext(), DetailActivity.class);
                toDetail.putExtra("id_product", id);
                v.getContext().startActivity(toDetail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name, desc;
        ImageView ivCode;

        public MyViewHolder(View view) {
            super(view);
            id = itemView.findViewById(R.id.tv_row_id_product);
            name = itemView.findViewById(R.id.tv_row_name_product);
            desc = itemView.findViewById(R.id.tv_row_desc_product);
            ivCode = itemView.findViewById(R.id.img_row_product);

        }
    }

    public Filter getFilter() {
        return historyFilter;
    }

    private Filter historyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProductModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productSearchList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ProductModel item : productSearchList) {
                    if (String.valueOf(item.getProduct_id()).toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getProduct_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}

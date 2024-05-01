package com.example.marketplace.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.Activity.DetailActivity;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;


import java.util.List;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductGridViewHolder> {
    Context context;
    List<FlowerModel> flowersList;

    public ProductGridAdapter(Context context, List<FlowerModel> flowersList) {
        this.context = context;
        this.flowersList = flowersList;
    }

    @NonNull
    @Override
    public ProductGridAdapter.ProductGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_grid_item, parent, false);
        return new ProductGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductGridViewHolder holder, int position) {
        FlowerModel currentFlower = flowersList.get(position);

        Glide.with(context)
                .load(currentFlower.getPhoto())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.productImg.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });

        holder.titleTxt.setText(currentFlower.getTitle());
        holder.priceTxt.setText(String.format("%sԴ", currentFlower.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", currentFlower);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return flowersList != null ? flowersList.size() : 0;
    }

    public static class ProductGridViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        TextView priceTxt;
        ImageView productImg;

        public ProductGridViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            productImg = itemView.findViewById(R.id.productImg);
        }
    }
}

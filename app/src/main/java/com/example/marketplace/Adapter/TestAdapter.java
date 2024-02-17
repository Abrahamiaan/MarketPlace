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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.Activity.DetailActivity;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;


import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ProductViewHolder> {
    Context context;
    List<FlowerModel> flowersList;
    int layoutResId;

    public TestAdapter(Context context, List<FlowerModel> flowersList, int layoutResId) {
        this.context = context;
        this.flowersList = flowersList;
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public TestAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.test_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        FlowerModel currentFlower = flowersList.get(position);

        Glide.with(context)
                .load(currentFlower.getPhoto())
                .transform(new RoundedCorners(30))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.image.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        holder.title.setText(currentFlower.getTitle());
        holder.price.setText(String.format("%s AMD", currentFlower.getPrice()));

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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImg);
            title = itemView.findViewById(R.id.titleTxt);
            price = itemView.findViewById(R.id.priceTxt);
        }
    }
}

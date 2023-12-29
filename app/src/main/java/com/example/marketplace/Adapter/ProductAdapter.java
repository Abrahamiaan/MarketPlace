package com.example.marketplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<FlowerModel> flowersList;

    public ProductAdapter(Context context, List<FlowerModel> flowersList) {
        this.context = context;
        this.flowersList = flowersList;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prodcut_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        FlowerModel currentFlower = flowersList.get(position);

        Glide.with(context)
                .load(currentFlower.getPhoto())
                .into(holder.image);

        holder.title.setText(currentFlower.getTitle());
        int price = currentFlower.getPrice();
        holder.price.setText(price + " AMD");
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

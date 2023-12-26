package com.example.marketplace.Adapter;

import static com.example.marketplace.R.drawable.item1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.ImageUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<FlowerModel> flowersList;

    public ProductAdapter(Context context, List<FlowerModel> flowersList) {
        this.context = context;
        this.flowersList = flowersList;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prodcut_item, parent, false);

        return new ProductAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Bitmap resizedBitmap = ImageUtils.resizeBitmap(context.getApplicationContext(), flowersList.get(position).getImageUrl(), 100, 100);
        holder.image.setImageBitmap(resizedBitmap);
        holder.title.setText(flowersList.get(position).getTitle());
        int price = flowersList.get(position).getPrice();
        holder.price.setText(Integer.toString(price) + " AMD");
    }

    @Override
    public int getItemCount() {
        return flowersList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView price;
        TextView length;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.productImg);
            title = itemView.findViewById(R.id.titleTxt);
            price = itemView.findViewById(R.id.priceTxt);
        }
    }
}

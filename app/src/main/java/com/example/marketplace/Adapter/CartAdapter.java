package com.example.marketplace.Adapter;

import android.content.Context;
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
import com.example.marketplace.Fragment.CartFragment;
import com.example.marketplace.Model.CartModel;
import com.example.marketplace.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    List<CartModel> cartItems;
    CartFragment cartFragment;

    public CartAdapter(Context context, List<CartModel> cartItems, CartFragment cartFragment) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartFragment = cartFragment;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel item = cartItems.get(position);
        Glide.with(context)
                .load(item.getProductModel().getPhoto())
                .transform(new RoundedCorners(30))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        holder.photo.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        int price = item.getProductModel().getPrice();
        holder.itemPrice.setText(String.valueOf(price));
        holder.cartItemName.setText(item.getProductModel().getTitle());
        holder.totalPrice.setText(String.valueOf(price * item.getCount()));
        holder.countOfProduct.setText(String.valueOf(item.getCount()));

        holder.countPlus.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.countOfProduct.getText().toString()) + 1;
            int availableCount = item.getProductModel().getAvailableCount();

            if (newCount <= availableCount) {
                item.setCount(newCount);
                holder.countOfProduct.setText(String.valueOf(newCount));
                holder.totalPrice.setText(String.valueOf(price * newCount));
                cartFragment.updateTotalSum();
            }
        });

        holder.countMinus.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.countOfProduct.getText().toString()) - 1;
            if (newCount >= item.getProductModel().getMinimumPurchaseCount()) {
                item.setCount(newCount);
                holder.countOfProduct.setText(String.valueOf(newCount));
                holder.totalPrice.setText(String.valueOf(price * newCount));
                cartFragment.updateTotalSum();
            }
        });

        holder.removeBtn.setOnClickListener(v -> cartFragment.removeFromCart(position));
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemName;
        TextView itemPrice;
        TextView countPlus;
        TextView countMinus;
        TextView totalPrice;
        TextView countOfProduct;
        ImageView photo;
        ImageView removeBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemName = itemView.findViewById(R.id.titleTxt);
            countPlus = itemView.findViewById(R.id.countPlus);
            countMinus = itemView.findViewById(R.id.countMinus);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            countOfProduct = itemView.findViewById(R.id.count);
            photo = itemView.findViewById(R.id.photoProduct);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }
}

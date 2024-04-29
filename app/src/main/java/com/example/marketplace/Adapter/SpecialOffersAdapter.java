package com.example.marketplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Model.Category;
import com.example.marketplace.R;

import java.util.List;

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.SpecialOffersViewHolder> {
    Context context;
    List<Category> categoryList;

    public SpecialOffersAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public SpecialOffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.special_offers_item, parent, false);
        return new SpecialOffersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialOffersViewHolder holder, int position) {
        holder.specialImg.setImageResource(categoryList.get(position).getImageUrl());
        holder.textView.setText(categoryList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public  static class SpecialOffersViewHolder extends RecyclerView.ViewHolder{
        ImageView specialImg;
        TextView textView;

        public SpecialOffersViewHolder(@NonNull View itemView) {
            super(itemView);
            specialImg = itemView.findViewById(R.id.specialImg);
            textView = itemView.findViewById(R.id.specialText);
        }
    }
}

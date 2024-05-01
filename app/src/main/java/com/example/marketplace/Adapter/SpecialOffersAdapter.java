package com.example.marketplace.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Model.SpecialOffer;
import com.example.marketplace.R;

import java.util.List;

public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.SpecialOffersViewHolder> {
    Context context;
    List<SpecialOffer> categoryList;

    public SpecialOffersAdapter(Context context, List<SpecialOffer> categoryList) {
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
        SpecialOffer fkCategory = categoryList.get(position);
        holder.secondaryImg.setImageResource(fkCategory.getImgUrl());
        holder.specialText.setText(fkCategory.getTitle());
        holder.unionConstraint.setBackgroundResource(fkCategory.getUnionsUrl());
        holder.mainCard.setCardBackgroundColor(context.getColor(fkCategory.getBgColor()));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public  static class SpecialOffersViewHolder extends RecyclerView.ViewHolder {
        CardView mainCard;
        ConstraintLayout unionConstraint;
        ImageView secondaryImg;
        TextView specialText;

        public SpecialOffersViewHolder(@NonNull View itemView) {
            super(itemView);
            mainCard = itemView.findViewById(R.id.mainCard);
            unionConstraint = itemView.findViewById(R.id.unionConstraint);
            secondaryImg = itemView.findViewById(R.id.secondaryImg);
            specialText = itemView.findViewById(R.id.specialText);
        }
    }
}

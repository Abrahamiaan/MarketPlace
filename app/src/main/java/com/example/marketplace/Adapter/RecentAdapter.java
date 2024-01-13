package com.example.marketplace.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Activity.SearchResultActivity;
import com.example.marketplace.Fragment.SearchFragment;
import com.example.marketplace.R;
import com.example.marketplace.Utils.DataProvider;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    List<String> data;
    DataProvider provider;

    public RecentAdapter(List<String> data, DataProvider provider) {
        this.data = data;
        this.provider = provider;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.resent_item, parent, false);
        return new RecentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        holder.recentItemText.setText(data.get(position));
        holder.removeRecent.setOnClickListener(v -> {
            provider.removeSearchQuery(data.get(position));
            notifyDataSetChanged();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SearchResultActivity.class);
            intent.putExtra("query", data.get(position));
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<String> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class RecentViewHolder extends RecyclerView.ViewHolder {
        TextView recentItemText;
        ImageView removeRecent;

        RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            recentItemText = itemView.findViewById(R.id.textTv);
            removeRecent = itemView.findViewById(R.id.removeBtn);
        }
    }
}

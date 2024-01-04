package com.example.marketplace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.R;

import java.util.ArrayList;
import java.util.List;

public class ColorSizeAdapter extends RecyclerView.Adapter<ColorSizeAdapter.ColorViewHolder> {

    List<EditText> colorItems;
    static List<String> colors = new ArrayList<>();

    public ColorSizeAdapter(List<EditText> colorItems) {
        this.colorItems = colorItems;
    }

    public void addItem(EditText editText) {
        colorItems.add(editText);
        notifyItemInserted(colorItems.size() - 1);
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        String color = colorItems.get(position).getText().toString();
        colors.add(color);
        Toast.makeText(holder.itemView.getContext(), color, Toast.LENGTH_SHORT).show();
    }

    public List<String> getColorItems() {
        return colors;
    }

    @Override
    public int getItemCount() {
        return colorItems.size();
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        ColorViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.colorTextView);
        }
    }
}

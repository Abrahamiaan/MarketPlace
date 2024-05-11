package com.example.marketplace.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Admin.AssignedOrdersActivity;
import com.example.marketplace.Admin.OrdersActivity;
import com.example.marketplace.Model.OrderModel;
import com.example.marketplace.Model.ProductModel;
import com.example.marketplace.R;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersViewHolder>{
    Context context;
    List<OrderModel> orderItems;
    OrdersActivity ordersActivity;
    AssignedOrdersActivity assignedOrdersActivity;
    int mode;

    public OrderAdapter(Context context, List<OrderModel> orderItems, OrdersActivity activity) {
        this.context = context;
        this.orderItems = orderItems;
        this.ordersActivity = activity;
        mode = 1;
    }

    public OrderAdapter(Context context, List<OrderModel> orderItems,
                        AssignedOrdersActivity assignedOrdersActivity) {
        this.context = context;
        this.orderItems = orderItems;
        this.assignedOrdersActivity = assignedOrdersActivity;
        mode = 2;
    }

    @NonNull
    @Override
    public OrderAdapter.OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderAdapter.OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrdersViewHolder holder, int position) {
        OrderModel item = orderItems.get(position);
        ProductModel productModel = orderItems.get(position).getProduct().getProductModel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, HH:mm", Locale.getDefault());
        String orderedAt = dateFormat.format(item.getOrderDate());
        holder.assignStatus.setText(item.getStatus());


        holder.orderedAt.setText(orderedAt);
        holder.shortName.setText(productModel.getTitle());
        String from = extractAddress(productModel.getLongitude(), productModel.getLatitude());
        String to = extractAddress(item.getLongitude(), item.getLatitude());
        if (from != null && to != null) {
            holder.fromTxt.setText(from);
            holder.toTxt.setText(to);
        }
        if (mode == 1) {
            holder.assignStatus.setOnClickListener(v -> {
                if (item.getAssignedDriverId() == null ||
                        item.getAssignedDriverId().isEmpty()) {
                    initListener(holder, item);
                }
            });
        } else if (mode == 2) {
            holder.assignStatus.setOnClickListener(v -> showPopup(v, position));
        }
    }

    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.driver_status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.delivered) {
                assignedOrdersActivity.updateOrder(position, "Delivered");
                return true;
            } else if (itemId == R.id.onTheWay) {
                assignedOrdersActivity.updateOrder(position, "On The Way");
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void initListener(OrderAdapter.OrdersViewHolder holder, OrderModel orderModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ordersActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_driver_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Select Driver");

        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group);

        AlertDialog dialog = builder.create();
        dialog.show();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.button1) {
                holder.assignStatus.setText(context.getString(R.string.assigned));
                ordersActivity.updateOrder(orderModel, "aXhCss3DSogAOwy59GKQKC2HrF73");
            } else if (checkedId == R.id.button2) {
                holder.assignStatus.setText(context.getString(R.string.assigned));
                ordersActivity.updateOrder(orderModel, "nrM0ll8Q4zYvVuaW54UXqbw1KI02");
            } else if (checkedId == R.id.button3) {
                holder.assignStatus.setText(context.getString(R.string.assigned));
                ordersActivity.updateOrder(orderModel, "xB0ztOZ3vFQZANd3kct8yq6BeA53");
            }
            dialog.dismiss();
        });
    }

    private String extractAddress(double longitude, double latitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.e("Order Adapter", e.getMessage());
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView shortName;
        TextView fromTxt;
        TextView toTxt;
        TextView orderedAt;
        AppCompatButton assignStatus;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            fromTxt = itemView.findViewById(R.id.fromText);
            shortName = itemView.findViewById(R.id.shortName);
            toTxt = itemView.findViewById(R.id.toText);
            orderedAt = itemView.findViewById(R.id.orderedAt);
            assignStatus = itemView.findViewById(R.id.orderStatus);
        }
    }
}

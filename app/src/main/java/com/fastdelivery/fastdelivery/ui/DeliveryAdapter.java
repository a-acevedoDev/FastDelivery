package com.fastdelivery.fastdelivery.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fastdelivery.fastdelivery.R;
import com.fastdelivery.fastdelivery.model.Delivery;
import com.fastdelivery.fastdelivery.model.DeliveryStatus;

import java.util.List;
import java.util.Locale;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    private List<Delivery> deliveryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Delivery delivery);
    }

    public DeliveryAdapter(List<Delivery> deliveryList, OnItemClickListener listener) {
        this.deliveryList = deliveryList;
        this.listener = listener;
    }

    public void updateList(List<Delivery> newList) {
        this.deliveryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Delivery delivery = deliveryList.get(position);
        holder.bind(delivery, listener);
    }

    @Override
    public int getItemCount() {
        return deliveryList != null ? deliveryList.size() : 0;
    }

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvAddress, tvAmount, tvPhono, tvStatus;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPhono = itemView.findViewById(R.id.tvPhono);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Delivery delivery, OnItemClickListener listener) {
            tvClientName.setText(delivery.getClientName());
            tvAddress.setText("Dir: " + delivery.getAddress());
            tvAmount.setText(String.format(Locale.getDefault(), "Monto: $%.2f", delivery.getAmountToPay()));
            tvPhono.setText(delivery.getPhono());

            DeliveryStatus status = delivery.getDeliveryStatusEnum();
            if (status != null) {
                tvStatus.setText(status.name());
                switch (status) {
                    case ENTREGADO:
                        tvStatus.setBackgroundColor(Color.parseColor("#DCFCE7"));
                        tvStatus.setTextColor(Color.parseColor("#15803D"));
                        break;
                    case PENDIENTE:
                        tvStatus.setBackgroundColor(Color.parseColor("#FEF3C7"));
                        tvStatus.setTextColor(Color.parseColor("#B45309"));
                        break;
                    case CANCELADO:
                        tvStatus.setBackgroundColor(Color.parseColor("#FEE2E2"));
                        tvStatus.setTextColor(Color.parseColor("#B91C1C"));
                        break;
                }
            }

            itemView.setOnClickListener(v -> listener.onItemClick(delivery));
        }
    }
}

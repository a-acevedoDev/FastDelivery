package com.example.fastdelivery.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastdelivery.R;
import com.example.fastdelivery.database.AppDatabase;
import com.example.fastdelivery.model.Delivery;
import com.example.fastdelivery.model.DeliveryStatus;

import java.util.Locale;

public class DeliveryDetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvClientNameDetail, tvPhonoDetail, tvAddressDetail,
            tvAmountDetail, tvStatusDetail, tvObservationDetail;
    private Button btnMarkDelivered, btnMarkCanceled, btnDeleteDelivery;

    private long deliveryId = -1;
    private Delivery currentDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detail);

        if (getIntent() != null && getIntent().hasExtra("DELIVERY_ID")) {
            deliveryId = getIntent().getLongExtra("DELIVERY_ID", -1);
        }

        if (deliveryId == -1) {
            Toast.makeText(this, "Error al cargar la entrega", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvClientNameDetail = findViewById(R.id.tvClientNameDetail);
        tvPhonoDetail = findViewById(R.id.tvPhonoDetail);
        tvAddressDetail = findViewById(R.id.tvAddressDetail);
        tvAmountDetail = findViewById(R.id.tvAmountDetail);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);
        tvObservationDetail = findViewById(R.id.tvObservationDetail);

        btnMarkDelivered = findViewById(R.id.btnMarkDelivered);
        btnMarkCanceled = findViewById(R.id.btnMarkCanceled);
        btnDeleteDelivery = findViewById(R.id.btnDeleteDelivery);

        loadDeliveryData();

        btnMarkDelivered.setOnClickListener(v -> updateStatus(DeliveryStatus.ENTREGADO));
        btnMarkCanceled.setOnClickListener(v -> updateStatus(DeliveryStatus.CANCELADO));
        btnDeleteDelivery.setOnClickListener(v -> deleteDelivery());
    }

    private void loadDeliveryData() {
        currentDelivery = AppDatabase.getInstance(this).deliveryDao().getDeliveryById(deliveryId);

        if (currentDelivery != null) {
            tvDetailTitle.setText("Detalle del Reparto #" + currentDelivery.getId());
            tvClientNameDetail.setText("Cliente: " + currentDelivery.getClientName());
            tvPhonoDetail.setText("Teléfono: " + currentDelivery.getPhono());
            tvAddressDetail.setText("Dirección: " + currentDelivery.getAddress());
            tvAmountDetail.setText(String.format(Locale.getDefault(), "Monto a cobrar: $%.2f", currentDelivery.getAmountToPay()));

            DeliveryStatus status = currentDelivery.getDeliveryStatusEnum();
            tvStatusDetail.setText("Estado Actual: " + (status != null ? status.name() : "PENDIENTE"));
            tvObservationDetail.setText("Notas: " + (currentDelivery.getObservation() != null ? currentDelivery.getObservation() : "Sin observaciones"));
        }
    }

    private void updateStatus(DeliveryStatus newStatus) {
        if (currentDelivery != null) {
            AppDatabase.getInstance(this).deliveryDao().updateStatus(deliveryId, newStatus.name());
            Toast.makeText(this, "Estado actualizado a " + newStatus.name(), Toast.LENGTH_SHORT).show();
            loadDeliveryData();
        }
    }

    private void deleteDelivery() {
        if (currentDelivery != null) {
            AppDatabase.getInstance(this).deliveryDao().delete(currentDelivery);
            Toast.makeText(this, "Registro eliminado de SQLite", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
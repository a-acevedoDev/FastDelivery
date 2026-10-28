package com.example.fastdelivery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastdelivery.R;
import com.example.fastdelivery.database.AppDatabase;
import com.example.fastdelivery.model.Delivery;
import com.example.fastdelivery.model.DeliveryStatus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDeliveries;
    private DeliveryAdapter adapter;
    private Button btnFilterAll, btnFilterPending, btnFilterDelivered, btnOpenMap, btnAddDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvDeliveries = findViewById(R.id.rvDeliveries);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterPending = findViewById(R.id.btnFilterPending);
        btnFilterDelivered = findViewById(R.id.btnFilterDelivered);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        btnAddDelivery = findViewById(R.id.btnAddDelivery);

        rvDeliveries.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryAdapter(new ArrayList<>(), delivery -> {
            Intent intent = new Intent(MainActivity.this, DeliveryDetailActivity.class);
            intent.putExtra("DELIVERY_ID", delivery.getId());
            startActivity(intent);
        });
        rvDeliveries.setAdapter(adapter);

        btnFilterAll.setOnClickListener(v -> loadDeliveries(null));
        btnFilterPending.setOnClickListener(v -> loadDeliveries(DeliveryStatus.PENDIENTE));
        btnFilterDelivered.setOnClickListener(v -> loadDeliveries(DeliveryStatus.ENTREGADO));

        btnOpenMap.setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        btnAddDelivery.setOnClickListener(v -> startActivity(new Intent(this, DeliveryRegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeliveries(null);
    }

    private void loadDeliveries(DeliveryStatus statusFilter) {
        List<Delivery> list;
        if (statusFilter == null) {
            list = AppDatabase.getInstance(this).deliveryDao().getAllDeliveries();
        } else {
            list = AppDatabase.getInstance(this).deliveryDao().getDeliveriesByStatus(statusFilter.name());
        }
        adapter.updateList(list);
    }
}
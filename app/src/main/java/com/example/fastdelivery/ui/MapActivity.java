package com.example.fastdelivery.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fastdelivery.R;
import com.example.fastdelivery.database.AppDatabase;
import com.example.fastdelivery.model.Delivery;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.5);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        fabMyLocation.setOnClickListener(v -> {
            if (locationOverlay.getMyLocation() != null) {
                mapView.getController().animateTo(locationOverlay.getMyLocation());
                mapView.getController().setZoom(16.0);
            } else {
                Toast.makeText(this, "Obteniendo señal GPS...", Toast.LENGTH_SHORT).show();
            }
        });

        loadDeliveriesOnMap();
    }

    private void loadDeliveriesOnMap() {
        List<Delivery> deliveries = AppDatabase.getInstance(this).deliveryDao().getAllDeliveries();

        if (deliveries.isEmpty()) {
            Toast.makeText(this, "No hay entregas registradas para mostrar en el mapa", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint lastPoint = null;

        for (Delivery delivery : deliveries) {
            if (delivery.getLatitude() != 0.0 && delivery.getLongitude() != 0.0) {
                GeoPoint point = new GeoPoint(delivery.getLatitude(), delivery.getLongitude());
                lastPoint = point;

                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(delivery.getClientName() + " (" + delivery.getDeliveryStatus() + ")");
                marker.setSnippet("Dir: " + delivery.getAddress() + "\nMonto: $" + delivery.getAmountToPay());

                mapView.getOverlays().add(marker);
            }
        }

        if (lastPoint != null) {
            mapView.getController().setCenter(lastPoint);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                break;
            }
        }
    }
}
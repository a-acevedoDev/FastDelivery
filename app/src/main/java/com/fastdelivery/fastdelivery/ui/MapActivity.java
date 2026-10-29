package com.fastdelivery.fastdelivery.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fastdelivery.fastdelivery.database.AppDatabase;
import com.fastdelivery.fastdelivery.model.Delivery;
import com.fastdelivery.fastdelivery.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class MapActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue("com.fastdelivery.fastdelivery/1.0 (Android)");
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);

        mapView.setTileSource(new org.osmdroid.tileprovider.tilesource.XYTileSource(
                "OpenStreetMap",
                0, 19, 256, ".png",
                new String[]{
                        "https://tile.openstreetmap.org/"
                }
        ));

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        GeoPoint santiagoChile = new GeoPoint(-33.4489, -70.6693);
        mapView.getController().setCenter(santiagoChile);

        checkAndRequestPermissions();

        setupMyLocationOverlay();

        fabMyLocation.setOnClickListener(v -> {
            if (locationOverlay != null && locationOverlay.getMyLocation() != null) {
                mapView.getController().animateTo(locationOverlay.getMyLocation());
                mapView.getController().setZoom(16.5);
            } else {
                Toast.makeText(this, "Obteniendo señal GPS...", Toast.LENGTH_SHORT).show();
            }
        });

        loadDeliveriesFromDatabase();
    }

    private void setupMyLocationOverlay() {
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
    }

    private void loadDeliveriesFromDatabase() {
        List<Delivery> deliveries = AppDatabase.getInstance(this).deliveryDao().getAllDeliveries();

        if (deliveries == null || deliveries.isEmpty()) {
            Toast.makeText(this, "No hay entregas registradas para mostrar", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint lastValidPoint = null;

        for (Delivery delivery : deliveries) {
            if (delivery.getLatitude() != 0.0 && delivery.getLongitude() != 0.0) {
                GeoPoint point = new GeoPoint(delivery.getLatitude(), delivery.getLongitude());
                lastValidPoint = point;

                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                String estado = delivery.getDeliveryStatus() != null ? delivery.getDeliveryStatus() : "PENDIENTE";
                marker.setTitle(delivery.getClientName() + " (" + estado + ")");
                marker.setSnippet("Dir: " + delivery.getAddress() + "\nMonto: $" + delivery.getAmountToPay());

                mapView.getOverlays().add(marker);
            }
        }

        if (lastValidPoint != null) {
            mapView.getController().setCenter(lastValidPoint);
        } else {
            mapView.getController().setCenter(new GeoPoint(-33.4489, -70.6693));
        }
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationOverlay != null) {
                    locationOverlay.enableMyLocation();
                }
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (locationOverlay != null) {
            locationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
        }
    }
}
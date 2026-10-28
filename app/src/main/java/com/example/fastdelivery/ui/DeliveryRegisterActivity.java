package com.example.fastdelivery.ui;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastdelivery.R;
import com.example.fastdelivery.database.AppDatabase;
import com.example.fastdelivery.model.Delivery;
import com.example.fastdelivery.model.DeliveryStatus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DeliveryRegisterActivity extends AppCompatActivity {

    private EditText etClientName, etPhono, etAddress, etAmountToPay, etObservation;
    private TextView tvCoordinates;
    private Button btnLocateAddress, btnSaveDelivery;

    private double calculatedLatitude = 0.0;
    private double calculatedLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delivery_register);

        etClientName = findViewById(R.id.etClientName);
        etPhono = findViewById(R.id.etPhono);
        etAddress = findViewById(R.id.etAddress);
        etAmountToPay = findViewById(R.id.etAmountToPay);
        etObservation = findViewById(R.id.etObservation);
        tvCoordinates = findViewById(R.id.tvCoordinates);

        btnLocateAddress = findViewById(R.id.btnLocateAddress);
        btnSaveDelivery = findViewById(R.id.btnSaveDelivery);
        btnLocateAddress.setOnClickListener(v -> locateAddressOnMap());
        btnSaveDelivery.setOnClickListener(v -> saveDeliveryToDatabase());
    }

    private void locateAddressOnMap() {
        String addressText = etAddress.getText().toString().trim();

        if (addressText.isEmpty()) {
            etAddress.setError("Ingrese una dirección válida");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressText, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                calculatedLatitude = address.getLatitude();
                calculatedLongitude = address.getLongitude();

                tvCoordinates.setText(String.format(Locale.getDefault(),
                        "Coordenadas: Lat %.5f, Lng %.5f", calculatedLatitude, calculatedLongitude));
                Toast.makeText(this, "¡Ubicación encontrada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontraron coordenadas para esa dirección", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al buscar coordenadas (Verifique conexión de red)", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDeliveryToDatabase() {
        String clientName = etClientName.getText().toString().trim();
        String phono = etPhono.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String amountStr = etAmountToPay.getText().toString().trim();
        String observation = etObservation.getText().toString().trim();

        if (clientName.isEmpty() || address.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios (Cliente, Dirección, Monto)", Toast.LENGTH_SHORT).show();
            return;
        }

        double amountToPay = Double.parseDouble(amountStr);

        Delivery delivery = new Delivery();
        delivery.setClientName(clientName);
        delivery.setPhono(phono);
        delivery.setAddress(address);
        delivery.setLatitude(calculatedLatitude);
        delivery.setLongitude(calculatedLongitude);
        delivery.setAmountToPay(amountToPay);
        delivery.setDeliveryStatus(DeliveryStatus.PENDIENTE);
        delivery.setObservation(observation);

        AppDatabase.getInstance(this).deliveryDao().insert(delivery);

        Toast.makeText(this, "Entrega registrada exitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
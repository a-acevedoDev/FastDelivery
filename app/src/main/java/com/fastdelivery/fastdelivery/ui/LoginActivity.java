package com.fastdelivery.fastdelivery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.fastdelivery.fastdelivery.R;
import com.fastdelivery.fastdelivery.utils.BiometricHelper;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private Button btnAuthBiometric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnAuthBiometric = findViewById(R.id.btnAuthBiometric);
        btnAuthBiometric.setOnClickListener(v -> checkAndAuthenticate());
        checkAndAuthenticate();
    }

    private void checkAndAuthenticate() {
        if (BiometricHelper.isBiometricAvailable(this)) {
            showBiometricPrompt();
        } else {
            Toast.makeText(this, "Biometría no disponible en este dispositivo", Toast.LENGTH_LONG).show();
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(LoginActivity.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(LoginActivity.this, "¡Autenticación exitosa!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(LoginActivity.this, "Huella no reconocida", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acceso a FastDelivery")
                .setSubtitle("Confirme su identidad con su huella dactilar")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
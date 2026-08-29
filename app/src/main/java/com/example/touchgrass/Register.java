package com.example.touchgrass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.touchgrass.activity.MainActivity;
import com.example.touchgrass.data.AuthService;
import com.example.touchgrass.data.FirestoreService;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText phoneInput;
    private EditText locationInput;
    private Button registerButton;
    private TextView loginToggleText;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        phoneInput = findViewById(R.id.phoneInput);
        locationInput = findViewById(R.id.locationInput);
        registerButton = findViewById(R.id.registerButton);
        loginToggleText = findViewById(R.id.loginToggleText);
        errorText = findViewById(R.id.errorText);

        registerButton.setOnClickListener(v -> onSubmit());
        loginToggleText.setOnClickListener(v -> finish());
    }

    private void onSubmit() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String displayName = usernameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(displayName)) {
            errorText.setText(R.string.auth_error_missing_fields);
            return;
        }

        setFormEnabled(false);
        authService.signUp(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                firestoreService.createUserProfile(user.getUid(), displayName, phone, location, new FirestoreService.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        goToHome();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        setFormEnabled(true);
                        errorText.setText(getString(R.string.auth_error_profile_failed, e.getMessage()));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                setFormEnabled(true);
                errorText.setText(e.getMessage());
            }
        });
    }

    private void setFormEnabled(boolean enabled) {
        registerButton.setEnabled(enabled);
        emailInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);
        usernameInput.setEnabled(enabled);
        phoneInput.setEnabled(enabled);
        locationInput.setEnabled(enabled);
    }

    private void goToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
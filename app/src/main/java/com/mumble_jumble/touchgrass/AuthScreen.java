package com.mumble_jumble.touchgrass;

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

import com.mumble_jumble.touchgrass.data.AuthService;
import com.google.firebase.auth.FirebaseUser;

public class AuthScreen extends AppCompatActivity {

    private final AuthService authService = new AuthService();

    private EditText emailInput;
    private EditText passwordInput;
    private Button submitButton;
    private TextView modeToggleText;
    private TextView forgotPasswordText;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        submitButton = findViewById(R.id.submitButton);
        modeToggleText = findViewById(R.id.modeToggleText);
        forgotPasswordText = findViewById(R.id.tvForgotPassword);
        errorText = findViewById(R.id.errorText);

        submitButton.setOnClickListener(v -> onSubmit());
        modeToggleText.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));
        forgotPasswordText.setOnClickListener(v -> onForgotPassword());
    }

    private void onForgotPassword() {
        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            errorText.setText(R.string.auth_error_missing_email);
            return;
        }

        authService.sendPasswordResetEmail(email, new AuthService.ResetPasswordCallback() {
            @Override
            public void onSuccess() {
                errorText.setText(R.string.auth_reset_email_sent);
            }

            @Override
            public void onFailure(Exception e) {
                errorText.setText(e.getMessage());
            }
        });
    }

    private void onSubmit() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            errorText.setText(R.string.auth_error_missing_fields);
            return;
        }

        setFormEnabled(false);
        authService.signIn(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                goToHome();
            }

            @Override
            public void onFailure(Exception e) {
                setFormEnabled(true);
                errorText.setText(e.getMessage());
            }
        });
    }

    private void setFormEnabled(boolean enabled) {
        submitButton.setEnabled(enabled);
        emailInput.setEnabled(enabled);
        passwordInput.setEnabled(enabled);
    }

    private void goToHome() {
        startActivity(new Intent(this, Homepage.class));
        finish();
    }
}
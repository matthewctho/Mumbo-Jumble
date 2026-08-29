package com.example.touchgrass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.touchgrass.data.AuthService;
import com.example.touchgrass.data.FirestoreService;
import com.google.firebase.auth.FirebaseUser;

public class AuthScreen extends AppCompatActivity {

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    private boolean signUpMode = false;

    private EditText displayNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button submitButton;
    private TextView modeToggleText;
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

        displayNameInput = findViewById(R.id.displayNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        submitButton = findViewById(R.id.submitButton);
        modeToggleText = findViewById(R.id.modeToggleText);
        errorText = findViewById(R.id.errorText);

        submitButton.setOnClickListener(v -> onSubmit());
        modeToggleText.setOnClickListener(v -> setSignUpMode(!signUpMode));

        setSignUpMode(false);
    }

    private void setSignUpMode(boolean signUp) {
        signUpMode = signUp;
        displayNameInput.setVisibility(signUp ? View.VISIBLE : View.GONE);
        submitButton.setText(signUp ? R.string.auth_sign_up : R.string.auth_sign_in);
        modeToggleText.setText(signUp ? R.string.auth_switch_to_sign_in : R.string.auth_switch_to_sign_up);
        errorText.setText("");
    }

    private void onSubmit() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String displayName = displayNameInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            errorText.setText(R.string.auth_error_missing_fields);
            return;
        }
        if (signUpMode && TextUtils.isEmpty(displayName)) {
            errorText.setText(R.string.auth_error_missing_fields);
            return;
        }

        setFormEnabled(false);
        if (signUpMode) {
            signUp(email, password, displayName);
        } else {
            signIn(email, password);
        }
    }

    private void signUp(String email, String password, String displayName) {
        authService.signUp(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                firestoreService.createUserProfile(user.getUid(), displayName, new FirestoreService.WriteCallback() {
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

    private void signIn(String email, String password) {
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
        displayNameInput.setEnabled(enabled);
    }

    private void goToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
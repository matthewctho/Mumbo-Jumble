package com.example.touchgrass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.touchgrass.data.AuthService;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, AuthScreen.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView signedInAsText = findViewById(R.id.signedInAsText);
        signedInAsText.setText(getString(R.string.home_signed_in_as, currentUser.getEmail()));

        findViewById(R.id.signOutButton).setOnClickListener(v -> {
            authService.signOut();
            startActivity(new Intent(this, AuthScreen.class));
            finish();
        });
    }
}
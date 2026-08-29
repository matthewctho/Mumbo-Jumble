package com.mumble_jumble.touchgrass.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mumble_jumble.touchgrass.AuthScreen;
import com.mumble_jumble.touchgrass.Homepage;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final AuthService authService = new AuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = authService.getCurrentUser();
        Class<?> destination = currentUser == null ? AuthScreen.class : Homepage.class;
        startActivity(new Intent(this, destination));
        finish();
    }
}

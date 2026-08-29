package com.mumble_jumble.touchgrass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mumble_jumble.touchgrass.activity.PackListActivity;
import com.mumble_jumble.touchgrass.data.AuthService;
import com.mumble_jumble.touchgrass.data.FirestoreService;
import com.mumble_jumble.touchgrass.models.User;
import com.google.firebase.auth.FirebaseUser;

public class Homepage extends AppCompatActivity {

    private final AuthService authService = new AuthService();
    private final FirestoreService firestoreService = new FirestoreService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView usernameText = findViewById(R.id.usernameText);
        TextView pointsText = findViewById(R.id.pointsText);

        FirebaseUser currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            firestoreService.getUserProfile(currentUser.getUid(), new FirestoreService.UserProfileCallback() {
                @Override
                public void onSuccess(User user) {
                    usernameText.setText(user.displayName);
                    pointsText.setText(user.points + " points");
                }

                @Override
                public void onFailure(Exception e) {
                    // leave placeholder header text, non-fatal
                }
            });
        }

        findViewById(R.id.profileImage).setOnClickListener(v -> {
            authService.signOut();
            startActivity(new Intent(this, AuthScreen.class));
            finish();
        });

        findViewById(R.id.hikingChallenge).setOnClickListener(v -> startActivity(new Intent(this, PackListActivity.class)));
        findViewById(R.id.basketballChallenge).setOnClickListener(v -> startActivity(new Intent(this, PackListActivity.class)));
        findViewById(R.id.photoWalksChallenge).setOnClickListener(v -> startActivity(new Intent(this, PackListActivity.class)));
    }
}
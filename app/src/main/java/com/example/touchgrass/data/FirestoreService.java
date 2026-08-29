package com.example.touchgrass.data;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreService {

    public interface WriteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void createUserProfile(String uid, String displayName, String phone, String location, WriteCallback callback) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("displayName", displayName);
        profile.put("phone", phone);
        profile.put("location", location);
        profile.put("points", 0);
        profile.put("activePacks", new ArrayList<String>());
        profile.put("pointsHidden", false);

        db.collection("users").document(uid)
                .set(profile)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}

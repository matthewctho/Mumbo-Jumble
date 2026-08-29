package com.mumble_jumble.touchgrass.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void signUp(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(callback::onFailure);
    }

    public void signIn(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(callback::onFailure);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
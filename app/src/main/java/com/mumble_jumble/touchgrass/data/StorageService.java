package com.mumble_jumble.touchgrass.data;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageService {

    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    /**
     * Uploads a photo's raw JPEG bytes to Storage under
     * submissions/{uid}/{taskId}_{timestamp}.jpg and returns the public
     * download URL via the callback.
     */
    public void uploadSubmissionPhoto(String uid, String taskId, byte[] imageBytes, UploadCallback callback) {
        String path = "submissions/" + uid + "/" + taskId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = storage.getReference().child(path);

        ref.putBytes(imageBytes)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener((Uri uri) -> callback.onSuccess(uri.toString()))
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }
}

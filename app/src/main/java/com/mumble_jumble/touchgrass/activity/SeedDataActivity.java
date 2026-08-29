package com.mumble_jumble.touchgrass.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.models.ChallengePack;
import com.mumble_jumble.touchgrass.models.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.Arrays;
import java.util.List;

public class SeedDataActivity extends AppCompatActivity {

    private static final String TAG = "SeedDataActivity";

    // A small container so pack + its tasks travel together as one unit,
    // instead of 3 separate near-identical method calls.
    private static class PackSeed {
        ChallengePack pack;
        List<Task> tasks;

        PackSeed(ChallengePack pack, List<Task> tasks) {
            this.pack = pack;
            this.tasks = tasks;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_data); // just needs one button, see layout file

        Button btnSeed = findViewById(R.id.btnSeed);
        btnSeed.setOnClickListener(v -> checkIfAlreadySeededThenRun());
    }

    private void checkIfAlreadySeededThenRun() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("challengePacks")
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        Toast.makeText(this,
                                "challengePacks already has data — skipping to avoid duplicates.",
                                Toast.LENGTH_LONG).show();
                        Log.w(TAG, "Seeding skipped: challengePacks is not empty.");
                        return;
                    }
                    seedAllPacks(db);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to check existing data", e));
    }

    private void seedAllPacks(FirebaseFirestore db) {
        List<PackSeed> allSeeds = Arrays.asList(
                new PackSeed(
                        new ChallengePack("Trail Blazer", "hiking",
                                "Summit photos, wildlife spots, and mutual connects on the trail."),
                        Arrays.asList(
                                new Task("Summit/lookout photo", "scenery_photo", 5, null),
                                new Task("Plant/wildlife photo", "scenery_photo", 3, null),
                                new Task("Mutual connect with another hiker", "mutual_connect", 5, null),
                                new Task("Water feature photo", "scenery_photo", 4, null)
                        )
                ),
                new PackSeed(
                        new ChallengePack("Game Day", "basketball",
                                "Court photos, gear shots, and mutual connects at pickup games."),
                        Arrays.asList(
                                new Task("Mutual connect at a game", "mutual_connect", 5, null),
                                new Task("Court/venue photo", "scenery_photo", 2, null),
                                new Task("Gear photo", "scenery_photo", 2, null),
                                new Task("Mutual connect with someone who played that week", "mutual_connect", 5, null)
                        )
                ),
                new PackSeed(
                        new ChallengePack("Photo Walk", "photography",
                                "Street shots, nature finds, and mutual connects out on a walk."),
                        Arrays.asList(
                                new Task("Street/architecture photo", "scenery_photo", 3, null),
                                new Task("Nature or park photo", "scenery_photo", 3, null),
                                new Task("Mutual connect with another walker", "mutual_connect", 5, null),
                                new Task("Favorite \"found\" detail photo (texture, shadow, sign, etc.)", "scenery_photo", 4, null)
                        )
                )
        );

        // Firestore needs each pack's real document ID before its tasks can be written
        // (since each task needs packId). Pre-generate IDs with .document() so we can
        // build one single batch instead of chaining nested success callbacks.
        WriteBatch batch = db.batch();

        for (PackSeed seed : allSeeds) {
            DocumentReference packRef = db.collection("challengePacks").document(); // generates ID, doesn't write yet
            batch.set(packRef, seed.pack);

            for (Task task : seed.tasks) {
                task.packId = packRef.getId();
                DocumentReference taskRef = db.collection("tasks").document();
                batch.set(taskRef, task);
            }
        }

        batch.commit()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Seeding complete — check Firestore Console", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Batch seed successful: 3 packs, 12 tasks.");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Seeding failed — check Logcat", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Batch seed failed", e);
                });
    }
}
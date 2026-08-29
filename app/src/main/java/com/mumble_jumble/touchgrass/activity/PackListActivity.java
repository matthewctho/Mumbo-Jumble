package com.mumble_jumble.touchgrass.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.adapters.PackAdapter;
import com.mumble_jumble.touchgrass.data.GeminiVerificationService;
import com.mumble_jumble.touchgrass.models.ChallengePack;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PackListActivity extends AppCompatActivity {

    private static final String TAG = "PackListActivity";
    private PackAdapter adapter;
    private List<ChallengePack> packs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerPacks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PackAdapter(packs);
        recyclerView.setAdapter(adapter);

        loadPacksFromFirestore();

    }

    private void loadPacksFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("challengePacks")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    packs.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ChallengePack pack = doc.toObject(ChallengePack.class);
                        pack.challengeId = doc.getId();
                        packs.add(pack);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading packs", e));
    }
}
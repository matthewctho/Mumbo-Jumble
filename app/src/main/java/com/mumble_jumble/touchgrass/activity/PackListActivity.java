package com.mumble_jumble.touchgrass.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.adapters.PackAdapter;
import com.mumble_jumble.touchgrass.models.ChallengePack;

import java.util.ArrayList;
import java.util.List;

public class PackListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_list);

        // Hardcoded seed data for now — matches your data model
        List<ChallengePack> packs = new ArrayList<>();
        packs.add(new ChallengePack("Vagabond", "hiking",
                "Summit photos, wildlife spots, and mutual connects on the trail."));
        packs.add(new ChallengePack("I'd be ballin", "basketball",
                "Court photos, gear shots, and mutual connects at pickup games."));
        packs.add(new ChallengePack("", "cultural",
                "Public art, gallery visits, and your own creative sketches."));

        RecyclerView recyclerView = findViewById(R.id.recyclerPacks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PackAdapter(packs));
    }
}

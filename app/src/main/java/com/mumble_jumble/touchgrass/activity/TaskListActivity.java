package com.mumble_jumble.touchgrass.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.adapters.TaskAdapter;
import com.mumble_jumble.touchgrass.models.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG = "TaskListActivity";
    private TaskAdapter adapter;
    private List<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks);
        recyclerView.setAdapter(adapter);

        String packId = getIntent().getStringExtra("packId");
        String packName = getIntent().getStringExtra("packName");

        if (packName != null) {
            setTitle(packName); // shows pack name in the top app bar, e.g. "Trail Blazer"
        }

        if (packId == null) {
            Log.e(TAG, "No packId received — check the Intent extras from PackListActivity.");
            return;
        }

        loadTasksFromFirestore(packId);
    }

    private void loadTasksFromFirestore(String packId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .whereEqualTo("packId", packId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tasks.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Task task = doc.toObject(Task.class);
                        tasks.add(task);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading tasks", e));
    }
}
package com.mumble_jumble.touchgrass.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.adapters.TaskAdapter;
import com.mumble_jumble.touchgrass.models.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Hardcoded Trail Blazer tasks for now — matches your data model
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Summit/lookout photo", "scenery_photo", 5));
        tasks.add(new Task("Plant/wildlife photo", "scenery_photo", 3));
        tasks.add(new Task("Mutual connect with another hiker", "mutual_connect", 5));
        tasks.add(new Task("Water feature photo", "scenery_photo", 4));

        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(tasks));
    }
}
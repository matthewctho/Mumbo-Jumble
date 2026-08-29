package com.mumble_jumble.touchgrass.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mumble_jumble.touchgrass.R;
import com.mumble_jumble.touchgrass.models.ChallengePack;

import java.util.List;

public class PackAdapter extends RecyclerView.Adapter<PackAdapter.PackViewHolder> {

    public interface OnPackClickListener {
        void onPackClick(ChallengePack pack);
    }

    private List<ChallengePack> packList;
    private final OnPackClickListener listener;

    public PackAdapter(List<ChallengePack> packList, OnPackClickListener listener) {
        this.packList = packList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pack, parent, false);
        return new PackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackViewHolder holder, int position) {
        ChallengePack pack = packList.get(position);
        holder.txtPackName.setText(pack.name);
        holder.txtPackDescription.setText(pack.description);
        holder.itemView.setOnClickListener(v -> listener.onPackClick(pack));
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    static class PackViewHolder extends RecyclerView.ViewHolder {
        TextView txtPackName;
        TextView txtPackDescription;

        public PackViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPackName = itemView.findViewById(R.id.txtPackName);
            txtPackDescription = itemView.findViewById(R.id.txtPackDescription);
        }
    }
}
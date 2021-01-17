package com.example.csd_eindopdracht.ui;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;

import java.util.ArrayList;

public class CollectableAdapter extends RecyclerView.Adapter<CollectableAdapter.CollectableViewHolder> {
    private final ArrayList<Collectable> collectables;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public static class CollectableViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView cardImageView;
        public CollectableViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_collectable_name);
            cardImageView = itemView.findViewById(R.id.image_view_collectable_card);

            itemView.setOnClickListener(v -> {
                if(onItemClickListener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                        onItemClickListener.onItemClick(position);
                }
            });
        }
    }

    public CollectableAdapter(ArrayList<Collectable> collectables) {
        this.collectables = collectables;
    }

    @NonNull
    @Override
    public CollectableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collectable_yugioh, parent, false);
        return new CollectableViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectableViewHolder holder, int position) {
        Collectable collectable = collectables.get(position);
        holder.nameTextView.setText(collectable.getName());
        Picasso.get().load(collectable.getImgLink()).placeholder(R.drawable.card_placeholder).into(holder.cardImageView);
    }

    @Override
    public int getItemCount() {
        return this.collectables.size();
    }

}
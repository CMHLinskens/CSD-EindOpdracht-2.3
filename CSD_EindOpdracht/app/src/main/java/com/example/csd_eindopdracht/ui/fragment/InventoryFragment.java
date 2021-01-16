package com.example.csd_eindopdracht.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.collectable.YugiohCollectable;
import com.example.csd_eindopdracht.ui.CollectableAdapter;

import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    private RecyclerView collectableRecyclerView;
    private CollectableAdapter collectableAdapter;
    private GridLayoutManager layoutManager;
    private ArrayList<Collectable> inventory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        collectableRecyclerView = view.findViewById(R.id.collectable_recycler);
        collectableRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
//        inventory = Data.INSTANCE.getInventory();

        collectableAdapter = new CollectableAdapter(inventory);
        collectableRecyclerView.setLayoutManager(layoutManager);
        collectableRecyclerView.setAdapter(collectableAdapter);

        return view;
    }
}

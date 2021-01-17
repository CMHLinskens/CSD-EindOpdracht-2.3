package com.example.csd_eindopdracht.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.ui.CollectableAdapter;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

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
        layoutManager = new GridLayoutManager(getContext(), 3);
        inventory = (ArrayList<Collectable>) Data.INSTANCE.getInventory();

        collectableAdapter = new CollectableAdapter(inventory);
        collectableRecyclerView.setLayoutManager(layoutManager);
        collectableRecyclerView.setAdapter(collectableAdapter);

        TextView timerTextView = view.findViewById(R.id.text_view_inventory_timer);
        initializeSpinButton(view);


        // TODO: SAVE DARK MAGICIAN
        return view;
    }

    private void initializeSpinButton(View view){
        Button spinButton = view.findViewById(R.id.btn_inventory_spin);

        DateTime lastSpinDateTime = Data.INSTANCE.getLastSpinDate();
        DateTime currentDateTime = DateTime.now();

        if (lastSpinDateTime != null) {
            // If 20 hours have passed since last spin, enable the button
            Period period = new Period(lastSpinDateTime, currentDateTime);
            spinButton.setEnabled(period.getHours() > 20);
        }

        spinButton.setOnClickListener(v -> {

            int randomID = new Random().nextInt(Data.INSTANCE.getCollectables().size() - 1);
            Collectable newCollectable = Data.INSTANCE.getCollectables().get(randomID);
            Data.INSTANCE.addToInventory(newCollectable);
            // TODO: refresh recyclerView
        });
    }
}

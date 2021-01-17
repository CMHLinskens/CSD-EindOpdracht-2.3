package com.example.csd_eindopdracht.ui.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.ui.CollectableAdapter;

import java.util.ArrayList;

/**
 * This class is responsible for displaying the pop-up
 * which contains the collectables of the user
 */
public class ChooseCardPopUp extends Dialog {
    private Collectable collectable;

    public ChooseCardPopUp(Context context, Collectable collectable) {
        super(context);
        this.collectable = collectable;
    }

    public Collectable getCollectable() { return this.collectable; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_card_pop_up);

        RecyclerView collectableRecyclerView = findViewById(R.id.recycler_choose_card_popup);
        collectableRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        CollectableAdapter collectableAdapter = new CollectableAdapter(Data.INSTANCE.getInventory());
        collectableRecyclerView.setLayoutManager(layoutManager);
        collectableRecyclerView.setAdapter(collectableAdapter);

        collectableAdapter.setOnItemClickListener(position -> {
            collectable = Data.INSTANCE.getInventory().get(position);
            dismiss();
        });

        setCanceledOnTouchOutside(true);
    }
}

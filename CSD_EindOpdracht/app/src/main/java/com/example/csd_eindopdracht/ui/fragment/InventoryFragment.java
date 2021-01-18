package com.example.csd_eindopdracht.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.csd_eindopdracht.ui.popup.AlertPopUp;
import com.example.csd_eindopdracht.ui.popup.SpinPopUp;
import com.example.csd_eindopdracht.util.RandomCardListener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatterBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class InventoryFragment extends Fragment {
    private static final String LOGTAG = InventoryFragment.class.getName();

    private RecyclerView collectableRecyclerView;
    private CollectableAdapter collectableAdapter;
    private GridLayoutManager layoutManager;
    private ArrayList<Collectable> inventory;
    private DateTime lastSpinDateTime;
    private DateTime currentDateTime;
    private Period period;
    private boolean isReadyToSpin;
    private TimerTask timerTask;
    private Timer timer;

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

        lastSpinDateTime = Data.INSTANCE.getLastSpinDate();
//        lastSpinDateTime = DateTime.parse("2021-01-13T10:56:15.819+01:00");
        currentDateTime = DateTime.now();

        if (lastSpinDateTime != null) {
            period = new Period(lastSpinDateTime, currentDateTime);
            // Check if 20 hours have passed
            isReadyToSpin = getTotalHours(period) > 20;
        } else {
            isReadyToSpin = true;
        }

        initializeSpinButton(view);
        initializeTimerTextView(view);

        // TODO: SAVE DARK MAGICIAN
        return view;
    }

    private void initializeTimerTextView(View view) {
        TextView timerTextView = view.findViewById(R.id.text_view_inventory_timer);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Every tick, the textView will display the remaining time in hh:mm:ss
                period = new Period(lastSpinDateTime, currentDateTime); // TODO: fix timer seconds
                Period nextTimePeriod = new Period(lastSpinDateTime.plusHours(20).minusHours(getTotalHours(period)), DateTime.now());
                String formattedTime = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSeconds().toFormatter().print(nextTimePeriod);
                String nextSpinTime = formattedTime.replaceAll("-", "");
                new Handler(Looper.getMainLooper()).post(() -> timerTextView.setText("NEXT SPIN: " + nextSpinTime)); // TODO: use string resources
            }
        };
        timer = new Timer();

        if (!isReadyToSpin)
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        else
            timerTextView.setText("READY"); // TODO: use string resources
    }

    private void initializeSpinButton(View view) {
        Button spinButton = view.findViewById(R.id.btn_inventory_spin);


        // If 20 hours have passed since last spin, enable the button
        spinButton.setEnabled(isReadyToSpin);


        spinButton.setOnClickListener(v -> {
            int randomLevel = new Random().nextInt(12) + 1;
            Data.INSTANCE.getRandomCardWithLevel(randomLevel, newCollectable -> {
                Data.INSTANCE.addToInventory(newCollectable);
                Looper.prepare();
                new Handler(Looper.getMainLooper()).post(() -> collectableAdapter.notifyDataSetChanged());
            });
            Data.INSTANCE.setLastSpinDate();
            lastSpinDateTime = DateTime.now();
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
            isReadyToSpin = false;
            spinButton.setEnabled(false);
            Log.d(LOGTAG, "Level " + randomLevel);
            new SpinPopUp(getActivity(), 3000, 720 - randomLevel * 30).show();
            });
    }

    private int getTotalHours(Period period){
        return period.getHours() + period.getDays() * 24;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
    }
}

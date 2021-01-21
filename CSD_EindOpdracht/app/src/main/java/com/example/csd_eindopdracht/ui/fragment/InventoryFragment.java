package com.example.csd_eindopdracht.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.csd_eindopdracht.ui.popup.SpinPopUp;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;

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
    private boolean isReadyToDailySpin;
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
        collectableAdapter.setOnItemClickListener(position -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, Data.INSTANCE.getFactory().createCardDetailFragment(Data.INSTANCE.getInventory().get(position))).addToBackStack(null).commit();
        });
        collectableRecyclerView.setLayoutManager(layoutManager);
        collectableRecyclerView.setAdapter(collectableAdapter);

        lastSpinDateTime = Data.INSTANCE.getLastSpinDate();
        currentDateTime = DateTime.now();

        if (lastSpinDateTime != null) {
            period = new Period(lastSpinDateTime, currentDateTime);
            // Check if 20 hours have passed
            isReadyToDailySpin = getTotalHours(period) > 20;
        } else {
            isReadyToDailySpin = true;
        }

        initializeMapButton(view);
        initializeSpinButton(view);
        initializeTimerTextView(view);

        return view;
    }

    private void initializeMapButton(View view) {
        ImageButton mapButton = view.findViewById(R.id.btn_inventory_map);
        mapButton.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, Data.INSTANCE.getFactory().createMapFragment()).addToBackStack(null).commit();
            getFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_enter_right, R.anim.anim_exit_left).replace(R.id.fragment_container, Data.INSTANCE.getFactory().createMapFragment()).addToBackStack(null).commit();
        });
    }

    private void initializeTimerTextView(View view) {
        TextView timerTextView = view.findViewById(R.id.text_view_inventory_timer);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                // Every tick, the textView will display the remaining time in hh:mm:ss
                period = new Period(lastSpinDateTime, currentDateTime);
                Period nextTimePeriod = new Period(lastSpinDateTime.plusHours(20).minusHours(getTotalHours(period)), DateTime.now());
                String formattedTime = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSeconds().toFormatter().print(nextTimePeriod);
                String nextSpinTime = formattedTime.replaceAll("-", "");
                new Handler(Looper.getMainLooper()).post(() -> timerTextView.setText(getString(R.string.spin_wait_text) + nextSpinTime));
            }
        };
        timer = new Timer();

        if (!isReadyToDailySpin)
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        else
            timerTextView.setText(getString(R.string.spin_ready_text));
    }

    private void initializeSpinButton(View view) {
        Button spinButton = view.findViewById(R.id.btn_inventory_spin);

        // If 20 hours have passed since last spin, enable the button
        if(isReadyToDailySpin) {
            spinButton.setEnabled(true);
            spinButton.setText(getString(R.string.button_daily_spin));

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
                isReadyToDailySpin = false;
                SpinPopUp spinPopUp = new SpinPopUp(getActivity(), 3000, -360 - randomLevel * 30);
                spinPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                spinPopUp.show();
                initializeSpinButton(view);
            });
        } else if (Data.INSTANCE.getPoints() >= 1000){
            spinButton.setEnabled(true);
            spinButton.setText(getString(R.string.button_normal_spin));

            spinButton.setOnClickListener(v -> {
                Data.INSTANCE.addOrSubtractPoints(-1000);
                int randomLevel = new Random().nextInt(12) + 1;
                Data.INSTANCE.getRandomCardWithLevel(randomLevel, newCollectable -> {
                    Data.INSTANCE.addToInventory(newCollectable);
                    Looper.prepare();
                    new Handler(Looper.getMainLooper()).post(() -> collectableAdapter.notifyDataSetChanged());
                });
                SpinPopUp spinPopUp = new SpinPopUp(getActivity(), 3000, -360 - randomLevel * 30);
                spinPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                spinPopUp.show();
                initializeSpinButton(view);
            });
        } else {
            spinButton.setEnabled(false);
            spinButton.setText(getString(R.string.button_normal_spin));
        }
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

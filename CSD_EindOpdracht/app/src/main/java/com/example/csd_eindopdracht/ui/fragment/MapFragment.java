package com.example.csd_eindopdracht.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class MapFragment extends Fragment {
    private static final String LOGTAG = MapFragment.class.getName();
    private Button button;
    private MapView mapView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.osm_map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setDestroyMode(false);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        button = view.findViewById(R.id.btn_map_inventory);
        button.setOnClickListener(v -> {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InventoryFragment()).addToBackStack(null).commit(); // TODO: use factory
        });
        return view;
    }

    /**
     * Event handler for my location button
     * @param view
     */
    public void onMyLocationClick(View view) {
        Log.d(LOGTAG, "Clicked on MyLocation button");
    }
}

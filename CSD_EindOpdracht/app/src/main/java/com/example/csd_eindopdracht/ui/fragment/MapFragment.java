package com.example.csd_eindopdracht.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.services.OpenRouteServiceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

public class MapFragment extends Fragment {
    private static final String LOGTAG = MapFragment.class.getName();

    private MapView mapView = null;
    private IMapController mapController = null;
    private final GeoPoint myLocation = new GeoPoint(0,0);
    private final OpenRouteServiceManager openRouteServiceManager = new OpenRouteServiceManager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Subscribe to EventBus
        EventBus.getDefault().register(this);

        mapView = view.findViewById(R.id.osm_map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setDestroyMode(false);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        mapController = mapView.getController();
        mapController.setCenter(new GeoPoint(51.5988037, 4.77801));
        mapController.setZoom(15);

        ImageButton myLocationButton = view.findViewById(R.id.btn_map_my_location);
        myLocationButton.setOnClickListener(view1 -> {
            mapController.setCenter(myLocation);
            mapView.invalidate();
        });

        return view;
    }

    /**
     * Method triggered by EventBus when new location is received
     * @param location new location data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(Location location){
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());
    }

    private void getRouteToPoint(GeoPoint geoPoint){
        
    }
}

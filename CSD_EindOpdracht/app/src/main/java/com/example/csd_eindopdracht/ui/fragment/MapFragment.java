package com.example.csd_eindopdracht.ui.fragment;

import android.graphics.Color;
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
import com.example.csd_eindopdracht.dataModel.ors.Route;
import com.example.csd_eindopdracht.dataModel.ors.TravelType;
import com.example.csd_eindopdracht.services.OpenRouteServiceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {
    private static final String LOGTAG = MapFragment.class.getName();
    private Button button;
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

        button = view.findViewById(R.id.btn_map_inventory);
        button.setOnClickListener(v -> {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InventoryFragment()).addToBackStack(null).commit(); // TODO: use factory
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

    /**
     * Sends route API call to ORS and draws a line of the retrieved points
     * @param geoPoint point to draw the route to
     */
    private void getRouteToPoint(GeoPoint geoPoint){
        ArrayList<GeoPoint> receivedGeoPoints = new ArrayList<>();

        openRouteServiceManager.getRoute(myLocation, geoPoint, TravelType.FOOT_WALKING, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(LOGTAG, e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject responseJson;
                try {
                    // Convert received json to route and gather all the coordinates
                    responseJson = new JSONObject(response.body().string());
                    Route route = new Route(responseJson);
                    ArrayList<double[]> coordinates = route.features.get(0).geometry.coordinates;
                    for(double[] coordinate : coordinates){
                        receivedGeoPoints.add(new GeoPoint(coordinate[1], coordinate[0]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Create polyline and display it on the map
                Polyline line = createRouteLine(receivedGeoPoints);
                mapView.getOverlayManager().add(line);
                mapView.invalidate();
            }
        });
    }

    /**
     * Creates a polyline with the given geo points
     * @param geoPoints points to draw the line with
     * @return polyline with the given points
     */
    private Polyline createRouteLine(List<GeoPoint> geoPoints) {
        Polyline line = new Polyline();
        line.setTitle("Route");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.getOutlinePaint().setStrokeWidth(10f);
        line.getOutlinePaint().setColor(Color.GRAY);
        line.setPoints(geoPoints);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        return line;
    }
}

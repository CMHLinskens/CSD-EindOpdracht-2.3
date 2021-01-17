package com.example.csd_eindopdracht.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.BuildConfig;
import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.ors.Route;
import com.example.csd_eindopdracht.dataModel.ors.TravelType;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.services.LocationService;
import com.example.csd_eindopdracht.services.OpenRouteServiceManager;
import com.example.csd_eindopdracht.services.ServerManager;

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
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {
    private static final String LOGTAG = MapFragment.class.getName();

    private MapView mapView = null;
    private IMapController mapController = null;
    private final GeoPoint myLocation = new GeoPoint(0,0);
    private Marker myLocationMarker = null;
    private final OpenRouteServiceManager openRouteServiceManager = new OpenRouteServiceManager();
    private WayPoint selectedWayPoint = null;
    private GeoPoint completionPoint = null;
    private ImageButton guessButton = null;
    private static final double COMPLETION_THRESHOLD = 5;
    private Polyline line = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if(savedInstanceState != null){
            WayPoint wayPoint = (WayPoint) savedInstanceState.get("wayPoint");
            if(wayPoint != null){
                startRoute(wayPoint);
            }
        }

        // Subscribe to EventBus
        EventBus.getDefault().register(this);

        mapView = view.findViewById(R.id.osm_map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setDestroyMode(false);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        mapController = mapView.getController();
        mapController.setCenter(new GeoPoint(51.5988037, 4.77801));
        mapController.setZoom(16.5);

        myLocationMarker = new Marker(mapView);
        myLocationMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_location, null));
        myLocationMarker.setPosition(myLocation);
        mapView.getOverlays().add(myLocationMarker);

        ImageButton myLocationButton = view.findViewById(R.id.btn_map_mylocation);
        myLocationButton.setOnClickListener(view1 -> {
            mapController.setCenter(myLocation);
            mapController.setZoom(16.5);
            mapView.invalidate();
        });

        guessButton = view.findViewById(R.id.btn_map_guess);
        guessButton.setOnClickListener(view1 -> checkGuess());

        drawWayPoints();
        return view;
    }

    /**
     * Draw a marker for each way point
     */
    private void drawWayPoints() {
        for(WayPoint wp : Data.INSTANCE.getWayPoints()){
            Marker marker = new Marker(mapView);
            marker.setPosition(wp.getLocation());
            Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(AppCompatResources.getDrawable(Objects.requireNonNull(getContext()), R.drawable.ic_waypoint)));
            drawable.setTint(Color.BLACK);
            marker.setIcon(drawable);
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                Log.d(LOGTAG, "Clicked on way point: " + wp.getName());
                startRoute(wp);
                // TODO show way point detail screen
                return false;
            });
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    /**
     * Sets selectedWayPoint and starts drawing a route on the map
     * @param wayPoint selected way point
     */
    private void startRoute(WayPoint wayPoint) {
        selectedWayPoint = wayPoint;
        getRouteToPoint(wayPoint.getLocation());
    }

    /**
     * Reset selectedWayPoint and completionPoint
     */
    private void stopRoute(){
        removeLineFromMap();
        completionPoint = null;
        selectedWayPoint = null;
    }

    /**
     * Method triggered by EventBus when new location is received
     * @param location new location data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(Location location){
        myLocation.setLatitude(location.getLatitude());
        myLocation.setLongitude(location.getLongitude());

        if(selectedWayPoint != null){
            getRouteToPoint(selectedWayPoint.getLocation());
        }

        myLocationMarker.setPosition(myLocation);
        mapView.invalidate();
    }

    /**
     * Method triggered by EventBus when a way point is reached
     * @param event data of reached way point
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWayPointReachedEvent(LocationService.WayPointReachedEvent event) {
        if(event.getWayPoint().getName().equals(selectedWayPoint.getName())) {
            Log.d(LOGTAG, "Reached way point");
            completionPoint = event.getCompletionPoint();
            // TODO show pop-up with explanation
            guessButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Removes the drawn line on the map if it exists
     */
    private void removeLineFromMap(){
        if(line != null){
            mapView.getOverlays().remove(line);
            line = null;
        }
    }

    /**
     * Sends route API call to ORS and draws a line of the retrieved points
     * @param geoPoint point to draw the route to
     */
    private void getRouteToPoint(GeoPoint geoPoint){
        removeLineFromMap();
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
                line = createRouteLine(receivedGeoPoints);
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

    /**
     * Check result of the guess
     * Complete the game or show a toast for feedback
     */
    private void checkGuess() {
        if(completionPoint != null) {
            if (myLocation.distanceToAsDouble(completionPoint) <= COMPLETION_THRESHOLD) {
                Log.d(LOGTAG, "Completed \nReceived collectable: " + ServerManager.INSTANCE.getCachePointCollectable((CachePoint) selectedWayPoint).getName());
                // TODO add to inventory
                // TODO show completion screen
                stopRoute();
            } else if (myLocation.distanceToAsDouble(completionPoint) <= COMPLETION_THRESHOLD * 2) {
                Toast.makeText(getContext(), getString(R.string.hot), Toast.LENGTH_SHORT).show();
            } else if (myLocation.distanceToAsDouble(completionPoint) <= COMPLETION_THRESHOLD * 3){
                Toast.makeText(getContext(), getString(R.string.warm), Toast.LENGTH_SHORT).show();
            } else if (myLocation.distanceToAsDouble(completionPoint) <= COMPLETION_THRESHOLD * 5){
                Toast.makeText(getContext(), getString(R.string.cold), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.freezing), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

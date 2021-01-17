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
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.ors.Route;
import com.example.csd_eindopdracht.dataModel.ors.TravelType;
import com.example.csd_eindopdracht.dataModel.wayPoint.CachePoint;
import com.example.csd_eindopdracht.dataModel.wayPoint.WayPoint;
import com.example.csd_eindopdracht.services.LocationService;
import com.example.csd_eindopdracht.services.OpenRouteServiceManager;
import com.example.csd_eindopdracht.services.ServerManager;
import com.example.csd_eindopdracht.ui.popup.AlertPopUp;
import com.example.csd_eindopdracht.util.RandomCardListener;

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
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {
    private static final String LOGTAG = MapFragment.class.getName();
    private Button button;
    private MapView mapView = null;
    private IMapController mapController = null;
    private final GeoPoint myLocation = new GeoPoint(0,0);
    private Marker myLocationMarker = null;
    private final OpenRouteServiceManager openRouteServiceManager = new OpenRouteServiceManager();
    private WayPoint selectedWayPoint = null;
    private GeoPoint completionPoint = null;
    private ImageButton guessButton = null;
    private Polyline line = null;
    private Polygon searchArea = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Subscribe to EventBus
        if(!EventBus.getDefault().isRegistered(this))
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
        if(myLocation == null){

        }
        myLocationMarker.setPosition(myLocation);
        mapView.getOverlays().add(myLocationMarker);

        ImageButton myLocationButton = view.findViewById(R.id.btn_map_mylocation);
        myLocationButton.setOnClickListener(view1 -> {
            mapController.setCenter(myLocation);
            mapController.setZoom(16.5);
            mapView.invalidate();
        });

        button = view.findViewById(R.id.btn_map_inventory);
        button.setOnClickListener(v -> {
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new InventoryFragment()).addToBackStack(null).commit(); // TODO: use factory
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
                if(selectedWayPoint == null)
                    startRoute(wp);
                else if (selectedWayPoint != wp)
                    startRoute(wp);
                else
                    stopRoute();
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
        // TODO make check to see if we are already standing on the selected way point
        selectedWayPoint = wayPoint;
        getRouteToPoint(wayPoint.getLocation());
    }

    /**
     * Reset selectedWayPoint and completionPoint
     */
    private void stopRoute(){
        removeLineFromMap();
        removeSearchAreaFromMap();
        if(completionPoint != null) {
            completionPoint = null;
            guessButton.setVisibility(View.GONE);
        }
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

        // If we have a route but we have not reached it yet, update the route
        if(selectedWayPoint != null && completionPoint == null){
            getRouteToPoint(selectedWayPoint.getLocation());
        // If we have a route and we have reached it, check if we are still in bounds
        } else if(selectedWayPoint != null){
            if(!LocationService.checkIfInBounds(myLocation, selectedWayPoint.getLocation())) {
                outOfBounds();
            }
        }

        // Update my location marker
        myLocationMarker.setPosition(myLocation);
        mapView.invalidate();
    }

    /**
     * Reset the completion point and let the user know he/she has walked out of the zone
     */
    private void outOfBounds() {
        completionPoint = null;
        guessButton.setVisibility(View.GONE);
        new AlertPopUp(getActivity(),getString(R.string.out_of_bounds_title), getString(R.string.out_of_bounds_message)).show();
        getRouteToPoint(selectedWayPoint.getLocation());
        removeSearchAreaFromMap();
    }

    /**
     * Method triggered by EventBus when a way point is reached
     * @param event data of reached way point
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWayPointReachedEvent(LocationService.WayPointReachedEvent event) {
        if(selectedWayPoint == null || completionPoint != null) { return; }
        if(event.getWayPoint().getName().equals(selectedWayPoint.getName())) {
            removeLineFromMap();
            Log.d(LOGTAG, "Reached way point");
            completionPoint = event.getCompletionPoint();

            // Make guess button appear on screen
            guessButton.setVisibility(View.VISIBLE);

            // Show pop up with explanation
            new AlertPopUp(getActivity(),getString(R.string.reached_popup_title), getString(R.string.reached_popup_message)).show();

            // Draw search area on the map
            searchArea = createCircleForMap(selectedWayPoint.getLocation(), LocationService.DISTANCE_THRESHOLD);
            mapView.getOverlays().add(searchArea);
            mapView.invalidate();
        }
    }

    /**
     * Removes the drawn line on the map if it exists
     */
    private void removeLineFromMap(){
        if(line != null){
            mapView.getOverlays().remove(line);
            line = null;
            mapView.invalidate();
        }
    }

    /**
     * Removes the drawn search area on the map if it exists
     */
    private void removeSearchAreaFromMap() {
        if(searchArea != null){
            mapView.getOverlays().remove(searchArea);
            searchArea = null;
            mapView.invalidate();
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
     * Creates and a circle for map view
     * @param point center point of circle
     * @param radius radius of circle
     * @return polygon as a circle
     */
    private Polygon createCircleForMap(GeoPoint point, double radius){
        List<GeoPoint> circlePoints = Polygon.pointsAsCircle(point, radius);
        Polygon circle = new Polygon(mapView);
        circle.setPoints(circlePoints);
        circle.getFillPaint().setColor(Color.BLUE);
        circle.getFillPaint().setAlpha(100);
        circle.setTitle("Search Area");
        return circle;
    }

    /**
     * Check result of the guess
     * Complete the game or show a toast for feedback
     */
    private void checkGuess() {
        if(completionPoint != null) {
            if (LocationService.checkIfInBounds(myLocation, completionPoint, 1)) {
                Data.INSTANCE.getRandomCardWithLevel((new Random().nextInt(12) + 1), newCollectable -> {
                    Log.d(LOGTAG, "Completed \nReceived collectable: " + newCollectable.getName());
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RewardFragment(newCollectable)).commit();
                });
                stopRoute();
            } else if (LocationService.checkIfInBounds(myLocation, completionPoint, 2)) {
                Toast.makeText(getContext(), getString(R.string.hot), Toast.LENGTH_SHORT).show();
            } else if (LocationService.checkIfInBounds(myLocation, completionPoint, 3)) {
                Toast.makeText(getContext(), getString(R.string.warm), Toast.LENGTH_SHORT).show();
            } else if (LocationService.checkIfInBounds(myLocation, completionPoint, 5)) {
                Toast.makeText(getContext(), getString(R.string.cold), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.freezing), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

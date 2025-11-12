package com.example.myapplicationsaleapp.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplicationsaleapp.R;
import com.example.myapplicationsaleapp.databinding.FragmentMapBinding;
import com.example.myapplicationsaleapp.Data.model.StoreLocation;
import com.example.myapplicationsaleapp.Data.repository.LocationRepository;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FragmentMapBinding binding;
    private MapView mapView;
    private IMapController mapController;
    private MyLocationNewOverlay locationOverlay;
    private List<StoreLocation> storeLocations = new ArrayList<>();
    private StoreLocation selectedStore = null;

    // UI elements
    private ProgressBar progressBar;
    private View cardStoreDetails;
    private TextView textViewStoreName;
    private TextView textViewStoreAddress;
    private TextView textViewStoreHours;
    private TextView textViewStorePhone;
    private Button buttonGetDirections;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Important! Setup OSMDroid configuration before inflating the view
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());

        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        mapView = binding.map;
        progressBar = binding.progressBar;
        cardStoreDetails = binding.cardStoreDetails;
        textViewStoreName = view.findViewById(R.id.textViewStoreName);
        textViewStoreAddress = view.findViewById(R.id.textViewStoreAddress);
        textViewStoreHours = view.findViewById(R.id.textViewStoreHours);
        textViewStorePhone = view.findViewById(R.id.textViewStorePhone);
        buttonGetDirections = view.findViewById(R.id.buttonGetDirections);

        // Setup the map
        setupMap();

        // Set click listener for directions button
        if (buttonGetDirections != null) {
            buttonGetDirections.setOnClickListener(v -> {
                if (selectedStore != null) {
                    openDirectionsToStore(selectedStore);
                }
            });
        }

        // My location button click listener
        binding.fabMyLocation.setOnClickListener(v -> {
            if (hasLocationPermission()) {
                zoomToCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });

        // OpenStreetMap button
        binding.fabOpenStreetMap.setOnClickListener(v -> openOpenStreetMap());

        // Load store locations
        loadStoreLocations();
    }

    private void setupMap() {
        // Basic map setup
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(12.0);

        // Default location (HCM)
        GeoPoint startPoint = new GeoPoint(10.82, 106.8);
        mapController.setCenter(startPoint);

        // Add location overlay if permission granted
        if (hasLocationPermission()) {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        try {
            locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
            locationOverlay.enableMyLocation();
            mapView.getOverlays().add(locationOverlay);
            mapView.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "Error enabling location: " + e.getMessage());
        }
    }

    private void zoomToCurrentLocation() {
        if (locationOverlay != null && locationOverlay.getMyLocation() != null) {
            mapController.animateTo(locationOverlay.getMyLocation());
            mapController.setZoom(17.0);
        } else {
            Toast.makeText(requireContext(), "Vị trí chưa sẵn sàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStoreLocations() {
        showLoading(true);

        LocationRepository locationRepository = new LocationRepository();
        locationRepository.getAllStoreLocations(new LocationRepository.LocationCallback<List<StoreLocation>>() {
            @Override
            public void onSuccess(List<StoreLocation> result) {
                storeLocations = result;
                showLoading(false);
                addStoreMarkers();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(requireContext(), "Lỗi tải vị trí cửa hàng: " + message, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading store locations: " + message);
            }
        });
    }

    private void addStoreMarkers() {
        if (storeLocations.isEmpty()) {
            return;
        }

        // Clear existing markers (except location overlay)
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        // List to calculate bounds
        List<GeoPoint> points = new ArrayList<>();

        for (StoreLocation store : storeLocations) {
            GeoPoint point = new GeoPoint(store.getLatitude(), store.getLongitude());
            points.add(point);

            // Create marker
            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(store.getName());
            marker.setSnippet(store.getAddress());

            // Store the store location object with the marker
            marker.setRelatedObject(store);

            // Set marker click listener
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                Object relatedObject = marker1.getRelatedObject();
                if (relatedObject instanceof StoreLocation) {
                    selectedStore = (StoreLocation) relatedObject;
                    showStoreDetails(selectedStore);
                }
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        // Zoom to show all markers
        if (!points.isEmpty()) {
            // Find center point of all locations
            double avgLat = 0, avgLon = 0;
            for (GeoPoint p : points) {
                avgLat += p.getLatitude();
                avgLon += p.getLongitude();
            }
            avgLat /= points.size();
            avgLon /= points.size();

            // Set center and zoom to show all
            mapController.setCenter(new GeoPoint(avgLat, avgLon));
            mapController.setZoom(13.0);
        }

        mapView.invalidate();
    }

    private void showStoreDetails(StoreLocation store) {
        if (textViewStoreName != null) textViewStoreName.setText(store.getName());
        if (textViewStoreAddress != null) textViewStoreAddress.setText(store.getAddress());
        if (textViewStoreHours != null) textViewStoreHours.setText(store.getOpeningHours());
        if (textViewStorePhone != null) textViewStorePhone.setText(store.getPhone());

        // Show the card
        if (cardStoreDetails != null) {
            cardStoreDetails.setVisibility(View.VISIBLE);
        }
    }

    private void openDirectionsToStore(StoreLocation store) {
        // Create a Uri for map directions
        Uri gmmIntentUri = Uri.parse("geo:" +
                store.getLatitude() + "," + store.getLongitude() +
                "?q=" + Uri.encode(store.getName() + ", " + store.getAddress()));

        // Create an Intent to launch any maps app
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Try to start an activity to handle the intent
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback to browser if no map app is available
            Uri browserUri = Uri.parse("https://www.openstreetmap.org/?mlat=" +
                    store.getLatitude() + "&mlon=" + store.getLongitude() +
                    "&zoom=16");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            if (browserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(requireContext(), "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openOpenStreetMap() {
        try {
            IGeoPoint centerPoint = mapView.getMapCenter();
            double latitude = centerPoint.getLatitude();
            double longitude = centerPoint.getLongitude();
            double zoom = mapView.getZoomLevelDouble();

            String osmUrl = String.format(java.util.Locale.US,
                "https://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f&zoom=%.0f",
                latitude, longitude, zoom);

            Uri osmUri = Uri.parse(osmUrl);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, osmUri);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (browserIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(requireContext(), "Không thể mở OpenStreetMap", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                zoomToCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Context ctx = requireActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
        binding = null;
    }
}

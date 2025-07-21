package com.example.optifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;
import java.util.List;

public class CoverageMapActivity extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationCallback locationCallback;
    private WiFiNetwork currentNetwork;
    private List<WiFiScanResult> scanResults = new ArrayList<>();
    private WifiScanner wifiScanner;
    private TextView mapStatusText;
    private long lastScanTime = 0;
    private static final long MIN_SCAN_INTERVAL = 30_000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coverage_map);

        // Отримуємо дані мережі
        currentNetwork = getIntent().getParcelableExtra("network");
        if (currentNetwork == null) {
            finish();
            return;
        }

        // Ініціалізація UI
        mapStatusText = findViewById(R.id.mapStatusText);

        // Ініціалізація OpenStreetMap
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Додаємо компас
        CompassOverlay compassOverlay = new CompassOverlay(this, mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Ініціалізація Wi-Fi сканера
        wifiScanner = new WifiScanner(this);

        // Ініціалізація локації
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();
    }

    private void setupLocationUpdates() {
        if (checkLocationPermission()) {
            startLocationTracking();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        }
    }

    private void startLocationTracking() {
        // Налаштування оверлею для відображення позиції
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Налаштування періодичного оновлення локації
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    updateMapWithLocation(locationResult.getLastLocation());
                    performWiFiScan(locationResult.getLastLocation());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void performWiFiScan(Location location) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime < MIN_SCAN_INTERVAL) {
            return; // Skip scan if within throttle interval
        }

        try {
            mapStatusText.setText("Сканування Wi-Fi...");
            wifiScanner.performScan(new WifiScanner.ScanCallback() {
                @Override
                public void onScanStarted() {
                    // Scanning started
                }

                @Override
                public void onScanCompleted(List<WiFiNetwork> networks) {
                    lastScanTime = System.currentTimeMillis();
                    // Find the current network in scan results
                    for (WiFiNetwork network : networks) {
                        if (network.getBssid().equals(currentNetwork.getBssid())) {
                            // Add scan result with location
                            scanResults.add(new WiFiScanResult(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    network.getSignalLevel()
                            ));
                            updateSignalHeatMap(new GeoPoint(location.getLatitude(), location.getLongitude()));
                            mapStatusText.setText("Теплова карта Wi-Fi покриття");
                            break;
                        }
                    }
                }

                @Override
                public void onScanError(String error) {
                    Toast.makeText(CoverageMapActivity.this, error, Toast.LENGTH_SHORT).show();
                    mapStatusText.setText("Помилка сканування");
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Необхідний дозвіл на доступ до локації", Toast.LENGTH_SHORT).show();
            mapStatusText.setText("Помилка: немає дозволу на локацію");
        }
    }

    private void updateMapWithLocation(Location location) {
        if (location == null) return;

        // Оновлюємо центр карти
        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapView.getController().setCenter(currentLocation);
        mapView.getController().setZoom(18.0);

        // Додаємо маркер поточної позиції
        addUserMarker(currentLocation);
    }

    private void addUserMarker(GeoPoint location) {
        // Видаляємо старі маркери
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker &&
                ((Marker) overlay).getTitle().equals("Ваше місцезнаходження"));

        Marker userMarker = new Marker(mapView);
        userMarker.setPosition(location);
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        userMarker.setTitle("Ваше місцезнаходження");
        userMarker.setIcon(getResources().getDrawable(R.drawable.ic_user_location));
        mapView.getOverlays().add(userMarker);
    }

    private void updateSignalHeatMap(GeoPoint userLocation) {
        // Видаляємо стару теплову карту
        mapView.getOverlays().removeIf(overlay -> overlay instanceof HeatMapOverlay);

        // Створюємо списки для точок і сили сигналу
        List<GeoPoint> points = new ArrayList<>();
        List<Integer> signalStrengths = new ArrayList<>();

        // Додаємо зібрані результати сканування
        for (WiFiScanResult result : scanResults) {
            points.add(new GeoPoint(result.latitude, result.longitude));
            signalStrengths.add(result.signalStrength);
        }

        // Додаємо поточну позицію користувача
        points.add(userLocation);
        signalStrengths.add(currentNetwork.getSignalLevel());

        // Додаємо нову теплову карту
        HeatMapOverlay heatMap = new HeatMapOverlay(points, signalStrengths);
        mapView.getOverlays().add(heatMap);
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (wifiScanner != null) {
            wifiScanner.cleanup();
        }
    }
}
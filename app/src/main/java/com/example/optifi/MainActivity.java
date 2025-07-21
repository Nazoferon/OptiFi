package com.example.optifi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private WifiScannerViewModel viewModel;
    private Button scanButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private RecyclerView networksList;
    private WifiNetworkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupViewModel();
        setupRecyclerView();
        checkPermissions();
    }

    private void initializeViews() {
        scanButton = findViewById(R.id.scanButton);
        progressBar = findViewById(R.id.progressBar);
        statusText = findViewById(R.id.statusText);
        networksList = findViewById(R.id.networksList);

        scanButton.setOnClickListener(v -> startWifiScan());
        progressBar.setVisibility(View.GONE);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(WifiScannerViewModel.class);

        viewModel.getNetworks().observe(this, this::updateNetworksList);
        viewModel.getIsScanning().observe(this, this::updateScanningState);
        viewModel.getErrorMessage().observe(this, this::showError);
    }

    private void setupRecyclerView() {
        adapter = new WifiNetworkAdapter(this::onNetworkSelected);
        networksList.setLayoutManager(new LinearLayoutManager(this));
        networksList.setAdapter(adapter);
    }

    private void onNetworkSelected(WiFiNetwork network) {
        Intent intent = new Intent(this, NetworkDetailActivity.class);
        intent.putExtra("network", network);
        startActivity(intent);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Дозвіл надано", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Дозвіл необхідний для сканування Wi-Fi", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startWifiScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.startScan();
        } else {
            checkPermissions();
        }
    }

    private void updateNetworksList(List<WiFiNetwork> networks) {
        adapter.updateNetworks(networks);
        statusText.setText(String.format("Знайдено мереж: %d", networks.size()));
    }

    private void updateScanningState(boolean isScanning) {
        scanButton.setEnabled(!isScanning);
        progressBar.setVisibility(isScanning ? View.VISIBLE : View.GONE);
        statusText.setText(isScanning ? "Сканування..." : "Готово до сканування");
    }

    private void showError(String error) {
        if (error != null && !error.isEmpty()) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
}
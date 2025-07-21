package com.example.optifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import java.util.ArrayList;
import java.util.List;

public class WifiScanner {
    private final WifiManager wifiManager;
    private final Context context;
    private ScanResultsReceiver scanReceiver;
    private ScanCallback callback;
    private boolean isReceiverRegistered = false;

    public interface ScanCallback {
        void onScanStarted();
        void onScanCompleted(List<WiFiNetwork> networks);
        void onScanError(String error);
    }

    public WifiScanner(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void performScan(ScanCallback callback) throws SecurityException {
        this.callback = callback;

        if (!checkPermissions()) {
            throw new SecurityException("Location permission required");
        }

        if (!wifiManager.isWifiEnabled()) {
            callback.onScanError("Wi-Fi вимкнено");
            return;
        }

        // Prevent multiple scans
        if (isReceiverRegistered) {
            callback.onScanError("Сканування вже виконується");
            return;
        }

        setupReceiver();
        callback.onScanStarted();

        boolean scanStarted = wifiManager.startScan();
        if (!scanStarted) {
            cleanup();
            callback.onScanError("Не вдалося розпочати сканування");
        }
    }

    private boolean checkPermissions() {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private void setupReceiver() {
        if (!isReceiverRegistered) {
            scanReceiver = new ScanResultsReceiver();
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(scanReceiver, filter);
            isReceiverRegistered = true;
        }
    }

    private List<WiFiNetwork> processResults(List<ScanResult> results) {
        List<WiFiNetwork> networks = new ArrayList<>();

        for (ScanResult result : results) {
            if (result.SSID != null && !result.SSID.isEmpty()) {
                WiFiNetwork network = new WiFiNetwork(
                        result.SSID,
                        result.BSSID,
                        result.level,
                        result.frequency,
                        result.capabilities
                );
                networks.add(network);
            }
        }

        return networks;
    }

    public void cleanup() {
        if (isReceiverRegistered && scanReceiver != null) {
            try {
                context.unregisterReceiver(scanReceiver);
                isReceiverRegistered = false;
                scanReceiver = null;
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private class ScanResultsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (success) {
                List<ScanResult> results = wifiManager.getScanResults();
                List<WiFiNetwork> networks = processResults(results);
                callback.onScanCompleted(networks);
            } else {
                callback.onScanError("Сканування завершилося помилкою");
            }

            cleanup();
        }
    }
}
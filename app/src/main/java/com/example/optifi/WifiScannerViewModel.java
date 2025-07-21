package com.example.optifi;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;

public class WifiScannerViewModel extends AndroidViewModel {
    private final MutableLiveData<List<WiFiNetwork>> networks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isScanning = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private WifiScanner wifiScanner;

    public WifiScannerViewModel(@NonNull Application application) {
        super(application);
        wifiScanner = new WifiScanner(application);
        networks.setValue(new ArrayList<>());
        isScanning.setValue(false);
    }

    public LiveData<List<WiFiNetwork>> getNetworks() { return networks; }
    public LiveData<Boolean> getIsScanning() { return isScanning; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void startScan() {
        try {
            wifiScanner.performScan(new WifiScanner.ScanCallback() {
                @Override
                public void onScanStarted() {
                    isScanning.postValue(true);
                }

                @Override
                public void onScanCompleted(List<WiFiNetwork> scanResults) {
                    networks.postValue(scanResults);
                    isScanning.postValue(false);
                }

                @Override
                public void onScanError(String error) {
                    errorMessage.postValue(error);
                    isScanning.postValue(false);
                }
            });
        } catch (SecurityException e) {
            errorMessage.setValue("Необхідний дозвіл на доступ до локації");
            isScanning.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (wifiScanner != null) {
            wifiScanner.cleanup();
        }
    }
}
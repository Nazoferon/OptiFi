package com.example.optifi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NetworkDetailActivity extends AppCompatActivity {

    private TextView ssidDetail, bssidDetail, signalDetail, frequencyDetail,
            qualityDetail, capabilitiesDetail;
    private Button viewCoverageButton, getRecommendationsButton;
    private WiFiNetwork network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_detail);

        network = getIntent().getParcelableExtra("network");
        if (network == null) {
            finish();
            return;
        }

        initializeViews();
        populateNetworkDetails();
        setupButtons();
    }

    private void initializeViews() {
        ssidDetail = findViewById(R.id.ssidDetail);
        bssidDetail = findViewById(R.id.bssidDetail);
        signalDetail = findViewById(R.id.signalDetail);
        frequencyDetail = findViewById(R.id.frequencyDetail);
        qualityDetail = findViewById(R.id.qualityDetail);
        capabilitiesDetail = findViewById(R.id.capabilitiesDetail);
        viewCoverageButton = findViewById(R.id.viewCoverageButton);
        getRecommendationsButton = findViewById(R.id.getRecommendationsButton);
    }

    private void populateNetworkDetails() {
        ssidDetail.setText(network.getSsid());
        bssidDetail.setText(network.getBssid());
        signalDetail.setText(network.getSignalLevel() + " dBm");
        frequencyDetail.setText(network.getFrequency() + " MHz (" + network.getFrequencyBand() + ")");
        qualityDetail.setText(network.getQuality().getUkrainianName());
        capabilitiesDetail.setText(network.getCapabilities());
    }

    private void setupButtons() {
        viewCoverageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CoverageMapActivity.class);
            intent.putExtra("network", network);
            startActivity(intent);
        });

        getRecommendationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendationsActivity.class);
            intent.putExtra("network", network);
            startActivity(intent);
        });
    }
}
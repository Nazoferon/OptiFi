package com.example.optifi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WifiNetworkAdapter extends RecyclerView.Adapter<WifiNetworkAdapter.NetworkViewHolder> {

    public interface OnNetworkClickListener {
        void onNetworkClick(WiFiNetwork network);
    }

    private List<WiFiNetwork> networks = new ArrayList<>();
    private OnNetworkClickListener clickListener;

    public WifiNetworkAdapter(OnNetworkClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateNetworks(List<WiFiNetwork> newNetworks) {
        this.networks = newNetworks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifi_network, parent, false);
        return new NetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {
        holder.bind(networks.get(position));
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    class NetworkViewHolder extends RecyclerView.ViewHolder {
        private TextView ssidText, signalText, frequencyText, qualityText;
        private ImageView signalIcon;

        NetworkViewHolder(@NonNull View itemView) {
            super(itemView);
            ssidText = itemView.findViewById(R.id.ssidText);
            signalText = itemView.findViewById(R.id.signalText);
            frequencyText = itemView.findViewById(R.id.frequencyText);
            qualityText = itemView.findViewById(R.id.qualityText);
            signalIcon = itemView.findViewById(R.id.signalIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onNetworkClick(networks.get(position));
                }
            });
        }

        void bind(WiFiNetwork network) {
            ssidText.setText(network.getSsid());
            signalText.setText(network.getSignalLevel() + " dBm");
            frequencyText.setText(network.getFrequencyBand());
            qualityText.setText(network.getQuality().getUkrainianName());

            // Set signal strength icon based on quality
            int iconRes = getSignalIcon(network.getQuality());
            signalIcon.setImageResource(iconRes);
        }

        private int getSignalIcon(WiFiNetwork.SignalQuality quality) {
            switch (quality) {
                case EXCELLENT: return android.R.drawable.ic_media_ff;
                case GOOD: return android.R.drawable.ic_media_play;
                case FAIR: return android.R.drawable.ic_media_pause;
                default: return android.R.drawable.ic_media_previous;
            }
        }
    }
}
package com.example.optifi;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import java.util.List;

public class HeatMapOverlay extends Overlay {
    private List<GeoPoint> points;
    private List<Integer> signalStrengths;
    private Paint heatmapPaint;

    public HeatMapOverlay(List<GeoPoint> points, List<Integer> signalStrengths) {
        this.points = points;
        this.signalStrengths = signalStrengths;
        init();
    }

    private void init() {
        heatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow || points == null || signalStrengths == null || points.size() != signalStrengths.size()) {
            return;
        }

        for (int i = 0; i < points.size(); i++) {
            GeoPoint point = points.get(i);
            int signalStrength = signalStrengths.get(i);

            // Convert GeoPoint to screen coordinates
            org.osmdroid.api.IGeoPoint geoPoint = point;
            android.graphics.Point screenPoint = new android.graphics.Point();
            mapView.getProjection().toPixels(geoPoint, screenPoint);

            // Calculate color and radius based on signal strength
            int color = signalToColor(signalStrength);
            float radius = signalToRadius(signalStrength);

            // Create gradient for the heatmap effect
            RadialGradient gradient = new RadialGradient(
                    screenPoint.x, screenPoint.y, radius,
                    new int[]{color, Color.TRANSPARENT},
                    new float[]{0f, 1f},
                    Shader.TileMode.CLAMP
            );

            heatmapPaint.setShader(gradient);
            canvas.drawCircle(screenPoint.x, screenPoint.y, radius, heatmapPaint);
        }
    }

    private int signalToColor(int signalStrength) {
        if (signalStrength >= -50) return Color.argb(150, 0, 255, 0); // Excellent
        else if (signalStrength >= -60) return Color.argb(150, 255, 255, 0); // Good
        else if (signalStrength >= -70) return Color.argb(150, 255, 165, 0); // Fair
        else return Color.argb(150, 255, 0, 0); // Poor
    }

    private float signalToRadius(int signalStrength) {
        return Math.max(30, (100 + signalStrength) * 2); // Adjust radius based on signal strength
    }
}
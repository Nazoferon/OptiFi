package com.example.optifi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoverageMapView extends View {

    private List<WiFiPoint> wifiPoints;
    private Paint heatmapPaint;
    private Paint textPaint;
    private Paint gridPaint;
    private WiFiNetwork currentNetwork;
    private List<WiFiScanResult> scanResults = new ArrayList<>(); // Додано поле

    private double userLatitude = 0;
    private double userLongitude = 0;

    public static class WiFiPoint {
        float x, y;
        int signalStrength;

        WiFiPoint(float x, float y, int signalStrength) {
            this.x = x;
            this.y = y;
            this.signalStrength = signalStrength;
        }
    }

    public CoverageMapView(Context context) {
        super(context);
        init();
    }

    public CoverageMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        heatmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32);

        gridPaint = new Paint();
        gridPaint.setColor(Color.GRAY);
        gridPaint.setStrokeWidth(2);
        gridPaint.setAlpha(100);

        wifiPoints = new ArrayList<>();
        generateSampleData();
    }

    public void setNetwork(WiFiNetwork network, List<WiFiScanResult> scanResults) {
        this.currentNetwork = network;
        this.scanResults = scanResults;
        invalidate();
    }

    public void setUserLocation(double latitude, double longitude) {
        this.userLatitude = latitude;
        this.userLongitude = longitude;
        invalidate();
    }

    private void generateSampleData() {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            wifiPoints.add(new WiFiPoint(
                    random.nextFloat(),
                    random.nextFloat(),
                    -30 - random.nextInt(50)
            ));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw grid
        drawGrid(canvas, width, height);

        // Draw heat map points
        for (WiFiPoint point : wifiPoints) {
            drawHeatPoint(canvas, point, width, height);
        }

        // Draw scan results if available
        if (scanResults != null) {
            for (WiFiScanResult result : scanResults) {
                drawWiFiSignal(canvas, result.latitude, result.longitude, result.signalStrength);
            }
        }

        // Draw user position
        if (userLatitude != 0 && userLongitude != 0) {
            drawUserPosition(canvas);
        }

        // Draw legend and network info
        if (currentNetwork != null) {
            drawLegend(canvas, width, height);
            drawNetworkInfo(canvas, width);
        }
    }

    private void drawWiFiSignal(Canvas canvas, double lat, double lng, int signal) {
        float x = convertLongitudeToX(lng);
        float y = convertLatitudeToY(lat);
        int color = signalToColor(signal);
        float radius = signalToRadius(signal);

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }

    private void drawUserPosition(Canvas canvas) {
        float x = convertLongitudeToX(userLongitude);
        float y = convertLatitudeToY(userLatitude);

        Paint userPaint = new Paint();
        userPaint.setColor(Color.BLUE);
        userPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 20, userPaint);
    }

    // Решта методів залишаються без змін...
    private void drawGrid(Canvas canvas, int width, int height) {
        int gridSize = 50;
        for (int i = 0; i <= width; i += gridSize) {
            canvas.drawLine(i, 0, i, height, gridPaint);
        }
        for (int i = 0; i <= height; i += gridSize) {
            canvas.drawLine(0, i, width, i, gridPaint);
        }
    }

    private void drawHeatPoint(Canvas canvas, WiFiPoint point, int width, int height) {
        float x = point.x * width;
        float y = point.y * height;
        int color = signalToColor(point.signalStrength);
        float radius = signalToRadius(point.signalStrength);

        RadialGradient gradient = new RadialGradient(
                x, y, radius,
                new int[]{color, Color.TRANSPARENT},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        );

        heatmapPaint.setShader(gradient);
        canvas.drawCircle(x, y, radius, heatmapPaint);
    }

    private int signalToColor(int signalStrength) {
        if (signalStrength >= -50) return Color.argb(150, 0, 255, 0);
        else if (signalStrength >= -60) return Color.argb(150, 255, 255, 0);
        else if (signalStrength >= -70) return Color.argb(150, 255, 165, 0);
        else return Color.argb(150, 255, 0, 0);
    }

    private float signalToRadius(int signalStrength) {
        return Math.max(30, (100 + signalStrength) * 2);
    }

    private float convertLongitudeToX(double longitude) {
        return (float) (getWidth() * (longitude + 180) / 360);
    }

    private float convertLatitudeToY(double latitude) {
        return (float) (getHeight() * (90 - latitude) / 180);
    }

    private void drawLegend(Canvas canvas, int width, int height) {
        int legendX = width - 200;
        int legendY = 50;
        int legendSpacing = 40;

        textPaint.setTextSize(24);
        textPaint.setColor(Color.BLACK);

        canvas.drawText("Якість сигналу:", legendX, legendY, textPaint);

        // Excellent
        heatmapPaint.setShader(null);
        heatmapPaint.setColor(Color.GREEN);
        canvas.drawCircle(legendX, legendY + legendSpacing, 10, heatmapPaint);
        canvas.drawText("Відмінно", legendX + 20, legendY + legendSpacing + 5, textPaint);

        // Good
        heatmapPaint.setColor(Color.YELLOW);
        canvas.drawCircle(legendX, legendY + 2 * legendSpacing, 10, heatmapPaint);
        canvas.drawText("Добре", legendX + 20, legendY + 2 * legendSpacing + 5, textPaint);

        // Fair
        heatmapPaint.setColor(Color.rgb(255, 165, 0));
        canvas.drawCircle(legendX, legendY + 3 * legendSpacing, 10, heatmapPaint);
        canvas.drawText("Задовільно", legendX + 20, legendY + 3 * legendSpacing + 5, textPaint);

        // Poor
        heatmapPaint.setColor(Color.rgb(255,0,0));
        canvas.drawCircle(legendX, legendY + 4 * legendSpacing, 10, heatmapPaint);
        canvas.drawText("Погано", legendX + 20, legendY + 4 * legendSpacing + 5, textPaint);
    }

    private void drawNetworkInfo(Canvas canvas, int width) {
        textPaint.setTextSize(28);
        textPaint.setColor(Color.BLACK);

        String info = currentNetwork.getSsid() + " - " +
                currentNetwork.getSignalLevel() + " dBm (" +
                currentNetwork.getQuality().getUkrainianName() + ")";

        canvas.drawText(info, 20, 30, textPaint);
    }
}
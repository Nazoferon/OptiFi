package com.example.optifi;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private TextView recommendationsText;
    private WiFiNetwork network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        network = getIntent().getParcelableExtra("network");
        if (network == null) {
            finish();
            return;
        }

        recommendationsText = findViewById(R.id.recommendationsText);
        generateRecommendations();
    }

    private void generateRecommendations() {
        List<String> recommendations = new ArrayList<>();

        // Signal strength based recommendations
        switch (network.getQuality()) {
            case POOR:
                recommendations.add("🔴 КРИТИЧНО: Дуже слабкий сигнал");
                recommendations.add("• Наблизьтеся до роутера");
                recommendations.add("• Встановіть Wi-Fi репітер або мешеву систему");
                recommendations.add("• Перевірте перешкоди між пристроєм та роутером");
                // Додаємо специфічні рекомендації для слабкого сигналу
                recommendations.add("🔴 Розмістіть репітер у зоні з найгіршим сигналом");
                recommendations.add("📍 Оптимальне місце: ~5 метрів від роутера");
                recommendations.add("🔵 Встановіть репітер між роутером і зоною без сигналу");
                break;

            case FAIR:
                recommendations.add("🟡 УВАГА: Слабкий сигнал");
                recommendations.add("• Розгляньте можливість встановлення репітера");
                recommendations.add("• Уникайте товстих стін та металевих предметів");
                break;

            case GOOD:
                recommendations.add("🟢 Добрий сигнал");
                recommendations.add("• Сигнал достатній для більшості завдань");
                break;

            case EXCELLENT:
                recommendations.add("🟢 Відмінний сигнал!");
                recommendations.add("• Оптимальне розташування для цієї мережі");
                break;
        }

        // Frequency band recommendations
        if (network.getFrequencyBand().equals("2.4GHz")) {
            recommendations.add("\n📡 Частотний діапазон 2.4GHz:");
            recommendations.add("• Більший радіус дії, але менша швидкість");
            recommendations.add("• Може мати завади від інших пристроїв");
            recommendations.add("• Рекомендується для IoT пристроїв");
        } else {
            recommendations.add("\n📡 Частотний діапазон 5GHz:");
            recommendations.add("• Вища швидкість, менше завад");
            recommendations.add("• Менший радіус дії");
            recommendations.add("• Ідеально для потокового відео та ігор");
        }

        // Security recommendations
        if (network.getCapabilities().contains("WEP")) {
            recommendations.add("\n🔒 БЕЗПЕКА: Застарілий протокол WEP");
            recommendations.add("• Рекомендується оновити до WPA2/WPA3");
        } else if (network.getCapabilities().contains("WPA")) {
            recommendations.add("\n🔒 Безпека: Сучасне шифрування");
        }

        // General optimization tips
        recommendations.add("\n💡 Загальні рекомендації:");
        recommendations.add("• Розмістіть роутер на висоті 1-2 метри");
        recommendations.add("• Уникайте закритих шаф та ніш");
        recommendations.add("• Регулярно оновлюйте прошивку роутера");
        recommendations.add("• Використовуйте менш завантажені канали");

        StringBuilder fullText = new StringBuilder();
        for (String recommendation : recommendations) {
            fullText.append(recommendation).append("\n");
        }

        recommendationsText.setText(fullText.toString());
    }
}
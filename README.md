# OptiFi 📡

**OptiFi** — це Android-додаток для створення теплової карти покриття Wi-Fi мереж. Додаток дозволяє сканувати Wi-Fi мережі, відображати силу сигналу на карті за допомогою OpenStreetMap і надавати рекомендації для покращення з’єднання. Розроблено з урахуванням адаптивності для різних пристроїв, включаючи сучасні смартфони, такі як Samsung A55.

**Автор**: BunnyB\Nazoferon  
**Дата створення**: 21 липня 2025

![OptiFi Logo](app/src/main/res/mipmap/ic_launcher.png)

## 🚀 Особливості
- **Сканування Wi-Fi**: Автоматичне сканування доступних Wi-Fi мереж із визначенням сили сигналу.
- **Теплова карта**: Візуалізація покриття Wi-Fi на карті з кольоровим кодуванням (червоний — слабкий, жовтий — задовільний, зелений — відмінний).
- **Відстеження локації**: Використання GPS для відображення поточної позиції користувача з кастомною іконкою.
- **Адаптивний дизайн**: Інтерфейс, оптимізований для різних розмірів екранів і вирізів (наприклад, на Samsung A55).
- **Інтуїтивний інтерфейс**: Зручна навігація з інструкціями та статусом сканування.

## 📸 Скріншоти
| Теплова карта Wi-Fi | Список мереж |
|---------------------|--------------|
| ![Wi-Fi Heatmap](docs/screenshots/heatmap.png) | ![Network List](docs/screenshots/network_list.png) |

## 🛠 Встановлення
1. **Склонуй репозиторій**:
   ```bash
   git clone https://github.com/Nazoferon/OptiFi.git

Відкрий проєкт у Android Studio:
Запусти Android Studio.
Вибери File > Open і вкажи папку OptiFi.


Налаштуй SDK:
Додай шлях до Android SDK у файл local.properties:sdk.dir=/шлях/до/твого/Android/Sdk




Збери та запусти:
Натисни Build > Rebuild Project.
Вибери пристрій або емулятор і натисни Run.



📋 Вимоги

Android: 5.0 (Lollipop, API 21) або новіший.
Дозволи:
ACCESS_FINE_LOCATION (для GPS і Wi-Fi сканування).
ACCESS_WIFI_STATE, CHANGE_WIFI_STATE (для сканування мереж).
INTERNET, ACCESS_NETWORK_STATE, WRITE_EXTERNAL_STORAGE (для OpenStreetMap).


Обладнання: Пристрій із підтримкою Wi-Fi та GPS.

🔧 Залежності
У файлі app/build.gradle вказані такі залежності:
implementation 'org.osmdroid:osmdroid-android:6.1.10'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'com.google.android.gms:play-services-location:21.0.1'

🖥 Використання

Запусти додаток на пристрої.
Дозволь доступ до локації та Wi-Fi.
Вибери Wi-Fi мережу зі списку в MainActivity.
У CoverageMapActivity переглянь теплову карту покриття:
🔴 Червоний: слабкий сигнал (< -70 dBm).
🟡 Жовтий: задовільний сигнал (-70 до -60 dBm).
🟢 Зелений: відмінний сигнал (≥ -60 dBm).


Використовуй рекомендації в RecommendationsActivity для покращення сигналу.

🗂 Структура проєкту

app/src/main/java/com/example/optifi/:
MainActivity.java: Головна активність зі списком Wi-Fi мереж.
CoverageMapActivity.java: Відображає теплову карту з OpenStreetMap.
NetworkDetailActivity.java: Деталі обраної мережі.
RecommendationsActivity.java: Рекомендації для покращення сигналу.
WifiScanner.java: Логіка сканування Wi-Fi.
HeatMapOverlay.java: Накладання теплової карти на OpenStreetMap.


app/src/main/res/:
layout/: XML-лейаути (activity_coverage_map.xml, activity_main.xml тощо).
drawable/: Ресурси, наприклад, gradient_background.xml, ic_user_location.xml.
mipmap/: Іконки застосунку (ic_launcher.png, ic_launcher_foreground.png тощо).



🤝 Як внести вклад

Форкни репозиторій.
Створи нову гілку: git checkout -b feature/твоя_фічa.
Внеси зміни та закоміть: git commit -m "Опис змін".
Відправ зміни: git push origin feature/твоя_фічa.
Створи Pull Request на GitHub.

📜 Ліцензія
Цей проєкт ліцензований за MIT License.
🙌 Подяка

OSMDroid: Бібліотека для роботи з картами.
Google Play Services: Для відстеження локації.
Тобі, користувачу, за інтерес до OptiFi!

📞 Контакти

Автор: BunnyB\Nazoferon
GitHub: Nazoferon
Email: (додай свій email, якщо бажаєш)

Останнє оновлення: 21 липня 2025

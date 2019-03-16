package com.example.parkpay;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

public class ParkPay extends Application {
    public static final String API_key="196f035d-dc5f-4d26-a1e4-61c1bb63e0a0";
    @Override
    public void onCreate() {
        super.onCreate();
        // Создание расширенной конфигурации библиотеки.
        YandexMetricaConfig config = YandexMetricaConfig
                .newConfigBuilder(API_key)
                .build();
        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking(this);
        YandexMetricaPush.init(getApplicationContext());
    }
}

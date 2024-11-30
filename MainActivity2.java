package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private EditText etCity;
    private TextView tvResult;
    private Button btnGet, btnCredits;

    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "2c75bda8ab09d8e52c296369375bea06";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        etCity = findViewById(R.id.etCity);
        tvResult = findViewById(R.id.tvResult);
        btnGet = findViewById(R.id.btnGet);
        btnCredits = findViewById(R.id.btnCredits);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeatherDetails(v);
            }
        });

        btnCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCredits(v);
            }
        });
    }

    public void getWeatherDetails(View view) {
        String city = etCity.getText().toString().trim();
        if (city.isEmpty()) {
            tvResult.setText("Voer een geldige stadsnaam in.");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = getWeatherData(city);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = "Weer in " + city + ":\n";
                                result += "Temperatuur: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C\n";
                                result += "Vochtigheid: " + jsonObject.getJSONObject("main").getInt("humidity") + "%\n";
                                result += "Weer: " + translateWeatherDescription(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
                                tvResult.setText(result);
                            } catch (JSONException e) {
                                tvResult.setText("Fout bij het verwerken van de gegevens.");
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("Fout bij het ophalen van de gegevens.");
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getWeatherData(String city) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(this.url + "?q=" + city + "&appid=" + appid + "&units=metric");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        }
        return result.toString();
    }

    private String translateWeatherDescription(String description) {
        Map<String, String> translations = new HashMap<>();
        translations.put("clear sky", "onbewolkte lucht");
        translations.put("few clouds", "lichte bewolking");
        translations.put("scattered clouds", "verspreide bewolking");
        translations.put("broken clouds", "gebroken bewolking");
        translations.put("shower rain", "regenbui");
        translations.put("rain", "regen");
        translations.put("thunderstorm", "onweer");
        translations.put("snow", "sneeuw");
        translations.put("mist", "mist");

        String translatedDescription = translations.get(description.toLowerCase());
        return translatedDescription != null ? translatedDescription : description;
    }

    public void showCredits(View view) {
        // Open de credits activity
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }
}


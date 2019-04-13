package com.example.weathernow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /*
    Константа WEATHER_URL для получения доступа к API OpenWeatherMap с параметрами
    q - город или почтовый индекс, appid - ключ доступа приложения к API, lang - язык,
    utils - СИ(система измерения, в данном случае метрическая системама измерения) - градусы Цельсия.
    Название города или почтовый индекс города вставляется с помощью метода String.format - %s(параметр).
    */
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=3010d750c9dd62858b40b947630996d9&lang=ru&units=metric";

    /*
    Ссылки на EditText и TextView для получения доступа к ним.
    */
    private EditText editTextCity;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_weather);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
    }

    /*
    Метод(кнопка: "Показать погоду") для вызова предопределенного метода DownloadWeatherTask.
    Переменная city с присвоенным полем для ввода editTextCity в строчном формате(toString)
    и без пробелов(trim).
    Если перменная city не пустая, то происходит создание объекта класса DownloadWeatherTask task c переданными
    параметрами WEATHER_URL - url-запрос и city - название города или его индекс, вызов метода execute.
    */
    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }

    /*
    Внутренний класс DownloadWeatherTask для загрузки данных из API, наследуемый от типизированного
    AsyncTask c типами String, Void, String. Принимает String - строку url-запроса, возвращает String - строку JSON объекта.
    */
    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {
        /*
        Предопределенный метод doInBackground для отправки URL запроса и получения ответа.
        */
        @Override
        protected String doInBackground(String... strings) {
            /*
            Создание объекта класса URL url, объекта класса HttpURLConnection urlConnection и объекта
            класса StringBuilder result для строки url-запроса, http протокола соединения и
            результата запроса - result соответственно.
            */
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            /*
            Передача url строки с адресом WEATHER_URL и открытие соединения.
            Получение потока ввода через соединение с помощью InputStream.
            Чтение данных из потока с помощью InputStreamReader и преобразование данных в строку
            с помощью BufferedReader.
            Чтение данных из reader с помощью метода readLine.
            */
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                /*
                До тех пор пока line не равно null, вставка данных в result и чтение следующей строки.
                */
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                /*
                Возвращение result, приведенного в строковый тип данных.
                */
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                /*
                Закрытие соединения, если оно не равно null.
                */
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
        /*
        Предопределенный метод onPostExecute, в который передается результат
        метода doInBackground.
        Служит для получения TextView, полученных данных из метода doInBackground.
        */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            /*
            Toast для вывода всплывающего сообщения.
            */
            Toast cityToast = Toast.makeText(getApplicationContext(), "Город введен неверно, попробуйте ввести на английском языке", Toast.LENGTH_LONG);
            cityToast.setGravity(Gravity.CENTER, 0, 0);
            try {
                JSONObject jsonObject = null;
                String city = "";
                String temp = "";
                String pressure = "";
                String humidity = "";
                String speed = "";
                String deg = "";
                String visibility = "";
                String description = "";
                String description1 = "";
                String weather = "";
                /*
                Обработка исключения, связанного с тем, что метод doInBackground может вернуть null.
                */
                try {
                    jsonObject = new JSONObject(s);
                } catch (NullPointerException e) {
                    cityToast.show();
                }
                if (jsonObject != null) {
                    /*
                    Получение JSON объектов из JSON строки result.
                    Получение данных из JSON объекта:
                    - вызов метода getString по ключу строкового объекта name;
                    - вызов метода getJSONObject по ключу JSON объекта main и вызов метода getString по ключу
                    строкового объекта temp;
                    - вызов метода getJSONObject по ключу JSON объекта main и вызов метода getString по ключу
                    строкового объекта pressure;
                    - вызов метода getJSONObject по ключу JSON объекта wind и вызов метода getString по ключу
                    строкового объекта speed;
                    - вызов метода getJSONObject по ключу JSON объекта wind и вызов метода getString по ключу
                    строкового объекта deg;
                    - вызов метода getJSONArray по ключу JSON массива weather, вызов метода getJSONObject по ключу JSON
                    объекта с индексом 0 и вызов getString по ключу строкового объекта description.
                    */
                    city = jsonObject.getString("name");
                    temp = jsonObject.getJSONObject("main").getString("temp") + " ℃";
                    pressure = jsonObject.getJSONObject("main").getString("pressure");
                    humidity = jsonObject.getJSONObject("main").getString("humidity") + " %";
                    speed = jsonObject.getJSONObject("wind").getString("speed") + " м/с";
                    deg = jsonObject.getJSONObject("wind").getString("deg");
                    /*
                    Обработка исключения JSON, связанная с возможным отсутствием видимости. Вызов метода getString
                    по ключу строкового объекта visibility.
                    */
                    try {
                        visibility = jsonObject.getString("visibility") + " м";
                    } catch (JSONException e) {
                        visibility = "";
                    }
                    description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    /*
                    Обработка исключения JSON, связанная с возможным отсутствием второго описания погоды. Вызов метода getJSONArray
                    по ключу JSON массива weather, вызов метода getJSONObject по ключу JSON объекта с индексом 1 и вызов getString
                    по ключу строкового объекта description.
                    */
                    try {
                        description1 = jsonObject.getJSONArray("weather").getJSONObject(1).getString("description");
                    } catch (JSONException e) {
                        description1 = null;
                    }
                    /*
                    Перевод из гектопаскалей(миллибаров) в мм ртутного столба.
                    */
                    double pressureDouble = 0;
                    int pressureInt = 0;
                    try {
                        pressureDouble = Double.parseDouble(pressure);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    pressureDouble = Math.round(pressureDouble / 1.333);
                    pressureInt = (int) pressureDouble;
                    pressure = Integer.toString(pressureInt) + " мм";
                    /*
                    Проверка, полученных данных из строкового объекта deg.
                    Если deg больше 0° и меньше 90°, то ветер северо-восточный.
                    Если deg больше 90° и меньше 180°, то ветер юго-восточный.
                    Если deg больше 180° и меньше 270°, то ветер юго-западный.
                    Если deg больше 270° и меньше 360°, то ветер северо-западный.
                    Если deg равен 360°, то ветер северный.
                    Если deg равен 90°, то ветер восточный.
                    Если deg равен 180°, то ветер южный.
                    Если deg равен 270°, то ветер западный.
                    Формирование строки из полученных данных с помощью String.format.
                    */
                    if (description1 != null) {
                        if (Double.parseDouble(deg) > 0 && Double.parseDouble(deg) < 90) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северо-восточный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) > 90 && Double.parseDouble(deg) < 180) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, юго-восточный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) > 180 && Double.parseDouble(deg) < 270) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, юго-западный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) > 270 && Double.parseDouble(deg) < 360) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северо-западный\\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) == 360) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) == 90) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, восточный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) == 180) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, южный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        } else if (Double.parseDouble(deg) == 270) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, западный\nВидимость: %s\nНа улице: %s, %s", temp, pressure, humidity, speed, visibility, description, description1);
                        }
                    } else {
                        if (Double.parseDouble(deg) > 0 && Double.parseDouble(deg) < 90) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северо-восточный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) > 90 && Double.parseDouble(deg) < 180) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, юго-восточный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) > 180 && Double.parseDouble(deg) < 270) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, юго-западный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) > 270 && Double.parseDouble(deg) < 360) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северо-западный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) == 360) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, северный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) == 90) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, восточный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) == 180) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, южный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        } else if (Double.parseDouble(deg) == 270) {
                            weather = String.format("\nТемпература: %s\nАтмосферное давление: %s\nВлажность: %s\nСкорость ветра: %s, западный\nВидимость: %s\nНа улице: %s", temp, pressure, humidity, speed, visibility, description);
                        }
                    }
                }
                /*
                Запись weather в TextView.
                */
                textViewWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

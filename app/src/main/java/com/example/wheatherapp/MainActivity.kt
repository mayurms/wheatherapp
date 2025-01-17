package com.example.wheatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wheatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("Chandrapur")
        searchCity()
    }
    private fun searchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiInterface::class.java)

        val respose =
            retrofit.getWeatherData(cityName, "3876c6723cbd3ef9465ec66e9d074135", "metric")
        respose.enqueue(object : Callback<WheatherApi> {
            override fun onResponse(call: Call<WheatherApi>, response: Response<WheatherApi>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temp = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = "$temp°C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max : $maxTemp°C"
                    binding.minTemp.text = "Min : $minTemp°C"
                    binding.humidity.text = "$humidity"
                    binding.sea.text = "$seaLevel"
                    binding.wind.text = "$windSpeed"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"


//                    Log.d("TAG", "onResponse: $temp")

                    changeImageAccordingToWeatherCondition(condition)
                }

            }

            override fun onFailure(call: Call<WheatherApi>, t: Throwable) {

            }

        })
    }
    private fun changeImageAccordingToWeatherCondition(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }


            "Light Rain", "Drizzle","Rain", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }
}
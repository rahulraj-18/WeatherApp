package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ConditionVariable
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

//b1482dc2c5b39b2e121f7d4b3cff7d56
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Hyderabad")
        Searchcity()
    }

    private fun Searchcity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchWeatherData(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityyName:String) {
       val retrofit= Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(apiInterface::class.java)
        val response =retrofit.getWeatherData(cityyName,"b1482dc2c5b39b2e121f7d4b3cff7d56","metric")
        response.enqueue(object:Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody= response.body()
                if(response.isSuccessful && responseBody!=null)
                {
                    val temperature= responseBody.main.temp.toString()
                    val humidity =responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition= responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp= responseBody.main.temp_min
                    binding.temp.text="$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="$maxTemp °C"
                    binding.minTemp.text="$minTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityyName"
                    changeImagesAccordingToWeather(condition)

                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImagesAccordingToWeather(condition: String) {
        when(condition)
        {
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","OverCast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Ran","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->
            {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timeStamp:Long):String
    {
        val sdf=SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timeStamp:Long):String
    {
        val sdf=SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timeStamp*1000))
    }
    fun date():String
    {
        val sdf=SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }
}

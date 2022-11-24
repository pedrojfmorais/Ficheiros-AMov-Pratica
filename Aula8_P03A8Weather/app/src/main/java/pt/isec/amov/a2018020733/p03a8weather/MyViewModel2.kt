package pt.isec.amov.a2018020733.p03a8weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast.json?")
    fun getWeatherInfo(
        @Query("key") api_key : String,
        @Query("q") city : String,
        @Query("days") days : Int
    ) : Call<ResponseBody>
}

class MyViewModel2 : ViewModel() {
    companion object {
        private const val TAG ="ViewModel2"
        private const val API_KEY = "95fc0d46b3914352a5c214419221611"
        private const val BASE_URL = "https://api.weatherapi.com/"
    }

    private val _location = MutableLiveData<String?>(null)
    val location : LiveData<String?>
        get() = _location

    private val _icon = MutableLiveData<String?>(null)
    val icon : LiveData<String?>
        get() = _icon

    private val _currentTemp = MutableLiveData<Double>(0.0)
    val currentTemp : LiveData<Double>
        get() = _currentTemp

    private val _webContent = MutableLiveData<String?>(null)
    val webContent : LiveData<String?>
        get()=_webContent

    fun getContent(city : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val result = weatherService.getWeatherInfo(API_KEY,city,4)
        result.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i(TAG, "onSuccess: OK!!!!!!")
                val content = response.body()!!.string()
                _webContent.postValue(content)
                procInfo(content)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i(TAG, "onFailure: ERRO!!!!")
                _webContent.postValue("ERRROOOOOOO")
            }

        })

    }

    private fun procInfo(content : String) {
        val json = JSONObject(content)

        json.getJSONObject("location").let {
            _location.postValue("${it["name"]}, ${it["country"]}")
        }

        json.getJSONObject("current").let {
            _currentTemp.postValue(it.getDouble("temp_c"))
            it.getJSONObject("condition").let {
                _icon.postValue("https:${it["icon"]}")
            }
        }
    }
}
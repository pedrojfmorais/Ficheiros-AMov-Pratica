package pt.isec.amov.a2018020733.p03a8weather

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject
import pt.isec.amov.a2018020733.p03a8weather.databinding.ActivityMainBinding

class MyViewModel : ViewModel() {
    private val _webContent = MutableLiveData<String?>(null)
    val webContent : LiveData<String?>
        get()=_webContent

    fun getContent(strURL : String) {
        NetUtils.getDataAsync(strURL, _webContent)
    }
}

class MainActivity : AppCompatActivity() {

    companion object {
        private const val WEATHERAPI_KEY = "a733720d9f4f45869c1164108221711"
        private const val CITY = "Coimbra"
        private const val WEATHER_URL = "https://api.weatherapi.com/v1/forecast.json?" +
                "key=$WEATHERAPI_KEY&q=$CITY&days=10&aqi=no&alerts=no"
    }

    private val viewModel : MyViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            viewModel.webContent.observe(this) {
                it?.run {
                    val json = JSONObject(it)

                    val location = json.getJSONObject("location")
                    val name = location.getString("name")
                    val name2 = location["name"]

                    println(json.toString(4))

                    val asd = JSONObject()
                    asd.put("name", "nome")
                    asd.put("age", 22)
                    println(asd.toString())
                }
            }
            viewModel.getContent(WEATHER_URL)
        }

        if (!NetUtils.verifyNetworkStateV3(this)) {
            Toast.makeText(this,"No network available",Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
package pt.isec.ans.amov_gps_maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pt.isec.ans.amov_gps_maps.databinding.ActivityMainBinding

//SHA1: 6C:AA:6D:DA:09:BD:84:AE:05:D0:57:95:F0:DF:DD:D6:0A:FC:96:45
//API Key: AIzaSyDWKUXtRqCQAxvsjV6YoDJXT3OL2muuKDU

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private val ISEC = LatLng(40.1925, -8.4115)
        private val DEIS = LatLng(40.1925, -8.4128)
    }

    /*private val locationManager: LocationManager by lazy {
        getSystemService(LOCATION_SERVICE) as LocationManager
    }
*/
    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var coarseLocationPermission = false
    private var fineLocationPermission = false
    private var backgroundLocationPermission = false
    private var locationEnabled = false

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verifyPermissions()

        (supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun verifyPermissions() {
        coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else
            backgroundLocationPermission = coarseLocationPermission || fineLocationPermission

        if (!coarseLocationPermission && !fineLocationPermission) {
            basicPermissionsAuthorization.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else
            verifyBackgroundPermission()
    }

    private val basicPermissionsAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        coarseLocationPermission = results[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        fineLocationPermission = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        startLocationUpdates()
        verifyBackgroundPermission()
    }

    private fun verifyBackgroundPermission() {
        if (!(coarseLocationPermission || fineLocationPermission))
            return

        if (!backgroundLocationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            ) {
                val dlg = AlertDialog.Builder(this)
                    .setTitle("Background Location")
                    .setMessage(
                        "This application needs your permission to use location while in the background.\n" +
                        "Please choose the correct option in the following screen" +
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            " (\"${packageManager.backgroundPermissionOptionLabel}\")."
                        else
                            "."
                    )
                    .setPositiveButton("Ok") { _, _ ->
                        backgroundPermissionAuthorization.launch(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
                    }
                    .create()
                dlg.show()
            }
        }
    }

    private val backgroundPermissionAuthorization = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        backgroundLocationPermission = result
        Toast.makeText(this,"Background location enabled: $result",Toast.LENGTH_LONG).show()
    }


    private var currentLocation = Location(null)
        set(value) {
            field = value
            binding.tvLat.text = String.format("%10.5f", value.latitude)
            binding.tvLon.text = String.format("%10.5f", value.longitude)
        }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (locationEnabled || !(coarseLocationPermission || fineLocationPermission))
            return

        /*
        currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            ?: Location(null).apply {
                latitude = 40.1925
                longitude = -8.4128
            }

        locationManager.requestLocationUpdates(
            if (fineLocationPermission) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER,
            1000,
            100f,
            locationListener
        )
        */

        currentLocation = Location(null).apply {
                latitude = 40.1925
                longitude = -8.4128
            }

        locationProvider.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener
                currentLocation = location
            }

        val locationRequest =
            LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 2000)
//.setMinUpdateDistanceMeters(100f)
//.setMinUpdateIntervalMillis(1000)
//.setMaxUpdateDelayMillis(5000)
//.setPriority(PRIORITY_HIGH_ACCURACY)
//.setIntervalMillis(2000)
//.setMaxUpdates(10)
                .build()

        locationProvider.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

        locationEnabled = true
    }

//    private val locationListener = LocationListenerCompat { location ->
//        currentLocation = location
//    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.forEach { location ->
                Log.i("TAG", "onLocationResult: $location")
                currentLocation = location
            }
        }
    }

    private fun stopLocationUpdates() {
        if (!locationEnabled)
            return
//        locationManager.removeUpdates(locationListener)
        locationProvider.removeLocationUpdates(locationCallback)
        locationEnabled = false
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        val cp = CameraPosition.Builder().target(ISEC).zoom(17f)
            .bearing(0f).tilt(0f).build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp))
        map.addCircle(CircleOptions().center(ISEC).radius(150.0)
            .fillColor(Color.argb(128, 128, 128, 128))
            .strokeColor(Color.rgb(128, 0, 0)).strokeWidth(4f))
        val mo = MarkerOptions().position(ISEC).title("ISEC-IPC")
            .snippet("Instituto Superior de Engenharia de Coimbra")
        val isec = map.addMarker(mo)
        isec?.showInfoWindow()
        map.addMarker(MarkerOptions().position(DEIS).title("DEIS-ISEC"))
    }
}
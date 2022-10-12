package pt.isec.amov.a2018020733.aula2

import android.app.Application
import android.content.res.Configuration
import android.util.Log

class MyApp : Application() {

    private var _my_value = 0
    val my_value : Int
        get() = ++_my_value

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "App.onCreate: $my_value")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.i(TAG, "App.onLowMemory: ")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.i(TAG, "App.onTrimMemory: ")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.i(TAG, "App.onConfigurationChanged: ")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "App.onTerminate: ")
    }
}